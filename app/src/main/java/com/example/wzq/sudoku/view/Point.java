package com.example.wzq.sudoku.view;

public class Point {
    private int row;
    private int col;
    private int value;

    public Point(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }


    public int getValue() {
        return value;
    }

}
