package com.xlg.component.ks.model;

import com.xlg.component.ks.enums.Indicator;

/**
 * @author wangqingwei 
 * Created on 2021-08-14
 */
public class IndicatorUser {

    private long userId;
    private Indicator indicator;
    private long actionTime;
    private long actionValue = 1;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public long getActionValue() {
        return actionValue;
    }

    public void setActionValue(long actionValue) {
        this.actionValue = actionValue;
    }
}
