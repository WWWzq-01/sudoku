package com.example.wzq.sudoku;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.Adapter.GameAdapter;
import com.example.wzq.sudoku.Adapter.NumberAdapter;
import com.example.wzq.sudoku.utils.Generator;
import com.example.wzq.sudoku.view.Callback;
import com.example.wzq.sudoku.view.MapDivider;
import com.example.wzq.sudoku.view.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements Callback {

    private static final int UPDATE_TEXT = 1;
    private GameAdapter gameAdapter;
    private Timer timer;
    private TextView textView;
    private ImageView noteImage;
    private long startTime;
    private int count;
    private boolean isNoting = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    textView.setText((String) msg.obj);            //修改UI组件
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.GRAY);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        count = intent.getIntExtra("num", 50);
        init();
    }

    private void init() {
        RecyclerView gameView = findViewById(R.id.game);
        RecyclerView numberView = findViewById(R.id.keyboard);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 9) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        gameView.setLayoutManager(gridLayoutManager);
        numberView.setLayoutManager(new GridLayoutManager(this, 5));
        List<Point> data = new ArrayList<>();

        int[][] map;
        map = Generator.generate(count);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Point point = new Point(i, j, map[i][j]);
                data.add(point);
            }
        }
        gameAdapter = new GameAdapter(this, data,gridLayoutManager);
        gameAdapter.setMap(map);
        gameView.setAdapter(gameAdapter);

        MapDivider mapDivider = new MapDivider(this);
        // 设置分割线颜色为灰色
        mapDivider.setDrawable(getResources().getDrawable(R.drawable.gray_divider));
        gameView.addItemDecoration(mapDivider);
        NumberAdapter numberAdapter = new NumberAdapter();
        numberView.setAdapter(numberAdapter);
        numberAdapter.setCallback(this);


        textView = findViewById(R.id.time);
        startTime = System.currentTimeMillis();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long curTime = System.currentTimeMillis();
                long delta = curTime - startTime;
                Message message = new Message();
                message.what = UPDATE_TEXT;
                message.obj = timeToString(delta / 1000);
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, 100L);

        noteImage = findViewById(R.id.note_image);
        noteImage.setColorFilter(Color.BLACK);
        noteImage.setOnClickListener(v -> {
            if (isNoting) {
                isNoting = false;
                noteImage.setColorFilter(Color.BLACK);
            } else {
                isNoting = true;
                noteImage.setColorFilter(Color.BLUE);
            }
            gameAdapter.setNotes(isNoting);
        });
    }

    @Override
    public void onClick(int number) {
        if (isNoting) {
            gameAdapter.note(number);
        } else {
            gameAdapter.click(number);
        }
        if (gameAdapter.isFull()) {
            showWin();
        }
    }

    private void showWin() {
        timer.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setTitle("Congratulations!")
                .setMessage("You Win!\n" + "You time is "
                        + timeToString((System.currentTimeMillis() - startTime) / 1000))
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .create();
        alertDialog.show();
    }

    private String timeToString(long time) {
        long hour = time / 3600;
        long minute = (time % 3600) / 60;
        long second = time % 60;
        String sh = hour < 10 ? "0" + hour : String.valueOf(hour);
        String sm = minute < 10 ? "0" + minute : String.valueOf(minute);
        String ss = second < 10 ? "0" + second : String.valueOf(second);
        return sh + ":" + sm + ":" + ss;
    }
}
