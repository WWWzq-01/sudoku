package com.example.wzq.sudoku.Adapter;

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
import com.example.wzq.sudoku.utils.ColorHelper;
import com.example.wzq.sudoku.utils.Generator;
import com.example.wzq.sudoku.view.Callback;
import com.example.wzq.sudoku.view.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The adapter and controller of sudoku
 * contains all info of this game
 * provide collision checker, highlight function and so on.
 *
 * @author wzq20
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameHolder> {

    private static final String NO_CLICK = "noClick";

    private static final String CLICK_AROUND = "clickAround";

    private static final String CLICK_SAME = "clickSame";

    private static final String ERROR_SAME = "errSame";

    private Set<Integer> errNumSet;

    private GameHolder clicked;

    private List<Point> data;
    private int[][] map = new int[9][9];

    private boolean[] canChange = new boolean[81];

    private int curNum = 0;

    private RecyclerView.LayoutManager layoutManager;

    private Callback callback;

    private Context context;

    private boolean[] initialized = new boolean[81];

    private NoteAdapter[] noteAdapters = new NoteAdapter[81];

//    private GameHolder [] gameHolders = new GameHolder[81];

    private boolean isNoting = false;

    private ColorHelper colorHelper;


    public GameAdapter(Context context, List<Point> data, RecyclerView.LayoutManager layoutManager) {
        this.data = data;
        colorHelper = new ColorHelper(context);
        this.layoutManager = layoutManager;
        errNumSet = new HashSet<>();
        this.context = context;
    }


    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public GameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_item, parent, false);
        final GameHolder holder = new GameHolder(view);

        view.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (isNoting && canChange[position]) {
                if (!initialized[position]) {
                    NoteAdapter noteAdapter = new NoteAdapter();
                    holder.notes.setAdapter(noteAdapter);
                    noteAdapters[position] = noteAdapter;
                    holder.notes.setOnTouchListener((v1, motionEvent) -> {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            return holder.itemView.callOnClick();
                        }
                        return false;
                    });
                    holder.notes.setLayoutManager(new GridLayoutManager(context, 3));
                    initialized[position] = true;
                }
                holder.notes.setVisibility(View.VISIBLE);
                holder.textView.setVisibility(View.GONE);
            }

            callback = noteAdapters[position];

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
                view.setBackgroundColor(colorHelper.CLICKED);
            }
        });
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull GameHolder holder, int position) {
        Point point = data.get(position);
        holder.setValue(point.getValue());
        holder.itemView.setBackgroundColor(colorHelper.NO_CLICK);
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
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                equal = map[i][j] == map[x][y] && (i != x && j != y);
                if (equal) {
                    layoutManager.findViewByPosition(i * 9 + j).setBackgroundColor(colorHelper.NO_CLICK);
                }
            }
        }
        int tmpPos;
        for (i = 0; i < 9; i++) {
            tmpPos = x * 9 + i;
            if (i != y) {
                errNumSet.remove(tmpPos);
                layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorHelper.CLICK_AROUND);
            }
        }
        for (i = 0; i < 9; i++) {
            tmpPos = i * 9 + y;
            if (i != x) {
                errNumSet.remove(tmpPos);
                layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorHelper.CLICK_AROUND);

            }
        }
        //(minX,minY)是(x,y)所属小九宫格的左上角的坐标
        int minX = x / 3 * 3;
        int minY = y / 3 * 3;

        for (i = minX; i < minX + 3; i++) {
            for (j = minY; j < minY + 3; j++) {
                tmpPos = i * 9 + j;
                if (i != x && j != y) {
                    errNumSet.remove(tmpPos);
                    layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorHelper.CLICK_AROUND);

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

        for (i = 0; i < 9; i++) {
            tmpPos = x * 9 + i;
            if (i != y) {
                if (!errNumSet.contains(tmpPos)) {
                    layoutManager.findViewByPosition(tmpPos).setBackgroundColor(colorId);

                }
            }
        }
        for (i = 0; i < 9; i++) {
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

        for (i = minX; i < minX + 3; i++) {
            for (j = minY; j < minY + 3; j++) {
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


        for (i = 0; i < 9; i++) {
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
        for (i = 0; i < 9; i++) {
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

        for (i = minX; i < minX + 3; i++) {
            for (j = minY; j < minY + 3; j++) {
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
            for (i = 0; i < 9; i++) {
                for (j = 0; j < 9; j++) {
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

    @SuppressLint("ClickableViewAccessibility")
    public void setNotes(boolean bl) {
        isNoting = bl;
        if (clicked == null) {
            return;
        }
        GameHolder tmp = clicked;
        // 如果已经有点击的子项，
        int position = clicked.getAdapterPosition();
        int x = position / 9;
        int y = position % 9;
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

    static class GameHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private RecyclerView notes;

        public GameHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.cell_text);
            notes = itemView.findViewById(R.id.notes_list);
        }

        public void setValue(int value) {
            textView.setText(value > 0 ? String.valueOf(value) : "");
        }
    }
}
