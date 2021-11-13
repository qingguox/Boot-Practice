package com.xlg.component.ks.bufferTrigger.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.github.phantomthief.collection.BufferTrigger;
import com.xlg.component.ks.bufferTrigger.IndicatorAggregator;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
@Lazy
@Service
public abstract class AbstractIndicatorAggregator<K, V> implements IndicatorAggregator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIndicatorAggregator.class);

    protected BufferTrigger<Integer> batchTrigger;
    protected BufferTrigger<Entry<K, V>> trigger;

    @PostConstruct
    public void init() {
        // batchTrigger 自己就有背压, 所以入队时之后判断元素size是否超过了max即可
        // 批量消费方式, 默认容器是LinkedBlockQueue, 可以通过指定max(bufferSize, batchSize), 设置队列大小
//        batchTrigger = BufferTrigger.<Integer>batchBlocking()
//                .batchSize(100)
//                .linger(5, TimeUnit.SECONDS)
//                .setConsumer(this::processData)
//                .build();

        // 满足count / interval 都会进行消费,  注意默认是单线程消费
        trigger = BufferTrigger.<Map.Entry<K, V>, Map<K, V>>simple()
                .maxBufferCount(getMaxBuggerCount())
                .interval(getInternal(), TimeUnit.SECONDS)
                .setContainer(ConcurrentHashMap::new, getQueueAdder())
                .consumer(this::processData)
                .enableBackPressure()           // 开启背压, 容器满了之后直接做阻塞, 需要参数maxBufferCount, 注意:不要有rejectHandler
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 清理/消费完buffer
            trigger.manuallyDoTrigger();
            logger.info("trigger {} closed, manual do the trigger", getTriggerName());
        }));
    }

    // 聚合器名称
    protected abstract String getTriggerName();

    //    protected abstract void processData(List<Integer> integers);

    /**
     * 消费数据的方法
     */
    protected abstract void processData(Map<K,V> kvMap);

    /**
     * bufferTrigger消费间隔时间
     */
    protected abstract long getInternal();

    /**
     * 聚合数据的方式
     */
    protected abstract BiPredicate<? super Map<K,V>,? super Entry<K,V>> getQueueAdder();

    /**
     * bufferTrigger最大数据量
     */
    protected abstract long getMaxBuggerCount();


}
