package com.xlg.component.ks.onJava.init;

/**
 * @author wangqingwei
 * Created on 2021-12-05
 */
public class IntegerTest {


    public static void main(String[] args) {
        // start1 和start2 比较的时候会自动装箱. 存储的值是同一个.
        int start1 = 99;
        Integer start2 = 99;
        System.out.println(start1 == start2);

        // 相当于start3栈内存存储了堆上的一个对象的地址.
        Integer start3 = new Integer(99);
        start3.intValue();
        System.out.println(start1 == start3);
        System.out.println(start3.equals(start1));
        System.out.println(System.identityHashCode(start1));
        System.out.println(System.identityHashCode(start3));

        // int 和 Integer 比较
        // == Integer 拆箱 intValue 直接return value
        // equals int 装箱 valueOf (有-128 ~ 127缓存)
    }

}
