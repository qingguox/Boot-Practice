package com.xlg.component.ks.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wangqingwei
 * Created on 2022-02-05
 */
public class EnumUtils {

    public static <T extends Enum & IntDescValue> T fromValue(Class<T> clazz, int value, T defaultValue) {
        return Arrays.stream(clazz.getEnumConstants()).filter((x) -> {
            return Objects.equals(((IntDescValue)x).getValue(), value);
        }).findFirst().orElse(defaultValue);
    }

    public static <T extends Enum & IntDescValue> T valueOf(Class<T> clazz, String desc, T defaultDesc) {
        return Arrays.stream(clazz.getEnumConstants()).filter((x) -> {
            return Objects.equals(((IntDescValue)x).getDesc(), desc);
        }).findFirst().orElse(defaultDesc);
    }
}
