package com.example.wzq.sudoku.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wzq.sudoku.R;
import com.example.wzq.sudoku.view.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wzq20
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Callback {

    private List<Integer> noteList = new ArrayList<>(9);

   private List<NoteViewHolder> holders = new ArrayList<>(9);



    public NoteAdapter() {
        for (int i = 0; i < 9; i++) {
            noteList.add(0);
        }
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        NoteViewHolder holder = new NoteViewHolder(view);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Integer value = noteList.get(position);
        holder.setValue(value);
    }

    @Override
    public int getItemCount() {
        return 9;
    }

    @Override
    public void onClick(int number) {
        if (number == 0) {
            for (Integer v : noteList) {
                v = 0;
            }
        } else {
            noteList.set(number - 1, number);
            holders.get(number-1).setValue(number);
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.note_text);
        }

        public void setValue(int value) {
            textView.setText(value > 0 ? String.valueOf(value) : "");
        }
    }
    public void setNull() {
        for(int i=0;i<9;i++) {
            noteList.set(i,0);
            holders.get(i).setValue(0);
        }
    }
}
