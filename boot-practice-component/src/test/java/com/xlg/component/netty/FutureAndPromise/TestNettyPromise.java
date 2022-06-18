package com.xlg.component.netty.FutureAndPromise;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;

/**
 * @author wangqingwei
 * Created on 2022-06-11
 */
public class TestNettyPromise {

    private static final Logger logger = LoggerFactory.getLogger(TestNettyPromise.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        EventLoop loop = group.next();

        DefaultPromise<Object> promise = new DefaultPromise<>(loop);

        new Thread(() -> {

            logger.debug("开始计算！！");
            try {
                int i = 1 / 0;
                Thread.sleep(1000);
                promise.setSuccess(50);
            } catch (Exception e) {
                promise.setFailure(e);
            }

        }).start();

        logger.debug("等待结果!!");

//        logger.debug("接受结果: {}", promise.get());
        logger.debug("接受结果: {}", promise.isSuccess());
    }
}
