package com.example.androidscript.Menu.FGO;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import org.jetbrains.annotations.NotNull;

import static com.example.androidscript.Menu.FGO.FGOBlockAdapter.updateOrder;
import java.util.Vector;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {

    public abstract void retrieveData(Vector<Vector<String>> Data,int position);

    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;

    public FGOViewHolder(@NonNull @NotNull View itemView) {
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

    //============================================================
    public static class PreStageVH extends FGOViewHolder {
        Spinner Stamina;
        Spinner Friend;
        Spinner Craft;
        EditText Repeat;


        @Override
        public void retrieveData(Vector<Vector<String>> Data, int position) {
            Vector<String> ret = Data.get(position);
            if(ret.size() == 1){
                ret.add((String) Stamina.getSelectedItem());
                ret.add((String) Friend.getSelectedItem());
                ret.add((String) Craft.getSelectedItem());
                ret.add(Repeat.getText().toString());
            }
            else{
                ret.set(1,(String) Stamina.getSelectedItem());
                ret.set(2,(String) Friend.getSelectedItem());
                ret.set(3,(String) Craft.getSelectedItem());
                ret.set(4,Repeat.getText().toString());
            }
        }

        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            Stamina = view.findViewById(R.id.stamina);
            Friend = view.findViewById(R.id.friend);
            Craft = view.findViewById(R.id.craft);
            Repeat = view.findViewById(R.id.n_repeat);

            Stamina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DebugMessage.set((String) Stamina.getSelectedItem());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

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

        @Override
        public void retrieveData(Vector<Vector<String>> Data, int position) {
            Vector<String> ret = Data.get(position);
            if(ret.size() == 1){
                ret.add((String) Skl11.getSelectedItem());
                ret.add((String) Skl12.getSelectedItem());
                ret.add((String) Skl13.getSelectedItem());
                ret.add((String) Skl21.getSelectedItem());
                ret.add((String) Skl22.getSelectedItem());
                ret.add((String) Skl23.getSelectedItem());
                ret.add((String) Skl31.getSelectedItem());
                ret.add((String) Skl32.getSelectedItem());
                ret.add((String) Skl33.getSelectedItem());
            }
            else{
                ret.set(1,(String) Skl11.getSelectedItem());
                ret.set(2,(String) Skl12.getSelectedItem());
                ret.set(3,(String) Skl13.getSelectedItem());
                ret.set(4,(String) Skl21.getSelectedItem());
                ret.set(5,(String) Skl22.getSelectedItem());
                ret.set(6,(String) Skl23.getSelectedItem());
                ret.set(7,(String) Skl31.getSelectedItem());
                ret.set(8,(String) Skl32.getSelectedItem());
                ret.set(9,(String) Skl33.getSelectedItem());
            }
            return;
        }


        public SkillVH(View v) {super(v);}

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            Skl11 = view.findViewById(R.id.skill_1_1);
            Skl12 = view.findViewById(R.id.skill_1_2);
            Skl13 = view.findViewById(R.id.skill_1_3);
            Skl21 = view.findViewById(R.id.skill_2_1);
            Skl22 = view.findViewById(R.id.skill_2_2);
            Skl23 = view.findViewById(R.id.skill_2_3);
            Skl31 = view.findViewById(R.id.skill_3_1);
            Skl32 = view.findViewById(R.id.skill_3_2);
            Skl33 = view.findViewById(R.id.skill_3_3);
        }
    }

    public static class CraftSkillVH extends FGOViewHolder {
        Spinner Skl1;
        Spinner Skl2;
        Spinner Skl3;
        Spinner SklX;

        @Override
        public void retrieveData(Vector<Vector<String>> Data, int position) {
            Vector<String> ret = Data.get(position);
            if(ret.size() == 1){
                ret.add((String) Skl1.getSelectedItem());
                ret.add((String) Skl2.getSelectedItem());
                ret.add((String) Skl3.getSelectedItem());
                ret.add((String) SklX.getSelectedItem());
            }
            else{
                ret.set(1,(String) Skl1.getSelectedItem());
                ret.set(2,(String) Skl2.getSelectedItem());
                ret.set(3,(String) Skl3.getSelectedItem());
                ret.set(4,(String) SklX.getSelectedItem());
            }
        }

        public CraftSkillVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            Skl1 = view.findViewById(R.id.skill_1_1);
            Skl2 = view.findViewById(R.id.skill_1_2);
            Skl3 = view.findViewById(R.id.skill_1_3);
            SklX = view.findViewById(R.id.skill_2_1);
        }
    }


    public static class NoblePhantasmsVH extends FGOViewHolder {
        androidx.appcompat.widget.SwitchCompat N1;
        androidx.appcompat.widget.SwitchCompat N2;
        androidx.appcompat.widget.SwitchCompat N3;


        @Override
        public void retrieveData(Vector<Vector<String>> Data, int position) {
            Vector<String> ret = Data.get(position);
            if(ret.size() == 1){
                ret.add(Boolean.toString(N1.isChecked()));
                ret.add(Boolean.toString(N2.isChecked()));
                ret.add(Boolean.toString(N3.isChecked()));
            }
            else{
                ret.set(1,Boolean.toString(N1.isChecked()));
                ret.set(2,Boolean.toString(N2.isChecked()));
                ret.set(3,Boolean.toString(N3.isChecked()));
            }
        }

        public NoblePhantasmsVH(View v) {            super(v);}

        @Override
        public void onBind(updateOrder order, int position) {
            super.onBind(order, position);
            N1 = view.findViewById(R.id.switch1);
            N2 = view.findViewById(R.id.switch2);
            N3 = view.findViewById(R.id.switch3);
        }
    }
    public static class EndVH extends FGOViewHolder {

        @Override
        public void retrieveData(Vector<Vector<String>> Data, int position) {

        }

        public EndVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position) {
        }
    }
}
