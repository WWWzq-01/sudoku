package com.example.wzq.sudoku.utils;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
    /**
     * the width of sudoku map
     */
    private static final int MAP_WIDTH = 9;
    /**
     * the width of block
     */
    private static final int BLOCK_WIDTH = 3;

    private static int[][] tempMap = new int[MAP_WIDTH][MAP_WIDTH];

    private static int[][] endMap = new int[MAP_WIDTH][MAP_WIDTH];

    /**
     * Determine whether the generation is complete
     */
    private static Boolean finished = false;

    /**
     *
     */
    public static Boolean isLegal(int[][] a, int x, int y, int value) {
        for (int i = 0; i < MAP_WIDTH; i++) {
            //如果列中有value，则返回false
            if (i != x && a[i][y] == value) {
                return false;
            }
            //如果行中有value，则返回false
            if (i != y && a[x][i] == value) {
                return false;
            }
        }

        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (int i = minX; i < minX + BLOCK_WIDTH; i++) {
            for (int j = minY; j < minY + BLOCK_WIDTH; j++) {
                //如果小九宫格中的非(x,y)的坐标上的值为value，返回false
                if (i != x && j != y && a[i][j] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public static int[][] generate(int num) {
        generateRec(0);

        Random random = new Random();
        ArrayList<Integer> list = new ArrayList<>(35);
        for (int i = 0; i < num; i++) {
            int index = random.nextInt(81);
            while (list.contains(index)) {
                index = random.nextInt(81);
            }
            list.add(index);
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (list.contains(i * 9 + j)) {
                    endMap[i][j] = 0;
                }
            }
        }
        finished = false;
        return endMap;
    }

    private static void generateRec(int k) {
        if (finished) {
            return;
        }
        if (k == 81) {

            for (int i = 0; i < 9; i++) {
                System.arraycopy(tempMap[i], 0, endMap[i], 0, 9);
                System.out.println();
            }
                finished = true;
            return;
        }

        // 取得第k+1个值所对应的坐标(x,y),k是从0开始的。
        int x = k / 9;
        int y = k % 9;

        if (tempMap[x][y] == 0) {
            // index用来判断是否已经完全随机生成了1-9这个9个数
            int index = 0;
            while (index < MAP_WIDTH) {
                // list用来储存已经随机生成的1-9的数字
                ArrayList<Integer> list = new ArrayList<>(9);
                Random random = new Random();
                int i = random.nextInt(9) + 1;
                // 当list中包含数字i时，再重新生成1-9的数字
                while (list.contains(i)) {
                    i = random.nextInt(9) + 1;
                }
                list.add(i);
                index++;
                tempMap[x][y] = i;
                // isLegal()函数是判断在九宫格中的坐标(x,y)的位置上插入i，是否符合规则
                if (isLegal(tempMap, x, y, i)) {
                    generateRec(k + 1);
                }
            }
            // 回溯时，将坐标(x,y)的值置零
            tempMap[x][y] = 0;
        } else {
            generateRec(k + 1);
        }
    }
}
