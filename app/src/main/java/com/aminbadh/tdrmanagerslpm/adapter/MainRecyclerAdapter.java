package com.aminbadh.tdrmanagerslpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdrmanagerslpm.R;

import java.util.ArrayList;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainHolder> {

    private final ArrayList<String> items;
    private final OnMain onMain;

    public MainRecyclerAdapter(ArrayList<String> items, OnMain onMain) {
        this.items = items;
        this.onMain = onMain;
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_main, parent, false);
        return new MainHolder(view, onMain);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        holder.textViewTitle.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MainHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;

        public MainHolder(@NonNull View itemView, OnMain onMain) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            itemView.setOnClickListener(view -> onMain.onClick(getAdapterPosition()));
        }
    }

    public interface OnMain {
        void onClick(int position);
    }
}
