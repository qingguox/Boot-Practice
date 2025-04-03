package com.xlg.component.someBase.sort;

/**
 * @author qingguox
 * Created on 2025-02-16
 */
public class BinarySearchTest {
    public static void main(String[] args) {
        int[] arr = {1, 3, 8, 9, 10};
        int n = 8;
        int nIndex = binarySearchV2(arr, n, 0, arr.length - 1);
        System.out.println(nIndex);

        n = 20;
        nIndex = binarySearchV2(arr, n, 0, arr.length - 1);
        System.out.println(nIndex);
    }

    // 二分查找算法, 默认数值增序, 递归版本
    public static int binarySearch(int[] arr, int n, int l, int r) {
        if (l > r) {
            return -1;
        }
        int mid = (l + r) / 2; // 可能溢出
        if (arr[mid] == n) {
            return mid;
        } else if (arr[mid] < n) {  // 说明元素在右侧
            return binarySearch(arr, n, mid + 1, r);
        } else {
            return binarySearch(arr, n,  l, mid - 1);
        }
    }


    // 二分查找算法, 默认数值增序, 非递归版本
    public static int binarySearchV2(int[] arr, int n, int l, int r) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int mid = -1;
        while (l <= r) {
            mid = l + ((r - l) >> 1);
            if (arr[mid] == n) {
                return mid;
            } else if (arr[mid] < n) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return -1;
    }


}
