package com.xlg.component.leetCode;

import java.util.HashMap;
import java.util.Map;

class LRUCache {
    class DLinkeNode {
        private int key;
        private int value;
        DLinkeNode prev;
        DLinkeNode next;
        public DLinkeNode() {}
        public DLinkeNode(int key, int value) {this.key = key; this.value = value;}
    }

    private Map<Integer, DLinkeNode> cache = new HashMap<>();
    private int size;
    private int capacity;
    private DLinkeNode head;
    private DLinkeNode tail;

    public LRUCache(int capacity) {
        this.size = 0;
        this.capacity = capacity;
        this.head = new DLinkeNode();
        this.tail = new DLinkeNode();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        // 看cache中有数据没
        DLinkeNode node = cache.get(key);
        if (node == null) {
            return -1;
        }
        // 有数据, 移动到头部
        moveNodeToHead(node);
        return node.value;
    }


    public void put(int key, int value) {
        // 先看整个元素是否有没有
        DLinkeNode node = cache.get(key);
        // 没有, 则插入头部, ++size, 并检查size是否到了capacity, 到了的话, removeTail
        if (node == null) {
            node = new DLinkeNode(key, value);
            cache.put(key, node);
            addNodeToHead(node);
            ++size;
            if (size > capacity) {
                DLinkeNode tail = removeTail();
                cache.remove(tail.key);
                --size;
            }
        } else {
            // 有, 更新value, 则提前即可
            node.value = value;
            moveNodeToHead(node);
        }
    }

    private DLinkeNode removeTail() {
        DLinkeNode prev = tail.prev;
        removeNode(prev);
        return prev;
    }


    private void moveNodeToHead(DLinkeNode node) {
        removeNode(node);
        addNodeToHead(node);
    }

    private void addNodeToHead(DLinkeNode node) {
        node.next = head.next;
        head.next.prev = node;
        node.prev = head;
        head.next = node;
    }

    private void removeNode(DLinkeNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */