package com.xlg.component.utils;

/**
 *
 * Created on 2023-05-16
 */
public class Sort {



    public static void quickSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        quickSortProcess(arr, 0, arr.length - 1);
    }

    private static void quickSortProcess(int[] arr, int l, int r) {
        if (l < r) {
            swap(arr, l + (int)(Math.random() * (r - l + 1)), r);
            int[] p = partition(arr, l, r);
            quickSortProcess(arr, l, p[0] - 1);
            quickSortProcess(arr, p[1] + 1, r);
        }
    }

    /**
     * 其实就是通过根据最后一个元素R比较.
     * 小于R放在左边，大于R放在右边
     * 切分出不同的快, 块间有序, 块内无序.  和归并排序思路相反.
     * 从上到下.
     */
    private static int[] partition(int[] arr, int l, int r) {
        int less = l - 1;
        int more = r;
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, ++less, l++);
            } else if (arr[l] > arr[r]) {
                // 交换之后依旧需要和l比较, 故l不加
                swap(arr, --more, l);
            } else {
                l++;
            }
        }
        swap(arr, more, r);
        return new int[]{less + 1, more};
    }


    public static void swap(int[] arr, int left, int right) {
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }

    /**
     * 归并排序算法.
     * @param arr
     */
    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        mergeSortProcess(arr, 0, arr.length - 1);
    }

    private static void mergeSortProcess(int[] arr, int l, int r) {
        if (l == r) {
            return;
        }
        int mid = l + ((r - l) >> 1);
        mergeSortProcess(arr, l, mid);
        mergeSortProcess(arr, mid + 1, r);
        mergeReal(arr, l, mid, r);
    }

    private static void mergeReal(int[] arr, int l, int mid, int r) {
        int[] help = new int[r - l + 1];
        int p1 = l; int p2 = mid + 1; int index = 0;
        while (p1 <= mid && p2 <= r) {
            help[index++] = arr[p1] < arr[p2]  ? arr[p1++] : arr[p2++];
        }
        while (p1 <= mid) {
            help[index++] = arr[p1++];
        }
        while (p2 <= r) {
            help[index++] = arr[p2++];
        }
        for (int curIndex = 0; curIndex < help.length; curIndex++) {
            arr[l + curIndex] = help[curIndex];
        }
    }


}
