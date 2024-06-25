package com.aminbadh.tdrcontentcreatorlpm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aminbadh.tdrcontentcreatorlpm.R;
import com.aminbadh.tdrcontentcreatorlpm.interfaces.OnMainListener;

import java.util.ArrayList;

public class SelectionRecyclerAdapter extends RecyclerView
        .Adapter<SelectionRecyclerAdapter.SelectionHolder> {

    private final ArrayList<String> items;
    private final OnMainListener onMainListener;

    public SelectionRecyclerAdapter(ArrayList<String> items,
                                    OnMainListener onMainListener) {
        this.items = items;
        this.onMainListener = onMainListener;
    }

    @NonNull
    @Override
    public SelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_selection, parent, false);
        return new SelectionHolder(view, onMainListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionHolder holder, int position) {
        holder.textViewItemName.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SelectionHolder extends RecyclerView.ViewHolder {
        TextView textViewItemName;

        public SelectionHolder(@NonNull View itemView, OnMainListener onMainListener) {
            super(itemView);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            itemView.setOnClickListener(view -> onMainListener.onMainListener(getAdapterPosition()));
        }
    }
}
