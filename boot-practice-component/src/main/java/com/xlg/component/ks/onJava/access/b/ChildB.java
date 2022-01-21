package com.xlg.component.ks.onJava.access.b;

import com.xlg.component.ks.onJava.access.a.BasicClass;

/**
 * @author wangqingwei
 * Created on 2021-12-05
 */
public class ChildB extends BasicClass {

    public void dance3() {
        dance();
    }

    public void dance2(BasicClass basicClass) {
        // 不能访问基类实例的受保护的方法
//        basicClass.dance();
    }

    public static void main(String[] args) {
        new ChildB().dance();
        new ChildB().dance3();
//        new BasicClass().dance();
    }
}
