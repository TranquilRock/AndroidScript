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

public abstract class BasicViewHolder extends RecyclerView.ViewHolder {

    protected View view;
    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
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
        Title = view.findViewById(R.id.Title);
    }

    public static class ZeroVH extends BasicViewHolder {
        public EditText Input;

        public OneVH(View view) {
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            Title = view.findViewById(R.id.Title);
            Input = view.findViewById(R.id.OneVarInput);
        }
    }


    public static class OneVH extends BasicViewHolder {

        public EditText Input;

        public OneVH(View view) {
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            Title = view.findViewById(R.id.Title);
            Input = view.findViewById(R.id.OneVarInput);
        }
    }

    public static class TwoVH extends BasicViewHolder {
        public TextView TitleMiddle;
        public EditText LeftInput;
        public EditText RightInput;

        public TwoVH(View view) {
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            TitleMiddle = view.findViewById(R.id.TitleMiddle);
            LeftInput = view.findViewById(R.id.LeftInput);
            RightInput = view.findViewById(R.id.RightInput);
        }
    }

    public static class ThreeVH extends BasicViewHolder {
        public TextView TitleMiddle;
        public EditText LeftInput;
        public EditText RightInput;

        public ThreeVH(View view) {
            super(view);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            TitleMiddle = view.findViewById(R.id.TitleMiddle);
            LeftInput = view.findViewById(R.id.LeftInput);
            RightInput = view.findViewById(R.id.RightInput);
        }
    }

    public static class FourVH extends BasicViewHolder {

        public EditText LeftPositionX;
        public EditText LeftPositionY;
        public EditText RightPositionX;
        public EditText RightPositionY;

        public FourVH(View view){
            super(view);
        }
        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            LeftPositionX = view.findViewById(R.id.LeftPositionX);
            LeftPositionY = view.findViewById(R.id.LeftPositionY);
            RightPositionX = view.findViewById(R.id.RightPositionX);
            RightPositionY = view.findViewById(R.id.RightPositionY);
        }
    }
    public static class FiveVH extends BasicViewHolder {

        public EditText LeftPositionX;
        public EditText LeftPositionY;
        public EditText RightPositionX;
        public EditText RightPositionY;

        public FiveVH(View view){
            super(view);
        }
        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            LeftPositionX = view.findViewById(R.id.LeftPositionX);
            LeftPositionY = view.findViewById(R.id.LeftPositionY);
            RightPositionX = view.findViewById(R.id.RightPositionX);
            RightPositionY = view.findViewById(R.id.RightPositionY);
        }
    }
}
