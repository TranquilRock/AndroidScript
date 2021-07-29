package com.example.androidscript.Menu.FGO;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.UserInterface.BlockAdapter;
import com.example.androidscript.util.DebugMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {

    public abstract Vector<String> retrieveData();

    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;

    public FGOViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(BlockAdapter.updateOrder order, int position) {
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
        EditText Repeat;

        @Override
        public Vector<String> retrieveData() {
            Vector<String> ret = new Vector<>();
            ret.add((String) Stamina.getSelectedItem());
            ret.add((String) Friend.getSelectedItem());
            ret.add((String) Craft.getSelectedItem());
            ret.add(Repeat.getText().toString());
            return ret;
        }

        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            Stamina = view.findViewById(R.id.stamina);
            Friend = view.findViewById(R.id.friend);
            Craft = view.findViewById(R.id.craft);
            Repeat = view.findViewById(R.id.n_repeat);

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
        @Override
        public Vector<String> retrieveData() {
            Vector<String> ret = new Vector<>();
            return ret;
        }

        public SkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order, position);
        }
    }

    public static class CraftSkillVH extends FGOViewHolder {
        @Override
        public Vector<String> retrieveData() {
            Vector<String> ret = new Vector<>();
            return ret;
        }

        public CraftSkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order, position);
        }
    }


    public static class NoblePhantasmsVH extends FGOViewHolder {
        @Override
        public Vector<String> retrieveData() {
            Vector<String> ret = new Vector<>();
            return ret;
        }

        public NoblePhantasmsVH(View v) {
            super(v);
        }

        @Override
        public void onBind(BlockAdapter.updateOrder order, int position) {
            super.onBind(order, position);
        }
    }
}
