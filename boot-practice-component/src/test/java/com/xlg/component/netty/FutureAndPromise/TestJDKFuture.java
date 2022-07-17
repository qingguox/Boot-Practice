package com.xlg.component.netty.FutureAndPromise;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author wangqingwei
 * Created on 2022-06-11
 */
public class TestJDKFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1. 线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        //2. 执行
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("执行计算!!1");
                Thread.sleep(1000);
                return 50;
            }
        });
        //3. 获取结果
        System.out.println("等待结果!11");
        System.out.println(future.get());
    }
}
