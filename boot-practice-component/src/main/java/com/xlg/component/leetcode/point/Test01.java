package com.xlg.component.leetcode.point;

/**
 * @author wangqingwei
 * Created on 2021-06-01
 */
public class Test01 {

    public boolean Find(int target, int [][] array) {
        int rows = array.length;
        int cols = array[0].length;
        int i = 0;
        int j = cols - 1;
        while (i < rows && j >= 0) {
            if (array[i][j] == target) {
                return true;
            } else if (array[i][j] > target) {
                j--;
            } else {
                i++;
            }
        }
        return false;
    }

    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *输入：
     * "We Are Happy"
     * 复制
     * 返回值：
     * "We%20Are%20Happy
     *
     * @param s string字符串
     * @return string字符串
     */
    public String replaceSpace (String s) {
        return s.replace(" ", "%20");
    }


    public static int findRepeatNumber(int[] nums) {
        int temp;
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != i) {
                if (nums[i] == nums[nums[i]]) {
                    return nums[i];
                }
                temp = nums[i];
                nums[i] = nums[temp];
                nums[temp] = temp;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        long l = 1623873600000L;
        long k = 1624996800000L;

        System.out.println(k - l);
    }
}
