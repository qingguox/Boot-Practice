package com.xlg.component.netty.rpc.client;

import static com.xlg.component.netty.rpc.client.handler.RpcResponseMessageHandler.PROMISE;
import static com.xlg.component.utils.SeqUtils.genSeqId;

import java.lang.reflect.Proxy;

import com.xlg.component.netty.chat.message.RpcRequestMessage;
import com.xlg.component.netty.chat.protocol.MessageShareCodec;
import com.xlg.component.netty.chat.protocol.ProtocolFrameDecoder;
import com.xlg.component.netty.chat.server.service.HelloService;
import com.xlg.component.netty.rpc.client.handler.RpcResponseMessageHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangqingwei
 * Created on 2022-06-27
 */
@Slf4j
public class RpcClientManager {


    public static void main(String[] args) {
        HelloService helloService = getProxyService(HelloService.class);
        System.out.println(helloService.sayHello("zhangsan"));
        System.out.println(helloService.sayHello("lisi"));
    }

    /**
     * 设置一个代理类
     */
    private static <T> T getProxyService(Class<T> service) {
        ClassLoader loader = service.getClassLoader();
        Class<?>[] interfaces = new Class<?>[] {service};
        Object o = Proxy.newProxyInstance(loader, interfaces, ((proxy, method, args) -> {
            // 其实底层就是发了个长连接的消息
            RpcRequestMessage message = new RpcRequestMessage(Math.toIntExact(genSeqId()),
                    service.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);
            Channel channel = getChannel();
            channel.writeAndFlush(message);

            // 等待回来, 注意此时是main线程和 底层eventLoop线程通信，是不是得搞个中间promise缓存处理结果.
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            PROMISE.put(message.getSequenceId(), promise);

            // 不会抛出异常, sync会
            promise.await();
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                throw new RuntimeException(promise.cause());
            }
        }));
        return (T)o;
    }


    private static Channel channel = null;
    private static final Object LOCK = new Object();

    private static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }


    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        MessageShareCodec MESSAGE_SHARE_CODEC = new MessageShareCodec();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtocolFrameDecoder());
                pipeline.addLast(LOGGING_HANDLER);
                pipeline.addLast(MESSAGE_SHARE_CODEC);
                pipeline.addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
