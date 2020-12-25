package com.example.wzq.sudoku.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.R;
import com.example.wzq.sudoku.utils.Callback;
import com.example.wzq.sudoku.utils.ColorHelper;
import com.example.wzq.sudoku.utils.Generator;
import com.example.wzq.sudoku.view.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;

/**
 * The adapter and controller of sudoku
 * contains all info of this game
 * provide collision checker, highlight function and so on.
 *
 * @author wzq20
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameHolder> {

    private final String NO_CLICK = "noClick";

    private final String CLICK_AROUND = "clickAround";

    private final String CLICK_SAME = "clickSame";

    private final String ERROR_SAME = "errSame";

    private final int MAP_SIDE_LEN = 9;

    private final int BLOCK_SIDE_LEN = 3;
    private final List<Point> data;
    private final boolean[] canChange = new boolean[81];
    private final boolean[] initialized = new boolean[81];
    private Set<Integer> errNumSet;
    private GameHolder clicked;
    private int[][] map = new int[9][9];
    private int[][] resMap = new int[9][9];
    private int curNum = 0;
    private Callback callback;
    private Context context;
    private NoteAdapter[] noteAdapters = new NoteAdapter[81];
    private GameHolder[] gameHolders = new GameHolder[81];
    private boolean isNoting = false;
    private ColorHelper colorHelper;

    public GameAdapter(Context context, List<Point> data) {
        this.data = data;
        colorHelper = new ColorHelper(context);
        errNumSet = new HashSet<>();
        this.context = context;
    }

    public void setResMap(int[][] resMap) {
        this.resMap = resMap;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_item, parent, false);

        return new GameHolder(view);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull GameHolder holder, int position) {
        Point point = data.get(position);
        holder.setValue(point.getValue());
        gameHolders[position] = holder;
        holder.itemView.setBackgroundColor(colorHelper.NO_CLICK);
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (isNoting && canChange[pos]) {
                if (!initialized[pos]) {
                    NoteAdapter noteAdapter = new NoteAdapter();
                    holder.notes.setAdapter(noteAdapter);
                    noteAdapters[pos] = noteAdapter;
                    holder.notes.setOnTouchListener((v1, motionEvent) -> {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            return holder.itemView.callOnClick();
                        }
                        return false;
                    });
                    holder.notes.setLayoutManager(new GridLayoutManager(context, 3));
                    initialized[pos] = true;
                }
                holder.notes.setVisibility(View.VISIBLE);
                holder.textView.setVisibility(View.GONE);
            }

            callback = noteAdapters[pos];

//             如果这次点击的与上次相同，则取消上次点击状态
            if (holder == clicked) {
                setClickedAroundColor(NO_CLICK);
                setClickSameColor(NO_CLICK);
                if (errNumSet.contains(clicked.getAdapterPosition())) {
                    clicked.itemView.setBackgroundColor(colorHelper.ERROR_SAME);
                } else {
                    clicked.itemView.setBackgroundColor(colorHelper.NO_CLICK);
                }
                clicked = null;
            } else {
                // 如果上次点击不为空，则取消上次点击状态,并且选择新的
                if (clicked != null) {
                    setClickedAroundColor(NO_CLICK);
                    setClickSameColor(NO_CLICK);

                    // 如果上次点击的是不合法的块，则设置为...

                    if (errNumSet.contains(clicked.getAdapterPosition())) {
                        clicked.itemView.setBackgroundColor(
                                colorHelper.ERROR_SAME);
                    } else {
                        clicked.itemView.setBackgroundColor(
                                colorHelper.NO_CLICK);
                    }
                }
                clicked = holder;
                setClickedAroundColor(CLICK_AROUND);
                setClickSameColor(CLICK_SAME);
                holder.itemView.setBackgroundColor(colorHelper.CLICKED);
            }
        });
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
        if (noteAdapters[position] != null) {
            noteAdapters[position].setNull();
        }
        clicked.notes.setVisibility(View.GONE);
        clicked.textView.setVisibility(View.VISIBLE);
        if (number == 0) {
            delete(x, y);
            return;
        }
        if(number == map[x][y] ) {
            return;
        }
        if (!Generator.isLegal(map, x, y, map[x][y])) {
            errNumSet.remove(x * 9 + y);
        }
        setLastNumColor();

        if (Generator.isLegal(map, x, y, number)) {
            map[x][y] = number;
            curNum++;
            clicked.textView.setTextColor(colorHelper.INPUT_NUM);
            setClickSameColor(CLICK_SAME);
        } else {
            setErrorSameColor(CLICK_AROUND);
            curNum--;
            errNumSet.add(position);
            map[x][y] = number;
            clicked.textView.setTextColor(colorHelper.ERROR_NUM);
            setErrorSameColor(ERROR_SAME);
        }
        clicked.setValue(number);
    }

    private void setLastNumColor() {
        int position = clicked.getAdapterPosition();
        int x = position / 9;
        int y = position % 9;
        int i, j;
        boolean equal;
        for (i = 0; i < MAP_SIDE_LEN; i++) {
            for (j = 0; j < MAP_SIDE_LEN; j++) {
                equal = map[i][j] == map[x][y] && (i != x && j != y);
                if (equal) {
                    gameHolders[i * 9 + j].itemView.setBackgroundColor(colorHelper.NO_CLICK);
                }
            }
        }
        int tmpPos;
        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = x * 9 + i;
            if (i != y) {
                errNumSet.remove(tmpPos);
                gameHolders[tmpPos].itemView.setBackgroundColor(colorHelper.CLICK_AROUND);
            }
        }
        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = i * 9 + y;
            if (i != x) {
                errNumSet.remove(tmpPos);
                gameHolders[tmpPos].itemView.setBackgroundColor(colorHelper.CLICK_AROUND);

            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (i = minX; i < minX + BLOCK_SIDE_LEN; i++) {
            for (j = minY; j < minY + BLOCK_SIDE_LEN; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y) {
                    errNumSet.remove(tmpPos);
                    gameHolders[tmpPos].itemView.setBackgroundColor(colorHelper.CLICK_AROUND);

                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void note(int number) {
        if (clicked == null) {
            return;
        }

        int position = clicked.getAdapterPosition();
        int x = position / 9;
        int y = position % 9;
        if (!canChange[position]) {
            return;
        }
        //
        delete(x, y);
        //
        clicked.textView.setVisibility(View.GONE);
        clicked.notes.setVisibility(View.VISIBLE);


        if (!initialized[position]) {
            NoteAdapter noteAdapter = new NoteAdapter();
            clicked.notes.setAdapter(noteAdapter);
            noteAdapters[position] = noteAdapter;
            clicked.notes.setOnTouchListener((v1, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    return clicked.itemView.callOnClick();
                }
                return false;
            });
            clicked.notes.setLayoutManager(new GridLayoutManager(context, 3));
            initialized[position] = true;
        }
        callback = noteAdapters[position];

        callback.onClick(number);
    }


    public void setMap(int[][] map) {
        curNum = 0;
        this.map = map;
        for (int i = 0; i < MAP_SIDE_LEN; i++) {
            for (int j = 0; j < MAP_SIDE_LEN; j++) {
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
        int colorId;
        int i, j;
        switch (s) {
            case NO_CLICK:
                colorId = colorHelper.NO_CLICK;
                break;
            case CLICK_AROUND:
                colorId = colorHelper.CLICK_AROUND;
                break;
            default:
                colorId = 0;
        }
        int position, tmpPos;
        int x, y;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;

        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = x * 9 + i;
            if (i != y) {
                if (!errNumSet.contains(tmpPos)) {
                    gameHolders[tmpPos].itemView.setBackgroundColor(colorId);

                }
            }
        }
        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = i * 9 + y;
            if (i != x) {
                if (!errNumSet.contains(tmpPos)) {
                    gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
                }
            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (i = minX; i < minX + BLOCK_SIDE_LEN; i++) {
            for (j = minY; j < minY + BLOCK_SIDE_LEN; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y) {
                    if (!errNumSet.contains(tmpPos)) {
                        gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
                    }
                }
            }
        }
    }

    private void setErrorSameColor(String s) {

        int colorId;
        int i, j;
        switch (s) {
            case CLICK_AROUND:
                colorId = colorHelper.CLICK_AROUND;
                break;
            case ERROR_SAME:
                colorId = colorHelper.ERROR_SAME;
                break;
            default:
                colorId = 0;
        }
        int position, tmpPos;
        int x, y;
        int num;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;
        num = map[x][y];


        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = x * 9 + i;
            if (i != y && map[x][i] == num) {
                if (s.equals(CLICK_AROUND)) {
                    errNumSet.remove(tmpPos);
                } else if (s.equals(ERROR_SAME)) {
                    errNumSet.add(tmpPos);
                }

                gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
            }
        }
        for (i = 0; i < MAP_SIDE_LEN; i++) {
            tmpPos = i * 9 + y;
            if (i != x && map[i][y] == num) {
                if (s.equals(CLICK_AROUND)) {
                    errNumSet.remove(tmpPos);
                } else if (s.equals(ERROR_SAME)) {
                    errNumSet.add(tmpPos);
                }
                gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (i = minX; i < minX + BLOCK_SIDE_LEN; i++) {
            for (j = minY; j < minY + BLOCK_SIDE_LEN; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y && map[i][j] == num) {
                    if (s.equals(CLICK_AROUND)) {
                        errNumSet.remove(tmpPos);
                    } else if (s.equals(ERROR_SAME)) {
                        errNumSet.add(tmpPos);
                    }
                    gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
                }
            }
        }
    }

    private void setClickSameColor(String s) {

        int colorId;

        switch (s) {
            case NO_CLICK:
                colorId = colorHelper.NO_CLICK;
                break;
            case CLICK_SAME:
                colorId = colorHelper.CLICK_SAME;
                break;
            default:
                colorId = 0;
        }
        int position, tmpPos;
        int x, y;
        int i, j;
        position = clicked.getAdapterPosition();
        x = position / 9;
        y = position % 9;
        int value = map[x][y];
        if (value != 0) {
            for (i = 0; i < MAP_SIDE_LEN; i++) {
                for (j = 0; j < MAP_SIDE_LEN; j++) {
                    if (map[i][j] == value && (i * 9 + j) != position) {
                        tmpPos = i * 9 + j;
                        if (!errNumSet.contains(tmpPos)) {
                            gameHolders[tmpPos].itemView.setBackgroundColor(colorId);
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setNotes(boolean bl) {
        isNoting = bl;
        if (clicked == null) {
            return;
        }

        // 如果已经有点击的子项，
        int position = clicked.getAdapterPosition();
        if (!canChange[position]) {
            return;
        }
        int x = position / 9;
        int y = position % 9;
        GameHolder tmp = clicked;
        if (bl) {
            if (!initialized[position]) {
                NoteAdapter noteAdapter = new NoteAdapter();
                clicked.notes.setAdapter(noteAdapter);
                noteAdapters[position] = noteAdapter;
                clicked.notes.setOnTouchListener((v1, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        return tmp.itemView.performClick();
                    }
                    return false;
                });
                clicked.notes.setLayoutManager(new GridLayoutManager(context, 3));
                initialized[position] = true;
            }
            delete(x, y);
            clicked.textView.setVisibility(View.GONE);
            clicked.notes.setVisibility(View.VISIBLE);

        }

    }

    private void delete(int x, int y) {
        // 如果删除的数是不合法数字
        if (!Generator.isLegal(map, x, y, map[x][y])) {
            setClickSameColor(NO_CLICK);
            setErrorSameColor(CLICK_AROUND);
            errNumSet.remove(x * 9 + y);
        } else {
            // 如果删除的数合法，
            setClickSameColor(NO_CLICK);
            setClickedAroundColor(CLICK_AROUND);
            curNum--;
        }

        clicked.textView.setText("");
        clicked.setValue(0);
        map[x][y] = 0;
    }

    public void hint() {
        if (clicked == null) {
            return;
        }
        int pos = clicked.getAdapterPosition();
        if (!canChange[pos]) {
            return;
        }
        if (noteAdapters[pos] != null) {
            noteAdapters[pos].setNull();
        }
        clicked.notes.setVisibility(View.GONE);
        clicked.textView.setVisibility(View.VISIBLE);
        clicked.textView.setTextColor(colorHelper.HINT_NUM);
        setLastNumColor();
        canChange[pos] = false;

        int x, y;
        x = pos / 9;
        y = pos % 9;
        int value = resMap[x][y];
        curNum++;

        map[x][y] = value;
        clicked.setValue(value);
    }

    static class GameHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final RecyclerView notes;

        private GameHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell_text);
            notes = itemView.findViewById(R.id.notes_list);
        }

        private void setValue(int value) {
            textView.setText(value > 0 ? String.valueOf(value) : "");
        }
    }
}
