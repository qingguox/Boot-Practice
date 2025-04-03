package com.xlg.component.someBase.sort;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 六种排序算法
 * 冒泡: 每次最后一位为最大值
 * 选择: 选择最小值到第一位
 * 插入: 最小值提前
 *
 * @author qingguox
 * Created on 2025-02-16
 */
public class SortSixText {

    public static void main(String[] args) {
        int[] arr = {5, 2, 3, 4};
//        mergeSort(arr);
//        quickSort(arr);
        heapSort(arr);
        printArray(arr);

        int ss = -2 / 2;
        System.out.println(ss);

        LockSupport.park();
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();
        Condition condition1 = reentrantLock.newCondition();
        Deque<Integer> ssss = new LinkedList<>();
        ssss.push(11);
        List<Integer> sssss = new ArrayList<>();
        Queue<Integer> priporityQueue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
    }
    private void ss() throws InterruptedException {
        wait();
    }

    public static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    // 冒泡排序: 每次最后一位为最大值
    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int end = arr.length - 1; end >= 0; end--) {  // 每一次循环都是把最大值放在最后
            for (int j = 0; j < end; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

    // 选择排序: 选择最小值到第一位
    public static void selectSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                minIndex = arr[j] < arr[minIndex] ? j : minIndex;
            }
            swap(arr, i, minIndex);
        }
    }

    // 插入排序: 最小值提前
    public static void insertSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        for (int i = 1; i < n; i++) {  // 构造一个小队列, 每次把最小值放在最前面
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                swap(arr, j, j + 1);
            }
        }
    }

    // 归并排序, 快内有序，快间无序 时间O(nlogn) 空间O(n)
    public static void mergeSort(int[] arr) {
        // 分而治之
        if (arr == null || arr.length < 2) {
            return;
        }
        sortProcess(arr, 0, arr.length - 1);
    }

    private static void sortProcess(int[] arr, int l, int r) {
        if (l == r) {
            return;
        }
        int mid = l + ((r - l) >> 1);
        sortProcess(arr, l, mid);
        sortProcess(arr, mid + 1, r);
        mergeV2(arr, l, mid, r); // 真正组合数据
    }

    private static void mergeV2(int[] arr, int l, int mid, int r) {
        int[] help = new int[r - l + 1];
        int p1 = l, p2 = mid + 1;
        int i = 0;
        while (p1 <= mid && p2 <= r) {
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        // p1 p2有剩余
        while (p1 <= mid) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }
        // 回写到arr数组中
        for (int j = 0; j < help.length; j++) {
            arr[l + j] = help[j];
        }
    }

    // 快速排序, 快内无序, 快间有序 时间O(nlogn) - O(n^2) 空间O(logn)
    public static void quickSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        quickSortProcess(arr, 0, arr.length - 1);
    }

    private static void quickSortProcess(int[] arr, int l, int r) {
        if (l < r) {
            swap(arr, l + (int) (Math.random() * (r - l + 1)), r);   // 增加随机性
            int[] partition = partitionProcess(arr, l, r);
            quickSortProcess(arr, l, partition[0] - 1);
            quickSortProcess(arr,partition[1] + 1, r);
        }
    }

    // 形成一个 以 [,,arr[r],,,] arr[r]为中心 数组, return less+1, more
    private static int[] partitionProcess(int[] arr, int l, int r) {
        int less = l - 1;
        int more = r;
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, ++less, l++);
            } else if (arr[l] > arr[r]) {
                swap(arr, --more, l);
            } else {
                l++;
            }
        }
        swap(arr, more, r);
        return new int[]{less + 1, more};
    }

    // 堆排序, 构造一个大顶堆, 每次index=0(堆顶) 和 堆底元素交换, 然后再构造大顶堆
    public static void heapSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            heapInsert(arr, i);
        }
        int heapSize = arr.length;
        swap(arr, 0, --heapSize);
        while (heapSize > 0) {
            heapify(arr, 0, heapSize); // 重新调整大顶堆
            swap(arr, 0, --heapSize);
        }
    }

    private static void heapify(int[] arr, int index, int heapSize) {
        int left = index * 2 + 1; // 左子节点
        while (left < heapSize) {
            int largest = left + 1 < heapSize && arr[left + 1] > arr[left] ? left + 1 : left;
            largest = arr[largest] > arr[index] ? largest : index;
            if (largest == index) {
                break;
            }
            swap(arr, largest, index);
            // 重新更新节点
            index = largest;
            left = index * 2 + 1;
        }
    }

    private static void heapInsert(int[] arr, int index) {
        while (arr[index] > arr[(index - 1) / 2]) {  // 改元素大于 父节点的大小, 更新父节点
            swap(arr, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }

    public static void swap(int[] arr, int l, int r) {
        int tmp = arr[r];
        arr[r] = arr[l];
        arr[l] = tmp;
    }

    public int[] qieuc(int[] arr, int l, int r) {
        int less = l - 1;
        int more = r;
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, ++less, l++);
            } else if (arr[l] > arr[r]) {
                swap(arr, --more, l);
            } else {
                l++;
            }
        }
        swap(arr, more, r);
        return new int[]{less + 1, more};
    }
}
