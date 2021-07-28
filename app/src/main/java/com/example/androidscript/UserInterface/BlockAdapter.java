package com.example.androidscript.UserInterface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class BlockAdapter extends RecyclerView.Adapter<FGOViewHolder> {


    private ArrayList<Vector<String>> BlockType;

    public BlockAdapter(ArrayList<Vector<String>> content) {
        BlockType = content;
    }

    @Override
    public @NotNull FGOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        holder.onBind(BlockType.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        switch (BlockType.get(position).get(0)) {
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
        return BlockType.size();
    }
}