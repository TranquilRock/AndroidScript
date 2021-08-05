package com.example.androidscript.Menu.Basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

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
                if (a >= 0 && b < BasicData.size() && a < b) {
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
                        .inflate(R.layout.basic_two_var_format, parent, false);
                return new BasicViewHolder.ThreeVH(view);
            case 1://OneVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_one_var_format, parent, false);
                return new BasicViewHolder.OneVH(view);
            case 2://Compare
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_four_var_format, parent, false);
                return new BasicViewHolder.FourVH(view);
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BasicViewHolder holder, int position) {
        holder.onBind(onOrderChange, position);
        ((BasicViewHolder) holder).Title.setText(BasicData.get(position).get(0));
        switch (BasicData.get(position).get(0)) {
            case "Exit":
                ((BasicViewHolder.OneVH) holder).Input.setVisibility(View.INVISIBLE);
                break;
            case "Contain":
            case "JumpTo":
            case "Wait":
            case "Call":
            case "Tag":
            case "Return":
                break;
            case "Click":
                ((BasicViewHolder.ThreeVH) holder).SubTitle.setText("Position");
                ((BasicViewHolder.ThreeVH) holder).Title1.setText("X:");
                ((BasicViewHolder.ThreeVH) holder).TitleMiddle.setText("Y:");
                break;
            case "CallArg":
            case "IfGreater":
                ((BasicViewHolder.ThreeVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).TitleMiddle.setText(">");
                break;
            case "IfSmaller":
                ((BasicViewHolder.ThreeVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).TitleMiddle.setText("<");//> or <
                break;
            case "Add":
            case "Subtract":
            case "Var":
                ((BasicViewHolder.ThreeVH) holder).SubTitle.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).Title1.setVisibility(View.INVISIBLE);
                ((BasicViewHolder.ThreeVH) holder).TitleMiddle.setText("=");
                break;
            case "Swipe":
            case "Compare":
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (BasicData.get(position).get(0)) {
            case "Exit":
                return 0;
            case "Contain":
            case "JumpTo":
            case "Wait":
            case "Call":
            case "Tag":
            case "Return":
                return 1;
            case "Click":
            case "CallArg":
            case "IfGreater":
            case "IfSmaller":
            case "Add":
            case "Subtract":
            case "Var":
                return 2;
            case "Swipe":
                return 4;
            case "Compare":
                return 5;
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public int getItemCount() {
        return BasicData.size();
    }
}
