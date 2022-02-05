package com.xlg.component.ks.effectiveJava.test4;


import java.util.Arrays;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

/**
 * @author wangqingwei
 * Created on 2022-02-05
 */
public class First5 {

    public static void main(String[] args) {

    }

    void testIntArrayLast() {
        int size = 10;
        int[] testIntArray = new int[size];
        for (int i = 0; i < size; i++) {
            testIntArray[i] = i + 1;
        }
        //        int lastInt = testIntArray[size--];
        //        System.out.println(lastInt);
        int lastInt = testIntArray[--size];
        System.out.println(lastInt);
    }

    static void testStackE() {
        Stack<Integer> stack = new Stack<>();
        Stream.iterate(0, seed -> seed + 1).limit(10).forEach(stack::push);
        new Thread(() -> {
            testStackE(stack);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            testStackE(stack);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    static void testStackE(Stack<Integer> stack) {
        while (!stack.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + " ------- " + stack.pop());
        }
    }

    public static <E extends Comparable<E>> Optional<E> max(Collection<E> e) {
        Assert.isTrue(CollectionUtils.isEmpty(e), "Empty collection!");
        E result = null;
        for (E cur : e) {
            if (cur != null && cur.compareTo(result) > 0)
                result = cur;
        }
        return Optional.ofNullable(result);
    }

    public static class Stack<E> {
        private Object[] elements;
        private int size = 0;
        private static final int DEFAULT_CAPACITY = 16;

        @SuppressWarnings("unchecked")
        public Stack() {
            elements = (E[]) new Object[DEFAULT_CAPACITY];
        }

        public Stack(int capacity) {
            Assert.isTrue(capacity > 0, "[Stack is init, capacity must > 0!]");
            elements = new Object[capacity];
        }

        public synchronized void push(Object obj) {
            ensureCapacity();
            elements[size++] = obj;
        }

        public synchronized E pop() {
            if (size == 0) throw new EmptyStackException();
            @SuppressWarnings("unchecked")
            E pop = (E) elements[--size];
            elements[size] = null;
            return pop;
        }

        public synchronized boolean isEmpty() {
            return size == 0;
        }

        private void ensureCapacity() {
            if (elements.length == size) {
                elements = Arrays.copyOf(elements, 2 * size + 1);
            }
        }
    }
}


