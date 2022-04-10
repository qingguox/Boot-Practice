package com.xlg.component.ks.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.github.phantomthief.util.MoreSuppliers;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author wangqingwei
 * Created on 2022-04-10
 */
public class ThreadExecutorHolder {

    private static final int POOL_CORE_SIZE = 10;
    private static final int POOL_MAX_SIZE = 20;
    private static final int POOL_KEEP_LIVE = 60;
    private static final int QUEUE_SIZE = 1000;
    private static final MoreSuppliers.CloseableSupplier<ExecutorService> INDICATOR_PROCESS_POOL;
    private static final AtomicReference<LinkedBlockingQueue<Runnable>> INDICATOR_PROCESS_REF = new AtomicReference<>();

    static {
        INDICATOR_PROCESS_POOL = MoreSuppliers.lazy(() -> {
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);

            INDICATOR_PROCESS_REF.set(queue);
            ThreadPoolExecutor executor =
                    new ThreadPoolExecutor(POOL_CORE_SIZE, POOL_MAX_SIZE, POOL_KEEP_LIVE,
                            TimeUnit.SECONDS, queue,
                            new ThreadFactoryBuilder()
                                    .setNameFormat("indicator-process-%d")
                                    .build());
            executor.allowCoreThreadTimeOut(true);
            return executor;
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> INDICATOR_PROCESS_POOL
                .tryClose(threadPool -> MoreExecutors.shutdownAndAwaitTermination(threadPool, 1, TimeUnit.DAYS))));
    }


    public static ExecutorService getIndicatorProcessExecutor() {
        return INDICATOR_PROCESS_POOL.get();
    }
}
