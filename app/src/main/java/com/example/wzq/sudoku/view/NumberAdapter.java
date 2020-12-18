package com.example.wzq.sudoku.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.wzq.sudoku.R;

import static android.content.ContentValues.TAG;

public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberHolder> {

    private int clickNum = -1;
    private Callback callback;

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public NumberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.number_item, parent, false);
        NumberHolder holder = new NumberHolder(view);
        int defaultColor = holder.cardView.getCardBackgroundColor().getDefaultColor();
        int clickColor = view.getResources().getColor(R.color.clicked);
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (callback != null && clickNum != -1) {
                        callback.onClick(clickNum);
                    }
                    clickNum = -1;
                    holder.cardView.setCardBackgroundColor(defaultColor);
                    break;
                case MotionEvent.ACTION_DOWN:
                    clickNum = holder.value;
                    holder.cardView.setCardBackgroundColor(clickColor);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    clickNum = -1;
                    holder.cardView.setCardBackgroundColor(defaultColor);
                    break;
                default:
            }
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NumberHolder holder, int position) {
        holder.setValue(position + 1);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    class NumberHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CardView cardView;
        private int value;

        public NumberHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.number_text);
            cardView = itemView.findViewById(R.id.number_card);
        }

        public void setValue(int value) {
            if(value == 10){
                this.value = 0;
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                    textView.setBackgroundResource(R.drawable.error);
                }
                return;
            }
            this.value = value;
            textView.setText(String.valueOf(value));
        }
    }
}
