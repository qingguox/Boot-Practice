package com.xlg.component.ks.bufferTrigger;

import com.xlg.component.ks.enums.Indicator;
import com.xlg.component.ks.model.IndicatorUser;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
public interface IndicatorAggregator {

    Indicator support();

    void aggregatorData(IndicatorUser indicatorUser);
}
