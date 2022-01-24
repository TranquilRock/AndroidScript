package com.example.androidscript.Activities.Basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.androidscript.R;
import com.example.androidscript.UITemplate.BlockAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Vector;

public class BasicBlockAdapter extends BlockAdapter<BasicViewHolder> {

    public BasicBlockAdapter(Vector<Vector<String>> content) {
        Data = content;
        this.onOrderChange = new updater() {
            @Override
            public void swap(int a, int b) {
                if (a >= 0 && b < Data.size() && a < b) {
                    Collections.swap(Data, a, b);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void delete(int a) {
                if (a >= 0 && a <= Data.size() - 1) {
                    Data.remove(a);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void insert() {
                notifyDataSetChanged();
            }

            @Override
            public void self(int index) {

            }
        };
    }

    @Override
    public @NotNull BasicViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0://ZeroVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_zero_var_format, parent, false);
                return new BasicViewHolder.ZeroVH(view);
            case 1://OneVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_one_var_format, parent, false);
                return new BasicViewHolder.OneVH(view);
            case 2://TwoVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_two_var_format, parent, false);
                return new BasicViewHolder.TwoVH(view);
            case 3://TwoVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_three_var_format, parent, false);
                return new BasicViewHolder.ThreeVH(view);
            case 4://FourVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_four_var_format, parent, false);
                return new BasicViewHolder.FourVH(view);
            case 5://FiveVarFormat
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.basic_five_var_format, parent, false);
                return new BasicViewHolder.FiveVH(view);
        }
        throw new RuntimeException("Invalid Type");
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BasicViewHolder holder, int position) {
        holder.onBind(onOrderChange, position, this.Data);
        holder.Title.setText(Data.get(position).get(0));
        switch (Data.get(position).get(0)) {
            case "Exit"://ZeroVH
                break;
            case "Log":
                holder.Inputs[0].setHint("LogString");
                break;
            case "JumpTo":
                holder.Inputs[0].setHint("Line");
                break;
            case "Wait":
                holder.Inputs[0].setHint("ms");
                break;
            case "Call":
                holder.Inputs[0].setHint(".txt");
                break;
            case "Tag":
                holder.Inputs[0].setHint("$Var");
                break;
            case "Return":
                holder.Inputs[0].setHint("Value");
                break;
            case "ClickPic"://TwoVH
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("");
                holder.Inputs[0].setHint("Image");
                holder.Inputs[1].setHint("Ratio");
                break;
            case "Click":
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("");
                holder.Inputs[0].setHint("X");
                holder.Inputs[1].setHint("Y");
                break;
            case "CallArg":
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("");
                holder.Inputs[0].setHint(".txt");
                holder.Inputs[1].setHint("Value");
                break;
            case "IfGreater":
                holder.Title.setText("If");
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText(">");
                holder.Inputs[0].setHint("$Var");
                holder.Inputs[1].setHint("Value");
                break;
            case "IfSmaller":
                holder.Title.setText("If");
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("<");
                holder.Inputs[0].setHint("$Var");
                holder.Inputs[1].setHint("Value");
                break;
            case "Add":
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("+=");
                holder.Inputs[0].setHint("$Var");
                holder.Inputs[1].setHint("Value");
                break;
            case "Subtract":
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("-=");
                holder.Inputs[0].setHint("$Var");
                holder.Inputs[1].setHint("Value");
                break;
            case "Var":
                ((BasicViewHolder.TwoVH) holder).TitleMiddle.setText("=");
                holder.Inputs[0].setHint("$Name");
                holder.Inputs[1].setHint("Value");
                break;
            case "Check"://Three
                holder.Inputs[0].setHint("x");
                holder.Inputs[1].setHint("y");
                holder.Inputs[2].setHint("color");
                break;
            case "Swipe"://FourVH
                holder.Inputs[0].setHint("FromX");
                holder.Inputs[1].setHint("FromY");
                holder.Inputs[2].setHint("ToX");
                holder.Inputs[3].setHint("ToY");
                break;
            case "Compare"://FiveVH
                holder.Inputs[0].setHint("X1");
                holder.Inputs[1].setHint("Y1");
                holder.Inputs[2].setHint("X2");
                holder.Inputs[3].setHint("Y2");
                holder.Inputs[4].setHint("Image");
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (Data.get(position).get(0)) {
            case "Exit":
                return 0;
            case "Log":
            case "JumpTo":
            case "Wait":
            case "Call":
            case "Tag":
            case "Return":
                return 1;
            case "ClickPic":
            case "Click":
            case "CallArg":
            case "IfGreater":
            case "IfSmaller":
            case "Add":
            case "Subtract":
            case "Var":
                return 2;
            case "Check":
                return 3;
            case "Swipe":
                return 4;
            case "Compare":
                return 5;
        }
        throw new RuntimeException("Invalid " + Data.get(position).get(0));
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }
}
