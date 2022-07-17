package com.xlg.component.netty.Pipeline;

import static com.xlg.component.nio.TestCommon.NETTY_SERVER_PORT;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author wangqingwei
 * Created on 2022-06-12
 */
public class TestPipelineServer {

    private static final Logger logger = LoggerFactory.getLogger(TestPipelineServer.class);

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // socket中 是有pipeline类似一个处理流 双向链表
                                // 默认有head -> tail  插入的分为入站和出站
                                // head -> h1 -> h2 -> h3 -> h4 -> tail
                                ChannelPipeline pipeline = ch.pipeline();

                                // 入站的handler处理完数据, 还可以把处理的数据 msg 给到下一个hander
                                // 调用后一个handler处理: ctx.fireChannelRead(msg); == super.channelRead(ctx, msg);
                                pipeline.addLast("h1", new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        logger.debug("1");
                                        super.channelRead(ctx, "1 -> 变更数据");
                                    }
                                });
                                pipeline.addLast("h2", new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        logger.debug("2, msg = {}", msg);
                                        // 3. 执行write写出数据, 这样出站handler才能执行
                                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server..".getBytes(
                                                StandardCharsets.UTF_8)));
                                        super.channelRead(ctx, msg);
                                    }
                                });

                                // 出站的顺序是从tail向前的
                                pipeline.addLast("h3", new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                            throws Exception {
                                        logger.debug("3");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                                pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
                                            throws Exception {
                                        logger.debug("4");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                            }
                        })
                .bind(NETTY_SERVER_PORT);
    }
}
