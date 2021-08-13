package com.example.androidscript.Menu.FGO;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import org.jetbrains.annotations.NotNull;

import static com.example.androidscript.Menu.FGO.FGOBlockAdapter.updateOrder;

import java.util.Vector;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {

    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;

    protected AdapterView.OnItemSelectedListener SpinnerListener(Vector<Vector<String>> Data, int pos, int index) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Data.get(pos).set(index,String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }


    public FGOViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {
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

    //============================================================
    public static class PreStageVH extends FGOViewHolder {
        Spinner Stamina;
        Spinner Friend;
        Spinner Craft;
        EditText Repeat;
        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {
            Stamina = view.findViewById(R.id.stamina);
            Stamina.setOnItemSelectedListener(SpinnerListener(Data,position,1));
            Stamina.setSelection(Integer.parseInt(Data.get(position).get(1)));
            Friend = view.findViewById(R.id.friend);
            Friend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Data.get(position).set(2,parent.getSelectedItem().toString());
                    DebugMessage.set(parent.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            for(int i = 0; i < Friend.getCount();i++){
                if(Data.get(position).get(2).equals(Friend.getItemAtPosition(i))){
                    Friend.setSelection(i);
                }
            }
            Craft = view.findViewById(R.id.craft);
            Craft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Data.get(position).set(3,parent.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            for(int i = 0; i < Craft.getCount();i++){
                if(Data.get(position).get(3).equals(Craft.getItemAtPosition(i))){
                    Craft.setSelection(i);
                }
            }
            Repeat = view.findViewById(R.id.n_repeat);
            Repeat.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    Data.get(position).setElementAt(s.toString(),4);
                }
            });
            Repeat.setText(Data.get(position).get(4));
        }
    }


    public static class SkillVH extends FGOViewHolder {
        Spinner Skl11;
        Spinner Skl12;
        Spinner Skl13;
        Spinner Skl21;
        Spinner Skl22;
        Spinner Skl23;
        Spinner Skl31;
        Spinner Skl32;
        Spinner Skl33;

        public SkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {
            super.onBind(order, position,Data);
            Skl11 = view.findViewById(R.id.skill_1_1);
            Skl12 = view.findViewById(R.id.skill_1_2);
            Skl13 = view.findViewById(R.id.skill_1_3);
            Skl21 = view.findViewById(R.id.skill_2_1);
            Skl22 = view.findViewById(R.id.skill_2_2);
            Skl23 = view.findViewById(R.id.skill_2_3);
            Skl31 = view.findViewById(R.id.skill_3_1);
            Skl32 = view.findViewById(R.id.skill_3_2);
            Skl33 = view.findViewById(R.id.skill_3_3);

            Skl11.setOnItemSelectedListener(SpinnerListener(Data,position,1));
            Skl12.setOnItemSelectedListener(SpinnerListener(Data,position,2));
            Skl13.setOnItemSelectedListener(SpinnerListener(Data,position,3));
            Skl21.setOnItemSelectedListener(SpinnerListener(Data,position,4));
            Skl22.setOnItemSelectedListener(SpinnerListener(Data,position,5));
            Skl23.setOnItemSelectedListener(SpinnerListener(Data,position,6));
            Skl31.setOnItemSelectedListener(SpinnerListener(Data,position,7));
            Skl32.setOnItemSelectedListener(SpinnerListener(Data,position,8));
            Skl33.setOnItemSelectedListener(SpinnerListener(Data,position,9));

            Skl11.setSelection(Integer.parseInt(Data.get(position).get(1)));
            Skl12.setSelection(Integer.parseInt(Data.get(position).get(2)));
            Skl13.setSelection(Integer.parseInt(Data.get(position).get(3)));
            Skl21.setSelection(Integer.parseInt(Data.get(position).get(4)));
            Skl22.setSelection(Integer.parseInt(Data.get(position).get(5)));
            Skl23.setSelection(Integer.parseInt(Data.get(position).get(6)));
            Skl31.setSelection(Integer.parseInt(Data.get(position).get(7)));
            Skl32.setSelection(Integer.parseInt(Data.get(position).get(8)));
            Skl33.setSelection(Integer.parseInt(Data.get(position).get(9)));
        }
    }

    public static class CraftSkillVH extends FGOViewHolder {
        Spinner Skl1;
        Spinner Skl2;
        Spinner Skl3;
        Spinner SklX;

        public CraftSkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {
            super.onBind(order, position,Data);
            Skl1 = view.findViewById(R.id.skill_1_1);
            Skl2 = view.findViewById(R.id.skill_1_2);
            Skl3 = view.findViewById(R.id.skill_1_3);
            SklX = view.findViewById(R.id.skill_2_1);

            Skl1.setOnItemSelectedListener(SpinnerListener(Data,position,1));
            Skl2.setOnItemSelectedListener(SpinnerListener(Data,position,2));
            Skl3.setOnItemSelectedListener(SpinnerListener(Data,position,3));
            SklX.setOnItemSelectedListener(SpinnerListener(Data,position,4));

            Skl1.setSelection(Integer.parseInt(Data.get(position).get(1)));
            Skl2.setSelection(Integer.parseInt(Data.get(position).get(2)));
            Skl3.setSelection(Integer.parseInt(Data.get(position).get(3)));
            SklX.setSelection(Integer.parseInt(Data.get(position).get(4)));
        }
    }


    public static class NoblePhantasmsVH extends FGOViewHolder {
        androidx.appcompat.widget.SwitchCompat N1;
        androidx.appcompat.widget.SwitchCompat N2;
        androidx.appcompat.widget.SwitchCompat N3;
        Spinner Color;
        public NoblePhantasmsVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {
            super.onBind(order, position,Data);
            N1 = view.findViewById(R.id.switch1);
            N2 = view.findViewById(R.id.switch2);
            N3 = view.findViewById(R.id.switch3);
            N1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    Data.get(position).set(1,"1");
                }else{
                    Data.get(position).set(1,"0");
                }
            });
            N1.setChecked(Data.get(position).get(1).equals("1"));
            N2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    Data.get(position).set(2,"1");
                }else{
                    Data.get(position).set(2,"0");
                }
            });
            N2.setChecked(Data.get(position).get(2).equals("1"));
            N3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    Data.get(position).set(3,"1");
                }else{
                    Data.get(position).set(3,"0");
                }
            });
            N3.setChecked(Data.get(position).get(3).equals("1"));
            Color = view.findViewById(R.id.card_color);
            Color.setOnItemSelectedListener(SpinnerListener(Data,position,4));
            Color.setSelection(Integer.parseInt(Data.get(position).get(4)));
        }
    }

    public static class EndVH extends FGOViewHolder {
        public EndVH(View v) {
            super(v);
        }
        @Override
        public void onBind(updateOrder order, int position,Vector<Vector<String>> Data) {}
    }
}
