package com.xlg.component.leetCode;


import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qingguox
 * Created on 2025-04-17
 */
public class LRUCacheTest {

    // 一个字符串中 abc,
    // 方案一 a 和 b
    // 方案二 a 和 c
    // 多少回合后删除完毕
    private static int getABCCount(String str) {
        if (StringUtils.isBlank(str)) {
            return 0;
        }
        // 思路: a = b + c
        char[] charArray = str.toCharArray();
        int aCount = 0;
        int bCount = 0;
        int cCount = 0;
        for (int i = 0; i < charArray.length; i++) {
            char x = charArray[i];
            if (x == 'a') {
                aCount++;
            }
            if (x == 'b') {
                bCount++;
            }
            if (x == 'c') {
                cCount++;
            }
        }
        boolean over = aCount == bCount + cCount;
        if (!over) {
            return -1;
        }
        return aCount;
    }

    public static void main(String[] args) {
        LRUCacheDTO nodeTest = new LRUCacheDTO(2);

        nodeTest.put(1, 1);
        nodeTest.put(2, 2);

        // 2- 1
        nodeTest.put(3, 3);
        // 3 -2
        int val = nodeTest.get(1);
        System.out.println(val);

        System.out.println("******");
        //
        String xx  = "abc";
        int abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == -1);

        xx = "abac";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == 2);

        xx = "abab";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == 2);

        xx = "acac";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == 2);

        xx = "accc";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == -1);


        xx = "abaacc";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == 3);

        xx = "aaaabbcc";
        abcCount = getABCCount(xx);
        System.out.println(abcCount);
        Assert.assertTrue(abcCount == 4);
    }

    public static class LRUCacheNodeTest {
        private int key;
        private int value;
        private LRUCacheNodeTest next;
        private LRUCacheNodeTest prev;
        public LRUCacheNodeTest() {}
        public LRUCacheNodeTest(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class LRUCacheDTO {
        private int size;
        private int capacity;
        private Map<Integer, LRUCacheNodeTest> map;
        private LRUCacheNodeTest head;
        private LRUCacheNodeTest tail;

        public LRUCacheDTO(int capacity) {
            this.size = 0;
            this.capacity = capacity;
            this.map = new HashMap<>();
            this.head = new LRUCacheNodeTest();
            this.tail = new LRUCacheNodeTest();
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }

        public int get(int key) {
            LRUCacheNodeTest node = map.get(key);
            if (node == null) {
                return -1;
            }
            // 跟新
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            LRUCacheNodeTest nodeTest = map.get(key);
            if (nodeTest == null) {
                nodeTest = new LRUCacheNodeTest(key, value);
                map.put(key, nodeTest);
                addToHead(nodeTest);
                ++size;
                if (size > capacity) {
                    LRUCacheNodeTest tail = removeTail();
                    map.remove(tail.key);
                    --size;
                }
            } else {
                nodeTest.value = value;
                moveToHead(nodeTest);
            }
        }

        private LRUCacheNodeTest removeTail() {
            LRUCacheNodeTest prev = tail.prev;
            removeNode(prev);
            return prev;
        }

        private void moveToHead(LRUCacheNodeTest node) {
            removeNode(node);
            addToHead(node);
        }

        private void addToHead(LRUCacheNodeTest node) {
            node.next = head.next;
            head.next.prev = node;

            head.next = node;
            node.prev = head;
        }

        private void removeNode(LRUCacheNodeTest node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}
