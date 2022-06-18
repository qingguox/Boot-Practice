package com.xlg.component.netty.eventLoop;

import static com.xlg.component.nio.TestCommon.LOCAL_HOST;
import static com.xlg.component.nio.TestCommon.NETTY_SERVER_PORT;

import java.net.InetSocketAddress;

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

/**
 * @author wangqingwei
 * Created on 2022-06-08
 */
public class EventLoopClient {
    private static final Logger logger = LoggerFactory.getLogger(EventLoopClient.class);

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1. 建立链接
                // 异步非阻塞, main发起了调用, 真正执行的是 nio 线程.NioEventLoopGroup
                .connect(new InetSocketAddress(LOCAL_HOST, NETTY_SERVER_PORT));

        // 2.1 使用sync同步处理结果
        // 阻塞住当前线程, 直到nio线程连接完毕. 如果去掉sync此时, 由于连接未建立完毕, main执行, channel无法给服务器写数据
//        channelFuture.sync();
//        Channel channel = channelFuture.channel();
//        System.out.println(channel);
//        System.out.println("--");
//        channel.writeAndFlush("hello world");

        // 2.2 异步addListener(回调对象)
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                final Channel channel = future.channel();
                logger.debug("{}", channel);
                channel.writeAndFlush("hello world !!");
            }
        });
    }
}
