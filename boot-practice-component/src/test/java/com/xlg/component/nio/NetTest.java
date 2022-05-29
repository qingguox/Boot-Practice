package com.xlg.component.nio;

import static com.xlg.component.nio.TestCommon.BUFFER_EXPRESSION_FACTOR;
import static com.xlg.component.nio.TestCommon.LOCAL_HOST;
import static com.xlg.component.nio.TestCommon.SPLIT_STR;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private static final int SERVER_PORT = 8080;


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

}
