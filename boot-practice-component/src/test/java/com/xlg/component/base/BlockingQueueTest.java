package com.xlg.component.base;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author wangqingwei
 * Created on 2022-07-29
 */
public class BlockingQueueTest {

    @Test
    public void testInit() {
        testQuick();
    }

    public void testQuick() {
        int[] arr = new int[]{1, 2,44, 55, 2, 44, 5};
        processQuickInner(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(Arrays.stream(arr).toArray()));
    }

    private void processQuickInner(int[] arr, int l, int r) {
        if (l < r) {
            swap(arr, (l + (int)(Math.random() * (r - l + 1))), r);
            int[] partition = processPartition(arr, l, r);
            processQuickInner(arr, l, partition[0] - 1);
            processQuickInner(arr, partition[1] + 1, r);
        }
    }

    private int[] processPartition(int[] arr, int l, int r) {
        int less = l - 1;
        int more = r;
        while (l < more) {
            if (arr[l] < arr[r]) {
                swap(arr, ++less, l++);
            } else if (arr[l] > arr[r]) {
                swap(arr, --more, l++);
            } else {
                l++;
            }
        }
        swap(arr, more, r);
        return new int[]{less+1, more};
    }


    public void swap(int[] arr, int l, int r) {
        int tmp = arr[r];
        arr[r] = arr[l];
        arr[l] = tmp;
    }

}
