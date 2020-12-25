package com.example.wzq.sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author wzq20
 */
public class LoginActivity extends AppCompatActivity {

    private int minCount;
    private int maxCount;
    private boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.GRAY);
        setContentView(R.layout.activity_main);

        Button btnEasy = findViewById(R.id.button_easy);
        Button btnNormal = findViewById(R.id.button_normal);
        Button btnHard = findViewById(R.id.button_hard);

        btnEasy.setOnClickListener(v -> {
            minCount = 20;
            maxCount = 30;
            start();
        });
        btnNormal.setOnClickListener(v -> {
            minCount = 30;
            maxCount = 40;
            start();
        });
        btnHard.setOnClickListener(v -> {
            minCount = 40;
            maxCount = 50;
            start();
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        clickable = true;
    }

    private void start() {
        if (!clickable) {
            return;
        }
        clickable = false;
        int delta = maxCount - minCount;
        int num = (int) (Math.random() * delta) + minCount;
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("num", num);
        // start game
        startActivity(intent);
    }
}
