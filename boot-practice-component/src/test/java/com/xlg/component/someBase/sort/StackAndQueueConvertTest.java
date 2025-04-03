package com.xlg.component.someBase.sort;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @author qingguox
 * Created on 2025-02-16
 */
public class StackAndQueueConvertTest {

    public static void main(String[] args) {
        TestStackQueue<Integer> queue = new TestStackQueue<>();
        queue.push(11);
        queue.push(21);
        Integer peek = queue.peek();
        System.out.println(peek);
        Integer peek2 = queue.poll();
        System.out.println(peek2);

        queue.push(51);
        Integer peek3 = queue.poll();
        System.out.println(peek3);

        TestQueueStack<Integer> stack = new TestQueueStack<>();
        stack.push(23);
        stack.push(29);

        Integer peek1 = stack.peek();
        System.out.println(peek1);
        Integer pop = stack.pop();
        System.out.println(pop);

        stack.push(20);
        System.out.println(stack.peek());
    }

    // 两个栈实现一个队列, 倒数据
    public static class TestStackQueue<T> {
        private Stack<T> data;
        private Stack<T> help;

        public TestStackQueue() {
            this.data = new Stack<>();
            this.help = new Stack<>();
        }

        public void push(T n) {
            data.push(n);
        }

        public T peek() {
            if (this.data.isEmpty() && this.help.isEmpty()) {
                throw new RuntimeException("this queue is empty");
            }
            dao();
            return help.peek();
        }

        public T poll() {
            if (this.data.isEmpty() && this.help.isEmpty()) {
                throw new RuntimeException("this queue is empty");
            }
            dao();
            return help.pop();
        }

        public void dao() {
            if (!help.isEmpty()) {
                return;
            }
            while (!data.isEmpty()) {
                help.push(data.pop());
            }
        }
    }

    // 两个队列实现一个栈
    public static class TestQueueStack<T> {
        private Queue<T> data;
        private Queue<T> help;

        public TestQueueStack() {
            this.data = new LinkedList<>();
            this.help = new LinkedList<>();
        }

        public void push(T n) {
            this.data.add(n);
        }

        public T pop() {
            if (this.data.isEmpty()) {
                throw new RuntimeException("the stack is empty！");
            }
            while (data.size() > 1) {
                help.add(data.poll());
            }
            T poll = data.poll();
            swapInner();
            return poll;
        }

        public T peek() {
            if (this.data.isEmpty()) {
                throw new RuntimeException("the stack is empty！");
            }
            while (data.size() > 1) {
                help.add(data.poll());
            }
            T poll = data.poll();
            help.add(poll);
            swapInner();
            return poll;
        }

        private void swapInner() {
            Queue<T> tmp = help;
            help = data;
            data = tmp;
        }

    }


}
