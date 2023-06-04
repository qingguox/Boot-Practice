package com.xlg.component;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.xlg.component.utils.Sort;

/**
 *
 * Created on 2023-05-16
 */
public class TestSort {

    @Test
    public void mergeSort() {
        int[] arr = {1, 2, 9, 2, 3};
        Sort.mergeSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void quickSort() {
        int[] arr = {1, 2, 9, 2, 3};
        Sort.quickSort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
