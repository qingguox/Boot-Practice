package com.xlg.component.leetCode;

import com.alibaba.druid.support.json.JSONUtils;
import com.xlg.component.someBase.sort.BinaryTreeTest;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qingguox
 * Created on 2025-02-09
 */
public class Test01 {





    @Test
    public void test01() {
        int[][] ss = {{13, 13}, {13, 13}};
        int i = equalPairs(ss);
        System.out.println("i:" + i);
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();

        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1);

        int[] xxxx = new int[] {2, 4,5 ,64, 34, 0, 2};
        heapSort(xxxx);
//        System.out.println(Arrays.toString(xxxx));

        //
        System.out.println(heapSortMaxK(xxxx, 2));
        System.out.println(Arrays.toString(xxxx));
    }

        /**
     * 1. 求两个字符串的想加 | 合并字符串
     * https://leetcode.cn/problems/merge-strings-alternately/submissions/597969816/?envType=study-plan-v2&envId=leetcode-75
     */
    public String mergeAlternately(String word1, String word2) {
        int word1Size = word1.length();
        int word2Size = word2.length();
        if (word1Size == 0) {
            return word2;
        }
        if (word2Size == 0) {
            return word1;
        }
        char[] result = new char[word1Size + word2Size];
        int index = 0;
        int i = 0;
        while (index < word1Size || index < word2Size) {
            if (index < word1Size) {
                result[i++] = word1.charAt(index);
            }
            if (index < word2Size) {
                result[i++] = word1.charAt(index);
            }
            index++;
        }
        return new String(result);
    }

    /**
     * 字符串的最大公约数
     * str1: ABABAB str2: AB
     *
     */
    public String gcdOfStrings(String str1, String str2) {
        // 解法1: 两个字符串必定有 一串字符重复X次
//        return gcdOfStrings1(str1, str2);
       // 解法2: 通过两个字符串长度的最大公约数来间接计算 o(n)
//        return gcdOfStrings2(str1, str2);
        // 解法3: 利用最大公约数这一点,  a+b == b + a  o(n)
        if (!str1.concat(str2).equals(str2.concat(str1))) {
            return "";
        }
        return str1.substring(0, gcd(str1.length(), str2.length()));
    }

    public String gcdOfStrings2(String str1, String str2) {
        // 解法1: 两个字符串必定有 一串字符重复X次
        int size1 = str1.length();
        int size2 = str2.length();

        String t = str1.substring(0, gcd(size1, size2));
        if (checkGcdOfString(t, str1) && checkGcdOfString(t, str2)) {
            return t;
        }
        int[] candies = new int[10];
        int length = candies.length;
        int maxCandie = Arrays.stream(candies).boxed().max(Comparator.naturalOrder()).get();
        String ss = "";
        char[] charArray = ss.toCharArray();
        String sd = new String(charArray);
        sd.indexOf('x');
        Arrays.sort(charArray);
        return "";
    }

    public int gcd(int a, int b) {
        // 求a b 最大公约数
        int fen = a % b;
        while (fen != 0) {
            a = b;
            b = fen;
            fen = a % b;
        }
        return b;
    }

    public String gcdOfStrings1(String str1, String str2) {
        // 解法1: 两个字符串必定有 一串字符重复X次
        int size1 = str1.length();
        int size2 = str2.length();

        for (int i = Math.min(size1, size2); i >= 1; i--) {
            if (size1 % i == 0 && size2 % i == 0) {
                String t = str1.substring(0, i);
                if (checkGcdOfString(t, str1) && checkGcdOfString(t, str2)) {
                    return t;
                }
            }
        }
        return "";
    }

    private boolean checkGcdOfString(String t, String str1) {
        // 重复n次, 必定可以
        int len = str1.length() / t.length();
        StringBuffer ams = new StringBuffer();
        for (int j = 1; j <= len; j++) {
            ams.append(t);
        }
        return ams.toString().equals(str1);
    }


    public int equalPairs(int[][] grid) {
        Map<String, Integer> x = new HashMap<>();
        Map<String, Integer> y = new HashMap<>();
        int n = grid.length;
        for (int i = 0; i < n; i++) {
            String nn = "";
            String mm = "";
            for (int j = 0; j <n; j++) {
                nn= nn + grid[i][j];
                mm= mm + grid[j][i];
            }
            x.put(nn, x.getOrDefault(nn, 0) + 1);
            y.put(mm, y.getOrDefault(mm, 0) + 1);
        }
        System.out.printf("x :" + JSONUtils.toJSONString(x));
        System.out.printf("y :" + JSONUtils.toJSONString(y));
        int result = 0;
        for (Map.Entry<String, Integer> sss : x.entrySet()) {
            String cur = sss.getKey();
            int curVal = sss.getValue();
            int ssss = y.getOrDefault(cur, 0);
            result = result + Math.min(curVal, ssss);
        }
        return result;
    }

    // 1. 无序数组第K大的数据, 堆排序, 每次大顶堆.
    public int heapSortMaxK(int[] a, int k) {
        if (a == null || a.length < 2 || a.length < k) {
            return 0;
        }
        //构建小顶堆
        // insert
        for (int i = 0; i < a.length; i++) {
            heapInsertV2(a, i);
        }
        int count = 0;
        int heapSize = a.length;
        swap(a, 0, --heapSize);
        count++;
        while (heapSize > 0 && count < k) {
            heapChange(a, 0, heapSize);
            swap(a, 0, --heapSize);
            count++;
        }
        return a[heapSize];
    }

    public void heapSort(int[] a) {
        if (a == null || a.length <= 1) {
            return;
        }
        //构建小顶堆
        // insert
        for (int i = 0; i < a.length; i++) {
            heapInsertV2(a, i);
        }
        int heapSize = a.length;
        swap(a, 0, --heapSize);
        while (heapSize > 0) {
            heapChange(a, 0, heapSize);
            swap(a, 0, --heapSize);
        }
    }

    private void heapChange(int[] a, int index, int heapSize) {
        int left = index * 2 + 1;
        while (left < heapSize) {
            int largest = left + 1 < heapSize && a[left + 1] > a[left] ? left + 1 : left;
            largest = a[largest] > a[index] ? largest : index;
            if (largest == index) {
                break;
            }
            swap(a, largest, index);
            index = largest;
            left = index * 2 + 1;
        }
    }

    private void heapInsertV2(int[] a, int i) {
        while (a[i] > a[(i - 1) / 2]) {
            swap(a, i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    private void swap(int[] arr, int l, int r) {
        int temp = arr[r];
        arr[r] = arr[l];
        arr[l] = temp;
    }

}

class Solution {
    // 合并两个有序链表
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode preHead = new ListNode(-1);

        ListNode pre = preHead;
        while (list1 != null && list2 != null) {
           if (list1.val < list2.val) {
               pre.next = list1;
               list1 = list1.next;
           } else {
               pre.next = list2;
               list2 = list2.next;
           }
           pre = pre.next;
        }

        pre.next = list1 != null ? list1 : list2;
        return preHead.next;
    }

    public static class ListNode {
        private int val;
        private ListNode next;
        public ListNode() {}
        public ListNode(int value) {this.val = value;}
    }
}

