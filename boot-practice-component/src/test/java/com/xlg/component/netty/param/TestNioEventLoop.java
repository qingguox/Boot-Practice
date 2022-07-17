package com.xlg.component.netty.param;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;

public class TestNioEventLoop {
    public static void main(String[] args) {
        EventLoop eventLoop = new NioEventLoopGroup().next();
        // 使用NioEventLoop执行任务
        eventLoop.execute(()->{
            System.out.println("hello");
        });
    }
}