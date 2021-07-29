package com.example.androidscript.UserInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.FGO.FGOViewHolder;
import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class BlockAdapter extends RecyclerView.Adapter<FGOViewHolder> {

    public final ArrayList<Vector<String>> Data;

    private updateOrder onOrderChange;

    public interface updateOrder {
        void swap(int a, int b);

        void delete(int a);
    }

    private final ArrayList<String> Blocks;

    public BlockAdapter(ArrayList<String> content) {
        Blocks = content;
        Data = new ArrayList<>(content.size());
    }

    @Override
    public @NotNull FGOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.onOrderChange = new updateOrder() {
            @Override
            public void swap(int a, int b) {
                if (a > 0 && b < Blocks.size() && a < b) {
                    Collections.swap(Blocks, a, b);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void delete(int a) {
                if (a > 0 && a < Blocks.size()) {
                    Blocks.remove(a);
                    notifyDataSetChanged();
                }
            }
        };

        View view;
        int id;

        switch (viewType) {
            case 0://Skill
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_skills, parent, false);
                id = R.id.skills;
                return new FGOViewHolder.SkillVH(view.findViewById(id));

            case 1://NoblePhantasms
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_noble_phantasms, parent, false);
                id = R.id.noble_phantasms;
                return new FGOViewHolder.NoblePhantasmsVH(view.findViewById(id));

            case 2://CraftSkill
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_craft_skills, parent, false);
                id = R.id.craft_skills;
                return new FGOViewHolder.CraftSkillVH(view.findViewById(id));
            case 3://PreStage
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_prestage, parent, false);
                id = R.id.pre_stage;
                return new FGOViewHolder.PreStageVH(view.findViewById(id));
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public void onBindViewHolder(@NotNull FGOViewHolder holder, int position) {//Do nothing
        holder.onBind(onOrderChange, position);
        Data.set(position, holder.retrieveData());
    }

    @Override
    public int getItemViewType(int position) {
        switch (Blocks.get(position)) {
            case "Skill":
                return 0;
            case "NoblePhantasms":
                return 1;
            case "CraftSkill":
                return 2;
            case "PreStage":
                return 3;
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public int getItemCount() {
        return Blocks.size();
    }
}