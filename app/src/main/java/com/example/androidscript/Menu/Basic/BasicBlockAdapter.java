package com.example.androidscript.Menu.Basic;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class BasicBlockAdapter extends RecyclerView.Adapter<BasicViewHolder> {

    public final Vector<Vector<String>> BasicData;

    public BasicBlockAdapter.updateOrder onOrderChange;

    public interface updateOrder {
        void swap(int a, int b);

        void delete(int a);

        void insert();
    }

    public BasicBlockAdapter(Vector<Vector<String>> content) {
        BasicData = content;
        this.onOrderChange = new updateOrder() {
            @Override
            public void swap(int a, int b) {
                if (a >= 0 && b < BasicData.size()&& a < b) {
                    Collections.swap(BasicData, a, b);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void delete(int a) {
                if (a >= 0 && a <= BasicData.size() - 1) {
                    BasicData.remove(a);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void insert() {
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public @NotNull BasicViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0://TwoVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_two_var_format, parent, false);
                return new BasicViewHolder.TwoVarVH(view);
            case 1://OneVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_one_var_format, parent, false);
                return new BasicViewHolder.OneVarVH(view);
            case 2://Compare
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.script_compare, parent, false);
                return new BasicViewHolder.CompareVH(view);
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BasicViewHolder holder, int position) {
        holder.onBind(onOrderChange, position);
        switch (BasicData.get(position).get(0)) {
            //TwoVarFormat
            case "Click":
                ((BasicViewHolder.TwoVarVH) holder).Title.setText("Click");
                ((BasicViewHolder.TwoVarVH) holder).SubTitle.setText("Position");
                ((BasicViewHolder.TwoVarVH) holder).Title1.setText("X:");
                ((BasicViewHolder.TwoVarVH) holder).TitleMiddle.setText("Y:");
                break;
            case "IfGreater":
                ((BasicViewHolder.TwoVarVH) holder).Title.setText("If");
                ((BasicViewHolder.TwoVarVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).TitleMiddle.setText(">");
                break;
            case "IfSmaller":
                ((BasicViewHolder.TwoVarVH) holder).Title.setText("If");
                ((BasicViewHolder.TwoVarVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).TitleMiddle.setText("<");
                break;
            case "Var":
                ((BasicViewHolder.TwoVarVH) holder).Title.setText("Var");
                ((BasicViewHolder.TwoVarVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.TwoVarVH) holder).TitleMiddle.setText("=");
                break;
            //OneVarFormat
            case "JumpToLine":
                ((BasicViewHolder.OneVarVH) holder).OneVarTitle.setText("JumpToLine");
                break;
            case "Wait":
                ((BasicViewHolder.OneVarVH) holder).OneVarTitle.setText("Wait");
                break;
            case "Call":
                ((BasicViewHolder.OneVarVH) holder).OneVarTitle.setText("Call");
                break;
            case "Return":
                ((BasicViewHolder.OneVarVH) holder).OneVarTitle.setText("Return");
                break;
            case "Exit":
                ((BasicViewHolder.OneVarVH) holder).OneVarTitle.setText("Exit");
                ((BasicViewHolder.OneVarVH) holder).OneVarInput.setVisibility(View.INVISIBLE);
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (BasicData.get(position).get(0)) {
            case "Exit":
                return 0;
            case "Contain":
            case "IfGreater":
            case "IfSmaller":
            case "Var":
                return 0;
            case "JumpToLine":
            case "Wait":
            case "Call":
            case "Return":
                return 1;
            case "Compare":
                return 2;
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public int getItemCount() {
        return BasicData.size();
    }
}
