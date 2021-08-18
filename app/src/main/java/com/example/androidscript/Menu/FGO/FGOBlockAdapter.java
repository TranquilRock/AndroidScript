package com.example.androidscript.Menu.FGO;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.SpnMaker;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Vector;


public class FGOBlockAdapter extends RecyclerView.Adapter<FGOViewHolder> {

    public Vector<Vector<String>> Data;

    public updateOrder onOrderChange;

    public interface updateOrder {
        void swap(int a, int b);

        void delete(int a);

        void insert();

        void self(int index);
    }

    public FGOBlockAdapter(Vector<Vector<String>> _Data) {
        this.Data = _Data;
        this.onOrderChange = new updateOrder() {
            @Override
            public void swap(int a, int b) {
                if (a > 0 && b < Data.size() - 1 && a < b) {
                    Collections.swap(Data, a, b);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void delete(int a) {
                if (a > 0 && a < Data.size() - 1) {
                    Data.remove(a);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void insert() {
                notifyDataSetChanged();
            }
            @Override
            public void self(int index){
                try{
                    new Handler(Looper.getMainLooper()).post(() -> notifyItemChanged(index));
                }catch (Throwable e){
                    DebugMessage.set("PreStageGG");
                }
            }
        };
    }

    @Override
    public @NotNull FGOViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0://Skill
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_skills, parent, false);
                return new FGOViewHolder.SkillVH(view.findViewById(R.id.skills));

            case 1://NoblePhantasms
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_noble_phantasms, parent, false);
                return new FGOViewHolder.NoblePhantasmsVH(view.findViewById(R.id.noble_phantasms));

            case 2://CraftSkill
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_craft_skills, parent, false);
                return new FGOViewHolder.CraftSkillVH(view.findViewById(R.id.craft_skills));
            case 3://PreStage
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_prestage, parent, false);
                Vector<String> tmp = FileOperation.browseWithoutSuffix(FGOEditor.FolderName + "Friend",".png");
                tmp.add(0,"None");
                SpnMaker.fromStringWithView(R.id.friend,view, tmp);
                tmp = FileOperation.browseWithoutSuffix(FGOEditor.FolderName + "Craft",".png");
                tmp.add(0,"None");
                SpnMaker.fromStringWithView(R.id.craft,view, tmp);
                return new FGOViewHolder.PreStageVH(view.findViewById(R.id.pre_stage));
            case 4:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_endstage, parent, false);
                return new FGOViewHolder.EndVH(view.findViewById(R.id.end_stage));
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public void onBindViewHolder(@NotNull FGOViewHolder holder, int position) {//Do nothing
        holder.onBind(onOrderChange, position,Data);
    }

    @Override
    public int getItemViewType(int position) {
        switch (Data.get(position).get(0)) {
            case "Skill":
                return 0;
            case "NoblePhantasms":
                return 1;
            case "CraftSkill":
                return 2;
            case "PreStage":
                return 3;
            case "End":
                return 4;
        }
        throw new RuntimeException("Invalid Type " + Data.get(position).get(0));
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }
}