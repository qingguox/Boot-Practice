package com.xlg.component.ks.onJava.access.a;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-12-05
 */
public class OtherA {

    private void dance() {
        new BasicClass().dance();
    }

    public static void main(String[] args) {
        OtherA a = new OtherA();
        a.dance();
    }
}
