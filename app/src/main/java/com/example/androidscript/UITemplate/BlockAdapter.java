package com.example.androidscript.UITemplate;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public abstract class BlockAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public Vector<Vector<String>> Data;

    public updater onOrderChange;

    public interface updater {
        void swap(int a, int b);
        void delete(int a);
        void insert();
        void self(int index);
    }
}
