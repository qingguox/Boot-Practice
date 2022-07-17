package com.xlg.component.netty.rpc.client;

import com.xlg.component.netty.chat.message.RpcRequestMessage;
import com.xlg.component.netty.chat.protocol.MessageShareCodec;
import com.xlg.component.netty.chat.protocol.ProtocolFrameDecoder;
import com.xlg.component.netty.rpc.client.handler.RpcResponseMessageHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangqingwei
 * Created on 2022-06-27
 */
@Slf4j
public class RpClient {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler();
        MessageShareCodec MESSAGE_SHARE_CODEC = new MessageShareCodec();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            Channel channel = bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ProtocolFrameDecoder());
                    pipeline.addLast(LOGGING_HANDLER);
                    pipeline.addLast(MESSAGE_SHARE_CODEC);
                    pipeline.addLast(RPC_HANDLER);
                }
            }).connect("localhost", 8080).sync().channel();

            ChannelFuture channelFuture = channel.writeAndFlush(new RpcRequestMessage(1,
                    "com.xlg.component.netty.chat.server.service.HelloService",
                    "sayHello",
                    String.class,
                    new Class[] {String.class},
                    new Object[] {"你好!1"}));
            channelFuture.addListener(promise -> {
                if (!promise.isSuccess()) {
                    final Throwable cause = promise.cause();
                    log.debug("cause : {}", cause);
                }
            });

            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
