package com.example.androidscript.Menu.Basic;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.Basic.BasicBlockAdapter.updateOrder;
import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Vector;

public abstract class BasicViewHolder extends RecyclerView.ViewHolder {

    protected View view;
    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    public TextView Title;
    public EditText[] Inputs;

    protected static TextWatcher getTextWatcher(Vector<Vector<String>> Data, int position, int index, BasicViewHolder abc) {
        return (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (abc.getAdapterPosition() == position) {
                    Data.get(position).setElementAt(s.toString(), index);
                }
            }
        });
    }

    public BasicViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
        Up = view.findViewById(R.id.btn_up);
        Up.setOnClickListener(v -> {
            order.swap(position - 1, position);

        });
        Down = view.findViewById(R.id.btn_down);
        Down.setOnClickListener(v -> order.swap(position, position + 1));
        Close = view.findViewById(R.id.btn_del);
        Close.setOnClickListener(v -> order.delete(position));
        Title = view.findViewById(R.id.Title);
    }

    public static class ZeroVH extends BasicViewHolder {
        public ZeroVH(View view) {
            super(view);
        }
    }

    public static class OneVH extends BasicViewHolder {
        public OneVH(View view) {
            super(view);
            Inputs = new EditText[1];
        }

        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Inputs[0] = view.findViewById(R.id.OneVarInput);
            Inputs[0].setText(Data.get(position).get(1));
            Inputs[0].addTextChangedListener(getTextWatcher(Data, position, 1, this));
        }
    }

    public static class TwoVH extends BasicViewHolder {
        public TextView TitleMiddle;

        public TwoVH(View view) {
            super(view);
            Inputs = new EditText[2];
        }

        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            TitleMiddle = view.findViewById(R.id.TwoVarTextMiddle);
            Inputs[0] = view.findViewById(R.id.TwoVarLeftInput);
            Inputs[1] = view.findViewById(R.id.TwoVarRightInput);
            for (int z = 0; z < 2; z++) {
                Inputs[z].setText(Data.get(position).get(z + 1));
                Inputs[z].addTextChangedListener(getTextWatcher(Data, position, z + 1, this));
            }
        }
    }

//    public static class ThreeVH extends BasicViewHolder {
//        public ThreeVH(View view) {
//            super(view);
//            Inputs = new EditText[3];
//        }
//
//        @Override
//        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
//            super.onBind(order, position, Data);
//            Inputs[0] = view.findViewById(R.id.ThreeVarInputLeft);
//            Inputs[1] = view.findViewById(R.id.ThreeVarInputMiddle);
//            Inputs[2] = view.findViewById(R.id.ThreeVarInputRight);
//            for (int z = 0; z < 3; z++) {
//            Inputs[z].setText(Data.get(position).get(z + 1));
//                Inputs[z].addTextChangedListener(getTextWatcher(Data, position, z + 1));
//            }
//        }
//    }

    public static class FourVH extends BasicViewHolder {

        public FourVH(View view) {
            super(view);
            Inputs = new EditText[4];
        }

        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Inputs[0] = view.findViewById(R.id.FourVarInputLeftUp);
            Inputs[1] = view.findViewById(R.id.FourVarInputRightUp);
            Inputs[2] = view.findViewById(R.id.FourVarInputLeftBottom);
            Inputs[3] = view.findViewById(R.id.FourVarInputRightBottom);
            for (int z = 0; z < 4; z++) {
                Inputs[z].setText(Data.get(position).get(z + 1));
                Inputs[z].addTextChangedListener(getTextWatcher(Data, position, z + 1, this));
            }
        }
    }

    public static class FiveVH extends BasicViewHolder {

        public FiveVH(View view) {
            super(view);
            Inputs = new EditText[5];
        }

        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Inputs[0] = view.findViewById(R.id.FiveVarInputLeftUp);
            Inputs[1] = view.findViewById(R.id.FiveVarInputRightUp);
            Inputs[2] = view.findViewById(R.id.FiveVarInputLeftBottom);
            Inputs[3] = view.findViewById(R.id.FiveVarInputRightBottom);
            Inputs[4] = view.findViewById(R.id.FiveVarInputLast);
            for (int z = 0; z < 5; z++) {
                Inputs[z].setText(Data.get(position).get(z + 1));
                Inputs[z].addTextChangedListener(getTextWatcher(Data, position, z + 1, this));
            }
        }
    }
}
