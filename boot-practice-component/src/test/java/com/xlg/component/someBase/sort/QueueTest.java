package com.xlg.component.someBase.sort;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qingguox
 * Created on 2025-03-03
 */
public class QueueTest {

    public static void main(String[] args) {
//        ConcurrentHashMap…ConcurrentHashMap
    }

    // 1. 设计一个有限队列
    // 2. unSafe理解
    // 3. 泛型理解
    // 4. SQL问题
    // 5. errorLog中找哪些异常, 异常出现次数等
//
//
//    学生， 班级， 科目id,  分数
//       id s       k       s
//    varchar          BigDigiceml
//            // 每个班级s 每个科目k 最高分s, 最高分的人数
//                 s     d       x

    //  select ok.x, count(id) from table
    //  join (select s,d, max(s) from table order by s, d) as ok having on
    //  on ok.s = s and ok.d = d

    static class CQue {
        private int[] ass;
        private int size = 0;
        private int count = 0;

        private int addIndex;
        private int takeIndex;

        private ReentrantLock lock;
        private Condition notEmpty;
        private Condition notFull;

        public CQue(int size) {
            ass = new int[size];
            this.size = size;
            notEmpty = lock.newCondition();
            notFull = lock.newCondition();
        }

        public boolean add(int s) throws InterruptedException {
            if (addIndex > size) {
                notFull.wait();
                addIndex = 0;
            }
            ass[addIndex++] = s;
            count++;
            notEmpty.notify();
            return true;
        }

        public int poll(CQue cQue) throws InterruptedException {
            if (takeIndex > size) {
                notFull.wait();
                takeIndex = 0;
            }
            int s = ass[takeIndex++];
            count--;
            notEmpty.notify();
            return s;
        }
    }
}
