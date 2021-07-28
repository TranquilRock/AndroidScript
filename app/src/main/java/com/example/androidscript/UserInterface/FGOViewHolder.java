package com.example.androidscript.UserInterface;

import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {
    protected View view;
    public FGOViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public abstract void onBind(Vector<String> data);

    public static class SkillVH extends FGOViewHolder {
        public SkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(Vector<String> data) {

        }
    }

    public static class CraftSkillVH extends FGOViewHolder {
        public CraftSkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(Vector<String> data) {

        }
    }

    public static class EndStageVH extends FGOViewHolder {
        public EndStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(Vector<String> data) {

        }
    }

    public static class PreStageVH extends FGOViewHolder {
        Spinner Stamina;
        Spinner Friend;
        Spinner Craft;

        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(Vector<String> data) {
            Stamina = view.findViewById(R.id.stamina);
            Friend = view.findViewById(R.id.friend);
            Craft = view.findViewById(R.id.craft);
        }
    }

    public static class NoblePhantasmsVH extends FGOViewHolder {
        public NoblePhantasmsVH(View v) {
            super(v);
        }

        @Override
        public void onBind(Vector<String> data) {

        }
    }
}
