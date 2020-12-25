package com.example.wzq.sudoku.solve;

public class DLXNode {

    private DLXNode up;
    private DLXNode down;
    private DLXNode left;
    private DLXNode right;
    private int row;
    private int col;

    public DLXNode(int row, int col) {
        this.row = row;
        this.col = col;
        up = down = left = right = this;
    }

    public DLXNode getUp() {
        return up;
    }

    public void setUp(DLXNode up) {
        this.up = up;
    }

    public DLXNode getDown() {
        return down;
    }

    public void setDown(DLXNode down) {
        this.down = down;
    }

    public DLXNode getLeft() {
        return left;
    }

    public void setLeft(DLXNode left) {
        this.left = left;
    }

    public DLXNode getRight() {
        return right;
    }

    public void setRight(DLXNode right) {
        this.right = right;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void addRight(DLXNode right) {
        this.right = right;
        right.setLeft(this);
    }

    public void addLeft(DLXNode left) {
        this.left = left;
        left.setRight(this);
    }

    public void addUp(DLXNode up) {
        this.up = up;
        up.setDown(this);
    }

    public void addDown(DLXNode down) {
        this.down = down;
        down.setUp(this);
    }

    public void removeHorizon() {
        left.setRight(right);
        right.setLeft(left);
    }

    public void removeVertical() {
        up.setDown(down);
        down.setUp(up);
    }

    public void recoverHorizon() {
        left.setRight(this);
        right.setLeft(this);
    }

    public void recoverVertical() {
        up.setDown(this);
        down.setUp(this);
    }
}
