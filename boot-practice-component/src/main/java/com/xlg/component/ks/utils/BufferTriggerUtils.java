package com.xlg.component.ks.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.util.concurrent.ListenableFutureTask;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
public class BufferTriggerUtils {


    private static final long TIME_OUT = 120;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public static <T> List<T> triggerBatchConsume(Collection<T> data, Consumer<T> consumer, String triggerName) {
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }

        // TODO 可以自己实现一个线程池试试
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<? extends Future<?>> futureList =
                data.stream().map(oneData -> executorService.submit(ListenableFutureTask.create(() -> {
                    try {
                        consumer.accept(oneData);
                        return oneData;
                    } catch (Exception e) {
                        return null;
                    }
                }))).collect(Collectors.toList());
        List<T> successList = new ArrayList<>(futureList.size());
        futureList.forEach(task -> {
            try {
                successList.add((T) task.get(TIME_OUT, TIME_UNIT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return successList;
    }

}
