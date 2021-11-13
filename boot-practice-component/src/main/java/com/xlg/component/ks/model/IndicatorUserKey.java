package com.xlg.component.ks.model;

import java.util.Objects;

import com.xlg.component.ks.enums.Indicator;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
public class IndicatorUserKey {

    private long userId;
    private Indicator indicator;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IndicatorUserKey that = (IndicatorUserKey) o;
        return userId == that.userId &&
                indicator.getValue() == that.indicator.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, indicator);
    }

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
}
