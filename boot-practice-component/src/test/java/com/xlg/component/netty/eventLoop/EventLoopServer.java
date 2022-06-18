package com.xlg.component.netty.eventLoop;

import static com.xlg.component.nio.TestCommon.NETTY_SERVER_PORT;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wangqingwei
 * Created on 2022-06-08
 */
public class EventLoopServer {

    private static final Logger logger = LoggerFactory.getLogger(EventLoopServer.class);

    public static void main(String[] args) {
       new ServerBootstrap()
               .group(new NioEventLoopGroup())
               .channel(NioServerSocketChannel.class)
               .childHandler(new ChannelInitializer<NioSocketChannel>() {
                   @Override
                   protected void initChannel(NioSocketChannel ch) throws Exception {
                       ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                           @Override
                           public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                               ByteBuf buf = (ByteBuf) msg;
                               logger.debug("{}", buf.toString(StandardCharsets.UTF_8));
                           }
                       });
                   }
               }).bind(NETTY_SERVER_PORT);
    }
}
