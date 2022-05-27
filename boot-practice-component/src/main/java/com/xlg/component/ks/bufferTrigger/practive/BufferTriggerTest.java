package com.xlg.component.ks.bufferTrigger.practive;

import java.time.Duration;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.phantomthief.collection.BufferTrigger;

/**
 * @author wangqingwei
 * Created on 2022-04-22
 *
 * todo: 后面可以搞一个类似Kconf动态配置, 或者直接用携程, 其实底层都是zk.
 */
public abstract class BufferTriggerTest<T> {


    private static final Logger logger = LoggerFactory.getLogger(BufferTriggerTest.class);
    private static final int DEFAULT_BUFFER_SIZE = 10000;
    private static final int DEFAULT_BUFFER_BATCH_SIZE = 500;
    private static final int DEFAULT_BUFFER_DURATION = 10;

    /**
     * test一个List的insert
     */
    private BufferTrigger<T> bufferTrigger;

    @PostConstruct
    public void initBufferTrigger() {
        bufferTrigger = BufferTrigger.<T>batchBlocking()
                .bufferSize(DEFAULT_BUFFER_SIZE)
                .batchSize(DEFAULT_BUFFER_BATCH_SIZE)
                .linger(() -> Duration.ofSeconds(DEFAULT_BUFFER_DURATION))
                .setConsumerEx(this::processOnce)
                .build();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bufferTrigger.manuallyDoTrigger();
            logger.info("trigger {} closed, manual do the trigger", getTriggerName());
        }));
    }

    private void processOnce(List<T> ts) {
        if (CollectionUtils.isEmpty(ts)) {
            return;
        }
        // process
    }

    protected abstract String getTriggerName();
}
