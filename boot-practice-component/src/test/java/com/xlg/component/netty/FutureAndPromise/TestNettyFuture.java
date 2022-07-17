package com.xlg.component.netty.FutureAndPromise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;

/**
 * @author wangqingwei
 * Created on 2022-06-11
 */
public class TestNettyFuture {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyFuture.class);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop loop = group.next();

        //2. 执行
        Future<Integer> future = loop.submit(() -> {
            logger.debug("执行计算中!");
            Thread.sleep(1000);
            return 50;
        });
        //3. 获取结果
        logger.debug("等待结果!1!");
        // 立即返回结果
        logger.debug("{}", future.getNow());
        // 同步返回结果 也就是阻塞
//        logger.debug("{}", future.get());
        // 异步返回结果
        future.addListener(future1 -> logger.debug("接受结果: {}", future1.getNow()));
    }
}
