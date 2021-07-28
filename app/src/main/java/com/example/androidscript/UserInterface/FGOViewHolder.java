package com.example.androidscript.UserInterface;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {
    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;

    public FGOViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(BlockAdapter.updateOrder order, int position){
        Up = view.findViewById(R.id.btn_up);
        Up.setOnClickListener(v -> {
            order.swap(position - 1, position);
        });
        Down = view.findViewById(R.id.btn_down);
        Down.setOnClickListener(v -> {
            order.swap(position, position + 1);
        });
        Close = view.findViewById(R.id.btn_close);
        Close.setOnClickListener(v -> {
            order.delete(position);
        });
    }

    //============================================================
    public static class PreStageVH extends FGOViewHolder {
        Spinner Stamina;
        Spinner Friend;
        Spinner Craft;

        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            Stamina = view.findViewById(R.id.stamina);
            Friend = view.findViewById(R.id.friend);
            Craft = view.findViewById(R.id.craft);
            Stamina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DebugMessage.set((String) Stamina.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }


    public static class SkillVH extends FGOViewHolder {
        public SkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order,position);
        }
    }

    public static class CraftSkillVH extends FGOViewHolder {
        public CraftSkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order,position);

        }
    }


    public static class NoblePhantasmsVH extends FGOViewHolder {
        public NoblePhantasmsVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order,position);


        }
    }
}
