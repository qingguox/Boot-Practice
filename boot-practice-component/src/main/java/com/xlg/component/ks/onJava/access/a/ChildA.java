package com.xlg.component.ks.onJava.access.a;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-12-05
 */
public class ChildA extends BasicClass {

    public void dance4() {
        dance();
    }

    public void dance2() {
        new ChildAA().dance();
        System.out.println(" s");
    }

    public void dance3() {
        new BasicClass().dance();
    }

    public static void main(String[] args) {
        new ChildA().dance4();
        new ChildA().dance2();
        new ChildA().dance3();
    }
}
