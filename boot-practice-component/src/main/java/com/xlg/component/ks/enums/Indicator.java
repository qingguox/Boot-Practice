package com.xlg.component.ks.enums;

import com.xlg.component.ks.utils.IntDescValue;

/**
 * @author wangqingwei
 * Created on 2021-08-14
 */
public enum Indicator implements IntDescValue {

    UNKNOWN(0, "未知"),
    UPLOAD_PHOTO(1, "上传视频"),
    LIVE_START(2, "直播开始")


    ;

    Indicator(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private int value;
    private String desc;

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public int getValue() {
        return value;
    }
}
