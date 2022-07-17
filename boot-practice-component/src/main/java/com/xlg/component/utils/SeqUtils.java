package com.xlg.component.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangqingwei
 * Created on 2022-06-27
 */
public class SeqUtils {
    private static AtomicLong counter = new AtomicLong();
    public static Long genSeqId() {
        return counter.incrementAndGet();
    }
}
