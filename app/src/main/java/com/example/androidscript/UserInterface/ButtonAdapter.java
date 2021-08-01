package com.example.androidscript.UserInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        public Button button;
        public ButtonViewHolder(@NonNull View itemView,Button button) {
            super(itemView);
            this.button = button;
        }
    }
    protected Vector<String> ButtonText;

    public ButtonAdapter(Vector<String> _ButtonText){
        this.ButtonText = _ButtonText;
    }

    @Override
    public @NotNull ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.button_item, parent, false);

        return new ButtonViewHolder(view,view.findViewById(R.id.button_item));
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        holder.button.setText(ButtonText.get(position));
    }

    @Override
    public int getItemCount() {
        return ButtonText.size();
    }
}