package com.xlg.component.netty.eventLoop;

import java.util.concurrent.TimeUnit;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author wangqingwei
 * Created on 2022-06-08
 */
public class TestEventLoop {

    public static void main(String[] args) {
        // 1. 创建事件循环组
        /*
        nthreads == 0 ? Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2)) : nthreads;
         */
        EventLoopGroup group = new NioEventLoopGroup(2); // io 事件, 普通任务, 定时任务
        //        EventLoopGroup group = new DefaultEventLoopGroup();  // 普通任务, 定时任务

        // 2. 获取下一个事件循环对象
        // 1 3 相等 因为底层是循环走的
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        // 3. 指定普通任务
        //        group.next().submit(() -> System.out.println("ok"));

        // 4. 定时任务
        // 立即执行, 并且是1s一次
        group.next().scheduleAtFixedRate(() -> System.out.println("ok"), 0, 1, TimeUnit.SECONDS);
    }
}
