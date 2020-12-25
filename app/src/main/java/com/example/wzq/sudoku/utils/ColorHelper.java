package com.example.wzq.sudoku.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.wzq.sudoku.R;

/**
 * @author wzq20
 */
public class ColorHelper {

    public final int NO_CLICK;

    public final int CLICKED;

    public final int CLICK_AROUND;

    public final int CLICK_SAME;

    public final int ERROR_SAME;

    public final int ERROR_NUM;

    public final int INPUT_NUM;

    public final int HINT_NUM;

    public final int NO_CLICK_NUMBER;


    public ColorHelper(Context context) {
        this.NO_CLICK = ContextCompat.getColor(context, R.color.noClick);
        this.CLICKED = ContextCompat.getColor(context, R.color.clicked);
        this.CLICK_AROUND = ContextCompat.getColor(context, R.color.clickAround);
        this.CLICK_SAME = ContextCompat.getColor(context, R.color.clickSame);
        this.ERROR_SAME = ContextCompat.getColor(context, R.color.errorSame);
        this.ERROR_NUM = ContextCompat.getColor(context, R.color.errorNum);
        this.INPUT_NUM = ContextCompat.getColor(context, R.color.inputNum);
        this.NO_CLICK_NUMBER = ContextCompat.getColor(context, R.color.noClickNumber);
        this.HINT_NUM = ContextCompat.getColor(context,R.color.hintNum);
    }
}
