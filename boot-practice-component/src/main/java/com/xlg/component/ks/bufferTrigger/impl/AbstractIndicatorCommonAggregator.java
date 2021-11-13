package com.xlg.component.ks.bufferTrigger.impl;

import static com.xlg.component.ks.utils.BufferTriggerUtils.triggerBatchConsume;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiPredicate;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.xlg.component.ks.model.IndicatorUser;
import com.xlg.component.ks.model.IndicatorUserKey;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
@Lazy
@Service
public abstract class AbstractIndicatorCommonAggregator
        extends AbstractIndicatorAggregator<IndicatorUserKey, IndicatorUser> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIndicatorCommonAggregator.class);

    /**
     * 聚合策略 比如sum型
     * @return
     */
    @Override
    protected BiPredicate<? super Map<IndicatorUserKey, IndicatorUser>, ? super Entry<IndicatorUserKey,
            IndicatorUser>> getQueueAdder() {
        return (map, entry) -> {
            map.merge(entry.getKey(), entry.getValue(), (oldUser, newUser) -> {
                long actionCount = oldUser.getActionValue() + newUser.getActionValue();
                newUser.setActionValue(actionCount);
                return newUser;
            });
            return true;
        };
    }

    /**
     * 处理数据
     */
    @Override
    protected void processData(Map<IndicatorUserKey, IndicatorUser> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }

        // 并发消费, 并且打点
        triggerBatchConsume(map.values(), this::processOneData, getTriggerName());
    }

    private void processOneData(IndicatorUser indicatorUser) {
        logger.info("process on data indicatorUser={}", JSON.toJSONString(indicatorUser));
    }

    /**
     * 入队
     */
    @Override
    public void aggregatorData(IndicatorUser indicatorUser) {
        IndicatorUserKey key = new IndicatorUserKey();
        BeanUtils.copyProperties(indicatorUser, key);
        trigger.enqueue(Maps.immutableEntry(key, indicatorUser));
    }
}
