package com.xlg.component.nio;

import static com.xlg.component.nio.TestCommon.BUFFER_EXPRESSION_FACTOR;
import static com.xlg.component.nio.TestCommon.LOCAL_HOST;
import static com.xlg.component.nio.TestCommon.SPLIT_STR;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.xlg.component.ks.utils.ByteBufferUtil;

/**
 * @author wangqingwei
 * Created on 2022-05-29
 */
public class NetTest {

    private static final Logger logger = LoggerFactory.getLogger(NetTest.class);
    private static final int BUFFER_CAPACITY = 16;
    private static final int SERVER_PORT = 8082;


    @Test
    public void blockingServer() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

        try (ServerSocketChannel server = ServerSocketChannel.open()) {

            server.bind(new InetSocketAddress(SERVER_PORT));

            // 存放链接的集合
            List<SocketChannel> clientChannelList = Lists.newArrayList();
            while (true) {
                logger.info("before connecting...");
                // 没有连接时，会阻塞线程
                SocketChannel socketChannel = server.accept();
                logger.info("after connecting...");
                clientChannelList.add(socketChannel);

                for (SocketChannel channel : clientChannelList) {
                    logger.info("before reading...");
                    // 处理通道中的数据
                    // 当通道中没有数据可读时，会阻塞线程
                    channel.read(buffer);
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    logger.info("after reading...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void blockingClient() {
        try (final SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(LOCAL_HOST, SERVER_PORT));
            logger.info("waiting...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSelectorAccept() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(SERVER_PORT));

            Selector selector = Selector.open();

            // 通道必须设置为非阻塞模式
            server.configureBlocking(false);
            // 将通道注册到选择器中，并设置感兴趣的事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转
                // 就绪事件数量
                final int readyEvents = selector.select();
                System.out.println("selector ready counts : " + readyEvents);

                final Set<SelectionKey> selectionKeys = selector.keys();

                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();

                    // 判断key的类型
                    if (key.isAcceptable()) {
                        // 获得key对应的channel
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        System.out.println("before accepting...");

                        // 获取连接并处理，而且是必须处理，否则需要取消
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("after accepting...");

                        // 处理完毕后移除
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSelectorAcceptRead() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(SERVER_PORT));

            Selector selector = Selector.open();

            // 通道必须设置为非阻塞模式
            server.configureBlocking(false);
            // 将通道注册到选择器中，并设置感兴趣的事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转
                // 就绪事件数量
                final int readyEvents = selector.select();
                System.out.println("selector ready counts : " + readyEvents);

                final Set<SelectionKey> selectionKeys = selector.keys();

                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();

                    // 判断key的类型
                    if (key.isAcceptable()) {
                        // 获得key对应的channel
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        System.out.println("before accepting...");
                        // 获取连接并处理，而且是必须处理，否则需要取消
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("after accepting...");

                        // 设置为非阻塞模式，同时将连接的通道也注册到选择其中
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        // 处理完毕后移除
                        iterator.remove();
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.channel();
                        logger.info("before reading...");
                        channel.read(buffer);
                        logger.info("after reading...");

                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();

                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 升级后的考虑断开、数据半包粘包(split)
     */
    @Test
    public void testSelectorAcceptReadUpgrade() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(SERVER_PORT));

            Selector selector = Selector.open();

            // 通道必须设置为非阻塞模式
            server.configureBlocking(false);
            // 将通道注册到选择器中，并设置感兴趣的事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转
                // 就绪事件数量
                final int readyEvents = selector.select();
                System.out.println("selector ready counts : " + readyEvents);

                final Set<SelectionKey> selectionKeys = selector.keys();

                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();

                    // 判断key的类型
                    if (key.isAcceptable()) {
                        // 获得key对应的channel
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        System.out.println("before accepting...");
                        // 获取连接并处理，而且是必须处理，否则需要取消
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("after accepting...");

                        // 设置为非阻塞模式，同时将连接的通道也注册到选择其中
                        socketChannel.configureBlocking(false);
                        ByteBuffer curChannelBuffer = ByteBuffer.allocate(BUFFER_CAPACITY);
                        // 每个通道发生读事件时都使用自己的通道
                        socketChannel.register(selector, SelectionKey.OP_READ, curChannelBuffer);

                        // 处理完毕后移除
                        iterator.remove();
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.channel();
                        // 通过key获得附件（buffer）
                        ByteBuffer curBuffer = (ByteBuffer)key.attachment();
                        logger.info("before reading...");
                        final int read = channel.read(curBuffer);
                        if (read == -1) {
                            key.channel();
                            channel.close();
                        } else {
                            // 通过分隔符来分隔buffer中的数据
                            ByteBufferTest bufferTest = new ByteBufferTest();
                            bufferTest.stickPckSplit(curBuffer, SPLIT_STR);
                            // buffer太小了
                            if (curBuffer.position() == curBuffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(curBuffer.capacity() * BUFFER_EXPRESSION_FACTOR);
                                curBuffer.flip();
                                newBuffer.put(curBuffer);
                                // 将新buffer放到key中作为附件
                                key.attach(newBuffer);
                            }
                        }
                        logger.info("after reading...");
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 升级后的考虑断开、数据半包粘包(split), write写事件
     * 因为通道的容量可能小于 buffer的大小, 一次处理不完
     */
    @Test
    public void testSelectorAcceptReadUpgradeAndWrite() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(SERVER_PORT));

            Selector selector = Selector.open();

            // 通道必须设置为非阻塞模式
            server.configureBlocking(false);
            // 将通道注册到选择器中，并设置感兴趣的事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转
                // 就绪事件数量
                final int readyEvents = selector.select();
                System.out.println("selector ready counts : " + readyEvents);

                final Set<SelectionKey> selectionKeys = selector.keys();

                final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();

                    // 判断key的类型
                    if (key.isAcceptable()) {
                        // 获得key对应的channel
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        System.out.println("before accepting...");
                        // 获取连接并处理，而且是必须处理，否则需要取消
                        SocketChannel socketChannel = channel.accept();
                        System.out.println("after accepting...");

                        // 写入data
                        StringBuilder dataBuilder = new StringBuilder();
                        Stream.iterate(0, s -> s + 1).limit(500000000).forEach(dataBuilder::append);

                        ByteBuffer buffer = StandardCharsets.UTF_8.encode(dataBuilder.toString());
                        // 先执行一次Buffer->Channel的写入，如果未写完，就添加一个可写事件
                        int write = socketChannel.write(buffer);
                        System.out.println(write);
                        // 通道中可能无法放入缓冲区中的所有数据
                        if (buffer.hasRemaining()) {
                            // 注册到Selector中，关注可写事件，并将buffer添加到key的附件中
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
                        }
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.channel();
                        // 通过key获得附件（buffer）
                        ByteBuffer curBuffer = (ByteBuffer)key.attachment();
                        logger.info("before reading...");
                        final int read = channel.read(curBuffer);
                        if (read == -1) {
                            key.channel();
                            channel.close();
                        } else {
                            // 通过分隔符来分隔buffer中的数据
                            ByteBufferTest bufferTest = new ByteBufferTest();
                            bufferTest.stickPckSplit(curBuffer, SPLIT_STR);
                            // buffer太小了
                            if (curBuffer.position() == curBuffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(curBuffer.capacity() * BUFFER_EXPRESSION_FACTOR);
                                curBuffer.flip();
                                newBuffer.put(curBuffer);
                                // 将新buffer放到key中作为附件
                                key.attach(newBuffer);
                            }
                        }
                        logger.info("after reading...");
                    } else if (key.isWritable()) {
                        SocketChannel socket = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int write = socket.write(buffer);
                        System.out.println(write);
                        // 如果已经完成了写操作，需要移除key中的附件，同时不再对写事件感兴趣
                        if (!buffer.hasRemaining()) {
                            key.attach(null);
                            key.interestOps(0);
                        }
                    }
                    // 处理完毕后移除
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 多线程版本.
     * 一个boss 监听accept
     * 多个worker 监听read
     */
    @Test
    public void threadsServer() {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            Thread.currentThread().setName("boss");
            server.bind(new InetSocketAddress(SERVER_PORT));

            // 负责轮训检查accept
            final Selector boss = Selector.open();
            server.configureBlocking(false);
            server.register(boss, SelectionKey.OP_ACCEPT);

            // 创建固定数据量的worker
            Worker[] workers = new Worker[4];
            // 用于负载因子的 robin
            AtomicInteger robin = new AtomicInteger(0);
            for (int index = 0; index < workers.length; index++) {
                workers[index] = new Worker("worker-" + index);
            }

            while (true) {
                boss.select();
                Set<SelectionKey> selectionKeys = boss.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // BossSelector负责Accept事件
                    if (key.isAcceptable()) {
                        // 建立连接
                        final SocketChannel socketChannel = server.accept();
                        System.out.println("connected...");
                        socketChannel.configureBlocking(false);
                        // socket注册到Worker的Selector中
                        System.out.println("before read...");
                        // 负载均衡，轮询分配Worker
                        workers[robin.getAndIncrement()% workers.length].register(socketChannel);
                        System.out.println("after read...");
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final class Worker implements Runnable {
        private Thread thread;
        private volatile Selector selector;
        private String name;
        private volatile boolean started = false;
        /**
         * 同步队列, 用于boss线程和worker线程的通信
         */
        private ConcurrentLinkedQueue<Runnable> queue;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel socket) throws IOException {
            // 初始化
            if (!started) {
                thread = new Thread(this, name);
                selector = Selector.open();
                queue = new ConcurrentLinkedQueue<>();
                thread.start();
                started = true;
            }

            // 向同步队列中添加SocketChannel的注册事件
            // 在Worker线程中执行注册事件
            queue.add(() -> {
                try {
                    socket.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            // 唤醒被阻塞的Selector
            // select类似LockSupport中的park，wakeup的原理类似LockSupport中的unpark
            selector.wakeup();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 若没有事件就绪，线程会被阻塞，反之不会被阻塞。从而避免了CPU空转
                    // 就绪事件数量
                    final int select = selector.select();
                    System.out.println(select);

                    // 通过同步队列获得任务并运行
                    final Runnable task = queue.poll();
                    if (task != null) {
                        // 获得任务，执行注册操作
                        task.run();
                    }
                    final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        final SelectionKey key = iterator.next();
                        // Worker只负责Read事件
                        if (key.isReadable()) {
                            // 简化处理，省略细节
                            SocketChannel socketChannel = (SocketChannel)key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            socketChannel.read(buffer);
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                        iterator.remove();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
