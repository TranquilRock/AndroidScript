package com.example.androidscript.Menu.Basic;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.Basic.BasicBlockAdapter.updateOrder;
import com.example.androidscript.R;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public abstract class BasicViewHolder extends RecyclerView.ViewHolder {

    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;
    public TextView Title;

    public BasicViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(updateOrder order, int position) {
        Up = view.findViewById(R.id.btn_up);
        Up.setOnClickListener(v -> {
            order.swap(position - 1, position);
        });
        Down = view.findViewById(R.id.btn_down);
        Down.setOnClickListener(v -> {
            order.swap(position, position + 1);
        });
        Close = view.findViewById(R.id.btn_del);
        Close.setOnClickListener(v -> {
            order.delete(position);
        });
    }

    public static class TwoVarVH extends BasicViewHolder{
        TextView SubTitle;
        TextView Title1;
        TextView TitleMiddle;
        EditText LeftInput;
        EditText RightInput;

        public TwoVarVH(View view) {
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position){
            super.onBind(order, position);
            Title = view.findViewById(R.id.Title);
            SubTitle = view.findViewById(R.id.SubTitle);
            Title1 = view.findViewById(R.id.Title1);
            TitleMiddle = view.findViewById(R.id.TitleMiddle);
            LeftInput = view.findViewById(R.id.LeftInput);
            RightInput = view.findViewById(R.id.RightInput);
        }
    }

    public static class OneVarVH extends BasicViewHolder{

        EditText Input;

        public OneVarVH (View view){
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position){
            super.onBind(order, position);
            Title = view.findViewById(R.id.OneVarTitle);
            Input = view.findViewById(R.id.OneVarInput);
        }
    }

    public static class CompareVH extends BasicViewHolder{

        EditText LeftPositionX;
        EditText LeftPositionY;
        EditText RightPositionX;
        EditText RightPositionY;

        public CompareVH (View view){
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position){
            super.onBind(order, position);
            LeftPositionX = view.findViewById(R.id.LeftPositionX);
            LeftPositionY = view.findViewById(R.id.LeftPositionY);
            RightPositionX = view.findViewById(R.id.RightPositionX);
            RightPositionY = view.findViewById(R.id.RightPositionY);
        }
    }

}
