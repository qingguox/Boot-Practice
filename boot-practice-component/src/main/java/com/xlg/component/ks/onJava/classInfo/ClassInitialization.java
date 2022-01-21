package com.xlg.component.ks.onJava.classInfo;

import java.util.Random;

/**
 * @author wangqingwei
 * Created on 2021-11-14
 */
public class ClassInitialization {
    public static Random rand = new Random(47);
    public static void main(String[] args) throws ClassNotFoundException {
        Class initable = Initable.class;
        System.out.println("after creating Initable ref ");
        // 不会创建
        System.out.println(Initable.STATIC_FINAL);
        // 会创建, 因为引用了类的非final的static字段
        System.out.println(Initable.STATIC_FINAL_2);
        // 会创建, 同上
        System.out.println(Initable2.STATIC_NO_FINAL);
        // 会初始化类, 因为forName 也是一个static方法
        Class<?> initable3 = Class.forName("com.xlg.component.ks.onJava.classInfo.Initable3");
        System.out.println("after creating Initable3 ref ");
        System.out.println(Initable3.STATIC_NO_FINAL);
    }
}

class Initable {
    static final int STATIC_FINAL = 47;
    static final int STATIC_FINAL_2 = ClassInitialization.rand.nextInt(1000);
    static {
        System.out.println("init initable");
    }
}

class Initable2 {
    static int STATIC_NO_FINAL = 147;
    static {
        System.out.println("init initable2");
    }
}

class Initable3 {
    static int STATIC_NO_FINAL = 74;
    static {
        System.out.println("init initable3");
    }
}