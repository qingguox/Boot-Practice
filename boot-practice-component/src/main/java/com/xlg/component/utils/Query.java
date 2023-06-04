package com.xlg.component.utils;

/**
 *
 * Created on 2023-05-16
 */
public class Query {


    /**
     * 有序且升序,  递归版本
     * 二分查找, 返回index
     * @param arr
     * @return
     */
    public static int binarySearch(int[] arr, int target, int left, int right) {
        if (left > right) {
            return -1;
        }
        int mid = left + ((right - left) >> 1);
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return binarySearch(arr, target, left, mid - 1);
        } else {
            return binarySearch(arr, target, mid + 1, right);
        }
    }

    // 非递归版本直接 while ( left <= right)

}


