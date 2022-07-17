package com.xlg.component.netty.source;

import static com.xlg.component.nio.TestCommon.NETTY_SERVER_PORT;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author wangqingwei
 * Created on 2022-06-28
 */
public class TestSourceServer {
    public static void main(String[] args) {
        // 1. 启动器, 负责组装netty组件, 启动服务器
        new ServerBootstrap()
                // 2. BossEventLoop, WorkerEventLoop(select, thread) group组 可以理解为线程池+selector工人
                .group(new NioEventLoopGroup())
                // 3. 选择服务器的 NioServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)
                // 4. boss负责连接客户端 worker(child)负责处理读写, 决定了worker(child)能执行哪些操作(handler)
                .childHandler(
                        // 5. 对客户端的读写初始化
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 将byteBuf -> string
                                ch.pipeline().addLast(new StringDecoder());
                                // 自定义一个handler 其实就是一个读事件
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        })
                .bind(NETTY_SERVER_PORT);
    }
}
