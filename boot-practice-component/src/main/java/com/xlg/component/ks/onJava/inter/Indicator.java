package com.xlg.component.ks.onJava.inter;

/**
 * @author wangqingwei
 * Created on 2021-12-05
 */
public interface Indicator {
    void dance();

    default void dance2() {
        System.out.println("2");
    }
}
