package com.example.androidscript.UserInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {

    public static class BlockViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = cardView;

            //if instanceof A ...
        }
    }

    private ArrayList<Integer> BlockContent;

    public BlockAdapter(ArrayList<Integer> content) {
        this.BlockContent = content;
    }

    @Override
    public @NotNull BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.script_battlestage, parent, false);
        return new BlockViewHolder(view.findViewById(R.id.battlestage));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BlockAdapter.BlockViewHolder holder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return BlockContent.size();
    }
}