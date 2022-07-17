package com.xlg.component.netty.eventLoop;

import static com.xlg.component.nio.TestCommon.LOCAL_HOST;
import static com.xlg.component.nio.TestCommon.NETTY_SERVER_PORT;

import java.net.InetSocketAddress;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author wangqingwei
 * Created on 2022-06-08
 */
public class CloseFutureClient {
    private static final Logger logger = LoggerFactory.getLogger(CloseFutureClient.class);

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1. 建立链接
                // 异步非阻塞, main发起了调用, 真正执行的是 nio 线程.NioEventLoopGroup
                .connect(new InetSocketAddress(LOCAL_HOST, NETTY_SERVER_PORT));

        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    // 还是异步的, 关闭交给另外一个线程处理
                    channel.close();
                    // thread1 输出
//                    logger.debug("关闭之后的操作!!1");
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "thread1").start();
//        logger.debug("main end ");

        // 获取closeFuture  关闭后的处理 1. 同步 2.异步
        ChannelFuture closeFuture = channel.closeFuture();
//        logger.debug("waiting close....");
//        closeFuture.sync();
//        logger.debug("处理关闭之后的操作...");

        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.debug("处理关闭之后的操作...");
                // 优雅的关闭NioEventLoopGroup 里面还有其他线程
                group.shutdownGracefully();
            }
        });
    }
}
