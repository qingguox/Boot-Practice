package com.xlg.component.someBase.sort;

import java.util.Arrays;

/**
 * 几个比较抽象或者说特定思路的算法
 * @author qingguox
 * Created on 2025-02-16
 */
public class SpecialTest {


    public static void main(String[] args) {
        int[][] arr = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        printXy(arr);

        System.out.println("");
        printDoubleX(arr);
        rotate(arr);
        printDoubleX(arr);

        int[][] arr2 = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        printMatrixZigZag(arr2);
    }

    /**
     * 1. 打印正方形的四周
     * 1 2 3 4
     * 5 6 7 8
     * 9 10 11 12
     * 13 14 15 16
     *
     * print: 1 2 3 4 8 12 16 15 14 13 9 5 6 7 11 10
     * @param arr
     */
    public static void printXy(int[][] arr) {
        int tR = 0, tC = 0;
        int dR = arr.length - 1, dC = arr[0].length - 1;
        while (tR <= dR && tC <= dC) {
            printXyInner(arr, tR++, tC++, dR--, dC--);
        }
    }

    private static void printXyInner(int[][] arr, int tR, int tC, int dR, int dC) {
        if (tR == dR) {  // 是有一行
            for (int i = tC; i <= dC; i++) {
                System.out.print(arr[tR][i] + " ");
            }
        } else if (tC == dC) {  // 只有一列
            for (int i = tR; i <= dR; i++) {
                System.out.print(arr[i][tC] + " ");
            }
        } else {
            // 多行多列
            int curR = tR;
            int curC = tC;
            // 第一行
            while (curC != dC) {
                System.out.print(arr[tR][curC++] + " ");
            }
            // 最后一列
            while (curR != dR) {
                System.out.print(arr[curR++][dC] + " ");
            }
            // 最后一行
            while (curC != tC) {
                System.out.print(arr[dR][curC--] + " ");
            }
            // 第一列
            while (curR != tR) {
                System.out.print(arr[curR--][tC] + " ");
            }
        }
    }


    /**
     * 2. 正方形旋转90度
     * 1 2 3 4
     * 5 6 7 8
     * 9 10 11 12
     * 13 14 15 16
     *
     * 1->4 4-> 16 16 ->13 13->1
     *
     * @param arr
     */
    public static void rotate(int[][] arr) {
        int tR = 0, tC = 0, dR = arr.length - 1, dC = arr[0].length - 1;
        while (tC < dC) {
            rotateEdge(arr, tR++, tC++, dR--, dC--);
        }
    }

    private static void rotateEdge(int[][] arr, int tR, int tC, int dR, int dC) {
        int times = dC - tC;  // 其实只有3次 1 2 3
        int temp = 0;
        for (int i = 0; i < times; i++) {
            temp = arr[tR][tC + i]; // 第一行的
            arr[tR][tC + i] = arr[dR - i][tC]; // 第一列最后向上
            arr[dR - i][tC] = arr[dR][dC - i];
            arr[dR][dC - i] = arr[tR + i][dC];
            arr[tR + i][dC] = temp;
        }
    }

    public static void printDoubleX(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(Arrays.toString(arr[i]));
        }
    }

    /**
     * 3. 之字形打印数组
     * A 向右边走, 到了dC 向下走
     * B 向下走, 到了dR 向右走
     */
    public static void printMatrixZigZag(int[][] arr) {
        int aR = 0;
        int aC = 0;
        int bR = 0;
        int bC = 0;
        int endR = arr.length - 1;
        int endC = arr[0].length - 1;
        boolean fromUp = false; // 默认B到A
        while (aR != endR + 1) {
            printLevel(arr, aR, aC, bR, bC, fromUp);
            aR = aC == endC ? aR + 1 : aR;
            aC = aC == endC ? aC : aC + 1;
            bC = bR == endR ? bC + 1 : bC;
            bR = bR == endR ? bR : bR + 1;
            fromUp = !fromUp; // 转向
        }
    }

    private static void printLevel(int[][] arr, int aR, int aC, int bR, int bC, boolean fromUp) {
        if (fromUp) { // A -> B
            while (aR != bR + 1) {
                System.out.print(arr[aR++][aC--] + " ");
            }
        } else {
            while (bR != aR - 1) {
                System.out.print(arr[bR--][bC++] + " ");
            }
        }
    }

    /**
     * 3. 在一个横向正序, 纵向正序中找一个k
     */
    public static boolean isContains(int[][] matrix, int k) {
        int x = 0, y = matrix[0].length - 1; // 右上角
        while (x < matrix.length && y >= 0) {
            if (matrix[x][y] > k) {
                y--;
            } else if (matrix[x][y] < k) {
                x++;
            } else {
                return true;
            }
        }
        return false;
    }

}
