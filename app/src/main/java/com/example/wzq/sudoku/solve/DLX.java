package com.example.wzq.sudoku.solve;

import java.util.ArrayList;
import java.util.List;

/**
 * Dancing links
 * A data structure
 * The main solver of sudoku
 * It works well in almost all cases
 * Using Dancing Links X algorithm to solve
 *
 * @author wzq20
 */
public class DLX {

    /**
     * The extra cell at the top left , is the root of the whole data structure
     */
    private final DLXNode head = new DLXNode(-1, -1);
    /**
     * store the column headers
     */
    private List<DLXNode> headList = new ArrayList<>();


    /**
     * Record how many elements(1) in each column
     * 324  = 9*9*4
     */
    private int[] counts = new int[324];

    public boolean solve(int x, int y, int lastVal, int curVal) {
        int pos = x * 9 + y;
        int row = pos * 9 + lastVal;
        DLXNode head = headList.get(pos);
        DLXNode cur = head.getDown();
        DLXNode tmp;
        DLXNode last;

        // find current node to delete
        while (head != cur) {
            if (cur.getRow() == row) {
                break;
            } else {
                cur = cur.getDown();
            }
        }
        last  = cur;
        cur.removeVertical();
        counts[cur.getCol()]--;
        tmp = cur.getRight();
        while (tmp != cur) {
            tmp.removeVertical();
            counts[tmp.getCol()]--;
            tmp = tmp.getRight();
        }


        row = pos * 9 + curVal;
        cur = head.getDown();
        while (head != cur) {
            if (cur.getRow() < row && cur.getDown().getRow() > row) {
                break;
            } else {
                cur = cur.getDown();
            }
        }
        // TODO: 2020/12/23 need optimisation
        DLXNode posNode = new DLXNode(row, pos);

        posNode.setUp(cur);
        posNode.setDown(cur.getDown());
        cur.getDown().setUp(posNode);
        cur.setDown(posNode);

        counts[pos]++;

        int col2 = 81 + x * 9 + curVal;
        DLXNode rowNode = new DLXNode(row, col2);
        DLXNode headRow = headList.get(col2);
        cur = headRow.getDown();
        while (headRow != cur) {
            if (cur.getRow() < row ) {
                if(cur.getDown() == headRow || cur.getDown().getRow()>row) {
                    break;
                }

            } else {
                cur = cur.getDown();
            }
        }
        // TODO: 2020/12/24

        rowNode.setUp(cur);
        rowNode.setDown(cur.getDown());
        cur.getDown().setUp(rowNode);
        cur.setDown(rowNode);

        counts[col2]++;

        int col3 = 162 + y * 9 + curVal;
        DLXNode colNode = new DLXNode(row, col3);
        DLXNode headCol = headList.get(col3);
        cur = headCol.getDown();

        while (headCol != cur) {
            if (cur.getRow() < row ) {
                if(cur.getDown() == headCol || cur.getDown().getRow()>row) {
                    break;
                }

            } else {
                cur = cur.getDown();
            }
        }
        colNode.setUp(cur);
        colNode.setDown(cur.getDown());
        cur.getDown().setUp(colNode);
        cur.setDown(colNode);


        counts[col3]++;

        int block = x / 3 * 3 + y / 3;
        int col4 = 243 + block * 9 + curVal;
        DLXNode blockNode = new DLXNode(row, col4);
        DLXNode headBlock = headList.get(col4);
        cur = headBlock.getDown();

        while (headBlock != cur) {
            if (cur.getRow() < row ) {
                if(cur.getDown() == headBlock || cur.getDown().getRow()>row) {
                    break;
                }

            } else {
                cur = cur.getDown();
            }
        }
        cur.getDown().setUp(blockNode);
        cur.setDown(blockNode);
        blockNode.setUp(cur);
        blockNode.setDown(cur.getDown());
        counts[col4]++;

        posNode.addRight(rowNode);
        rowNode.addRight(colNode);
        colNode.addRight(blockNode);
        blockNode.addRight(posNode);

        boolean ret = dance();
        cur = posNode;
        cur.removeVertical();
        counts[cur.getCol()] --;
        tmp = cur.getRight();
        while(tmp!= cur) {
            tmp.removeVertical();
            counts[tmp.getCol()] --;
            tmp = tmp.getRight();
        }

        last.recoverVertical();
        counts[last.getCol()] ++;

        tmp = last.getRight();
        while(tmp!=last) {
            tmp.recoverVertical();
            counts[tmp.getCol()] ++;
            tmp = tmp.getRight();
        }

        return ret;
    }

    /**
     * Convert array to linked list(DLX)
     *
     * @param map the two-dimensional array of sudoku
     */
    public void init(int[][] map) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int num = map[i][j];
                // if num>0,indicates that there is only one option for this location,add 1 row
                if (num > 0) {
                    addLine(i, j, num - 1);
                } else {
                    // there have 9 choice ,can add 9 row
                    for (int k = 0; k < 9; k++) {
                        addLine(i, j, k);
                    }
                }
            }
        }
    }

    public boolean dance() {
        DLXNode cur = head.getRight();
        // indicate the matrix A has no columns,
        // the current partial solution is a valid solution; terminate successfully.
        if (cur == head) {
            return true;
        }

        int minCount = counts[cur.getCol()];

        // select a column with the smallest number of 1s
        for (DLXNode t = head.getRight(); t != head; t = t.getRight()) {
            int tCount = counts[t.getCol()];
            if (tCount < minCount) {
                minCount = tCount;
                cur = t;
            }
        }

        removeCol(cur.getCol());

        DLXNode first = cur.getDown();
        while (first != cur) {

            DLXNode second = first.getRight();

            while (second != first) {
                removeCol(second.getCol());
                second = second.getRight();
            }

            if (dance()) {
                return true;
            }
            // wrong solution
            second = first.getLeft();
            while (second != first) {
                recoverCol(second.getCol());
                second = second.getLeft();
            }

            first = first.getDown();
        }

        recoverCol(cur.getCol());
        return false;
    }


    private void removeCol(int col) {
        DLXNode colNode = headList.get(col);
        colNode.removeHorizon();
        DLXNode first = colNode.getDown();
        while (first != colNode) {
            // remove selected row
            DLXNode second = first.getRight();
            while (second != first) {
                second.removeVertical();
                counts[second.getCol()]--;
                second = second.getRight();
            }
            first = first.getDown();
        }
    }

    private void addRow(int row) {

    }

    private void removeRow(int row) {

    }

    private void recoverCol(int col) {
        DLXNode colNode = headList.get(col);
        colNode.recoverHorizon();
        DLXNode first = colNode.getUp();
        while (first != colNode) {
            DLXNode second = first.getRight();
            while (second != first) {
                second.recoverVertical();
                counts[second.getCol()]++;
                second = second.getRight();
            }
            first = first.getUp();
        }
    }

    /**
     * Initialize the headList
     */
    public void reset() {
        headList.clear();
        DLXNode lastNode = head;
        DLXNode node = null;
        for (int i = 0; i < 324; i++) {
            node = new DLXNode(-1, i);
            lastNode.addRight(node);
            lastNode = node;
            headList.add(node);
            counts[i] = 0;
        }
        node.addRight(head);
    }

    /**
     * @param x map[x][]
     * @param y map[][y]
     * @param k add k lines
     */
    private void addLine(int x, int y, int k) {
        // pos: the position of (x,y) in map
        int pos = x * 9 + y;
        // TODO: 2020/12/23 : row should not equal pos * 9+k, maybe equal last row + k
        int row = pos * 9 + k;

        // add pos index, 0<=pos index<81
        int col1 = pos;
        DLXNode posNode = new DLXNode(row, col1);
        DLXNode headPos = headList.get(col1);
        DLXNode headPosU = headPos.getUp();
        headPos.addUp(posNode);
        headPosU.addDown(posNode);
        counts[col1]++;

        // add row index ,81<=row index< 162
        int col2 = 81 + x * 9 + k;
        DLXNode rowNode = new DLXNode(row, col2);
        DLXNode headRow = headList.get(col2);
        DLXNode headRowU = headRow.getUp();
        headRow.addUp(rowNode);
        headRowU.addDown(rowNode);
        counts[col2]++;

        // add col index , 162<= col index <243
        int col3 = 162 + y * 9 + k;
        DLXNode colNode = new DLXNode(row, col3);
        DLXNode headCol = headList.get(col3);
        DLXNode headColU = headCol.getUp();
        headCol.addUp(colNode);
        headColU.addDown(colNode);
        counts[col3]++;

        // add block index 243<= block index <324
        int block = x / 3 * 3 + y / 3;
        int col4 = 243 + block * 9 + k;
        DLXNode blockNode = new DLXNode(row, col4);
        DLXNode headBlock = headList.get(col4);
        DLXNode headBlockU = headBlock.getUp();
        headBlock.addUp(blockNode);
        headBlockU.addDown(blockNode);
        counts[col4]++;

        posNode.addRight(rowNode);
        rowNode.addRight(colNode);
        colNode.addRight(blockNode);
        blockNode.addRight(posNode);
    }
}
