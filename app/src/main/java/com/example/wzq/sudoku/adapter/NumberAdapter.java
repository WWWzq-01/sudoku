package com.example.wzq.sudoku.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.R;
import com.example.wzq.sudoku.utils.Callback;

/**
 * @author wzq20
 */
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
        int clickColor = view.getResources().getColor(R.color.clicked,null);
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

    static class NumberHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CardView cardView;
        private int value;

        private NumberHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.number_text);
            cardView = itemView.findViewById(R.id.number_card);
        }

        private void setValue(int value) {
            if (value == 10) {
                this.value = 0;
                textView.setBackgroundResource(R.drawable.error);
                return;
            }
            this.value = value;
            textView.setText(String.valueOf(value));
        }
    }
}
