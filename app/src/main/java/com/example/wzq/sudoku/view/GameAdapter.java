package com.example.wzq.sudoku.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.R;
import com.example.wzq.sudoku.utils.Generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The adapter and controller of sudoku
 * contains all info of this game
 * provide collision checker, highlight function and so on.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameHolder> {

    private static final String NO_CLICK = "noClick";

    private static final String CLICK_AROUND = "clickAround";

    private static final String CLICK_SAME = "clickSame";

    private static final String ERROR_SAME = "errSame";

    private  Set<Integer> errNumSet ;

    private GameAdapter.GameHolder clicked;

    private List<Point> data;
    private int[][] map = new int[9][9];

    private boolean[] canChange = new boolean[81];

    private int curNum = 0;
    private Resources resources;
    private RecyclerView.LayoutManager layoutManager;


    public GameAdapter(Context context, List<Point> data, RecyclerView.LayoutManager layoutManager) {
        this.data = data;
        resources = context.getResources();
        this.layoutManager = layoutManager;
        errNumSet = new HashSet<>();
    }


    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_item, parent, false);
        GameHolder holder = new GameHolder(view);
        view.setOnClickListener(v -> {
            // 如果这次点击的与上次相同，则取消上次点击状态
            if (holder == clicked) {
                setClickedAroundColor(NO_CLICK);
                setClickSameColor(NO_CLICK);
                if (errNumSet.contains(clicked.getAdapterPosition())) {
                    clicked.itemView.setBackgroundColor(resources.getColor(R.color.errorSame));
                } else {
                    clicked.itemView.setBackgroundColor(resources.getColor(R.color.noClick));
                }
                clicked = null;
            } else {
                // 如果上次点击不为0，则取消上次点击状态
                if (clicked != null) {
                    setClickedAroundColor(NO_CLICK);
                    setClickSameColor(NO_CLICK);
                    // 如果上次点击的是不合法的块，则设置为...
                    if (errNumSet.contains(clicked.getAdapterPosition())) {
                        clicked.itemView.setBackgroundColor(
                                resources.getColor(R.color.errorSame));
                    } else {
                        clicked.itemView.setBackgroundColor(
                                resources.getColor(R.color.noClick));
                    }
                }
                clicked = holder;
                setClickedAroundColor(CLICK_AROUND);
                setClickSameColor(CLICK_SAME);
                view.setBackgroundColor(resources.getColor(R.color.clicked));
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull GameHolder holder, int position) {
        Point point = data.get(position);
        holder.setValue(point.getValue());
        holder.itemView.setBackgroundColor(resources.getColor(R.color.noClick));
    }

    public void click(int number) {

        if (clicked == null) {
            return;
        }
        int position = clicked.getAdapterPosition();

        int x = position / 9;
        int y = position % 9;
        if (!canChange[position]) {
            return;
        }
        if (number == 0) {
            // 如果删除的数是不合法数字
            if (!Generator.isLegal(map, x, y, map[x][y])) {
                setErrorSameColor(CLICK_AROUND);
                errNumSet.remove(position);
            }
            setClickSameColor(NO_CLICK);
            setClickedAroundColor(CLICK_AROUND);
            clicked.textView.setText("");
            clicked.setValue(0);
            map[x][y] = 0;
            return;
        }
        if (Generator.isLegal(map, x, y, number)) {
            map[x][y] = number;
            clicked.textView.setTextColor(resources.getColor(R.color.inputNum));
            setClickSameColor(CLICK_SAME);
        } else {
            setErrorSameColor(CLICK_AROUND);
            errNumSet.add(position);
            map[x][y] = number;
            clicked.textView.setTextColor(resources.getColor(R.color.errorNum));
            setErrorSameColor(ERROR_SAME);
        }
        clicked.setValue(number);
    }


    public void setMap(int[][] map) {
        curNum = 0;
        this.map = map;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int pos = i * 9 + j;
                if (map[i][j] > 0) {
                    canChange[pos] = false;
                    curNum++;
                } else {
                    canChange[pos] = true;
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isFull() {
        return curNum == 81;
    }

    private void setClickedAroundColor(String s) {
        int colorId = 0;

        switch (s) {
            case NO_CLICK:
                colorId = resources.getColor(R.color.noClick);
                break;
            case CLICK_AROUND:
                colorId = resources.getColor((R.color.click_around));
                break;
            default:
        }
        int position = 0, tmpPos = 0;
        int x, y;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;

        for (int i = 0; i < 9; i++) {
            tmpPos = x * 9 + i;
            if (i != y) {
                if (!errNumSet.contains(tmpPos)) {
                    layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            tmpPos = i * 9 + y;
            if (i != x) {
                if (!errNumSet.contains(tmpPos)) {
                    layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
                }
            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (int i = minX; i < minX + 3; i++) {
            for (int j = minY; j < minY + 3; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y) {
                    if (!errNumSet.contains(tmpPos)) {
                        layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
                    }
                }
            }
        }
    }

    private void setErrorSameColor(String s) {

        int colorId = 0;
        switch (s) {
            case CLICK_AROUND:
                colorId = resources.getColor(R.color.click_around);
                break;
            case ERROR_SAME:
                colorId = resources.getColor(R.color.errorSame);
                break;
            default:
        }
        int position = 0, tmpPos = 0;
        int x, y;
        int num;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;
        num = map[x][y];


        for (int i = 0; i < 9; i++) {
            tmpPos = x * 9 + i;
            if (i != y && map[x][i] == num) {
                if (s.equals(CLICK_AROUND)) {
                    errNumSet.remove(tmpPos);
                } else if (s.equals(ERROR_SAME)) {
                    errNumSet.add(tmpPos);
                }
                layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
            }
        }
        for (int i = 0; i < 9; i++) {
            tmpPos = i * 9 + y;
            if (i != x && map[i][y] == num) {
                if (s.equals(CLICK_AROUND)) {
                    errNumSet.remove(tmpPos);
                } else if (s.equals(ERROR_SAME)) {
                    errNumSet.add(tmpPos);
                }
                layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (int i = minX; i < minX + 3; i++) {
            for (int j = minY; j < minY + 3; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y && map[i][j] == num) {
                    if (s.equals(CLICK_AROUND)) {
                        errNumSet.remove(tmpPos);
                    } else if (s.equals(ERROR_SAME)) {
                        errNumSet.add(tmpPos);
                    }
                    layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
                }
            }
        }
    }

    private void setClickSameColor(String s) {

        int colorId = 0;

        switch (s) {
            case NO_CLICK:
                colorId = resources.getColor(R.color.noClick);
                break;
            case CLICK_SAME:
                colorId = resources.getColor((R.color.click_same));
                break;
            default:
        }
        int position = 0, tmpPos = 0;
        int x, y;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;
        int value = map[x][y];
        if (value != 0) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (map[i][j] == value && (i * 9 + j) != position) {
                        tmpPos = i * 9 + j;
                        if (!errNumSet.contains(tmpPos)) {
                            layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);
                        }
                    }
                }
            }
        }
    }

    class GameHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private int value;

        public GameHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell_text);
        }

        public void setValue(int value) {
            this.value = value;
            textView.setText(value > 0 ? String.valueOf(value) : "");
        }
    }
}
