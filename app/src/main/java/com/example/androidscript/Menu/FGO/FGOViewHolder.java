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

import org.jetbrains.annotations.NotNull;

import static com.example.androidscript.Menu.FGO.FGOBlockAdapter.updateOrder;

import java.util.Vector;

public abstract class FGOViewHolder extends RecyclerView.ViewHolder {

    protected ImageButton Up;
    protected ImageButton Down;
    protected ImageButton Close;
    protected View view;

    protected static CompoundButton.OnCheckedChangeListener getOnCheckedChange(Vector<String> data, int index){
        return (buttonView, isChecked) -> {
            if (isChecked) {
                data.set(index, "1");
            } else {
                data.set(index, "0");
            }
        };
    }

    protected static void setRawSpinner(Spinner spn, Vector<String> data, int index) {
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                data.set(index, parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        for (int i = 0; i < spn.getCount(); i++) {
            if (data.get(index).equals(spn.getItemAtPosition(i))) {
                spn.setSelection(i);
            }
        }
    }

    protected static AdapterView.OnItemSelectedListener SpinnerListener(Vector<String> data, int index) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.set(index, String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    protected static TextWatcher getTextWatcher(Vector<String> data,int index){
        return (new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                data.setElementAt(s.toString(),  index);
            }
        });
    }

    public FGOViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
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
        androidx.appcompat.widget.SwitchCompat Config;
        EditText X1,Y1,X2,Y2;

        public PreStageVH(View v) {
            super(v);
        }

        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            Vector<String> data = Data.get(position);

            Stamina = view.findViewById(R.id.stamina);
            Stamina.setOnItemSelectedListener(SpinnerListener(data, 1));
            Stamina.setSelection(Integer.parseInt(data.get(1)));

            Friend = view.findViewById(R.id.friend);
            setRawSpinner(Friend,data, 2);

            Craft = view.findViewById(R.id.craft);
            setRawSpinner(Craft,data, 3);

            Repeat = view.findViewById(R.id.n_repeat);
            Repeat.addTextChangedListener(getTextWatcher(data,4));
            Repeat.setText(data.get(4));


            Config = view.findViewById(R.id.config);
            Config.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    data.set(5, "1");
                } else {
                    data.set(5, "0");
                }
                order.self(position);
            });
            Config.setChecked(data.get(5).equals("1"));

            X1 = view.findViewById(R.id.x1);
            Y1 = view.findViewById(R.id.y1);
            X2 = view.findViewById(R.id.x2);
            Y2 = view.findViewById(R.id.y2);

            if(Config.isChecked()){
                view.findViewById(R.id.game_pos).setVisibility(View.VISIBLE);
                X1.setVisibility(View.VISIBLE);
                Y1.setVisibility(View.VISIBLE);
                X2.setVisibility(View.VISIBLE);
                Y2.setVisibility(View.VISIBLE);

                X1.addTextChangedListener(getTextWatcher(data,6));
                X1.setText(data.get(6));

                Y1.addTextChangedListener(getTextWatcher(data,7));
                Y1.setText(data.get(7));

                X2.addTextChangedListener(getTextWatcher(data,8));
                X2.setText(data.get(8));

                Y2.addTextChangedListener(getTextWatcher(data,9));
                Y2.setText(data.get(9));
            }else{
                view.findViewById(R.id.game_pos).setVisibility(View.GONE);
                X1.setVisibility(View.GONE);
                Y1.setVisibility(View.GONE);
                X2.setVisibility(View.GONE);
                Y2.setVisibility(View.GONE);
            }
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
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Vector<String> data = Data.get(position);

            Skl11 = view.findViewById(R.id.skill_1_1);
            Skl12 = view.findViewById(R.id.skill_1_2);
            Skl13 = view.findViewById(R.id.skill_1_3);
            Skl21 = view.findViewById(R.id.skill_2_1);
            Skl22 = view.findViewById(R.id.skill_2_2);
            Skl23 = view.findViewById(R.id.skill_2_3);
            Skl31 = view.findViewById(R.id.skill_3_1);
            Skl32 = view.findViewById(R.id.skill_3_2);
            Skl33 = view.findViewById(R.id.skill_3_3);

            Skl11.setOnItemSelectedListener(SpinnerListener(data, 1));
            Skl12.setOnItemSelectedListener(SpinnerListener(data, 2));
            Skl13.setOnItemSelectedListener(SpinnerListener(data, 3));
            Skl21.setOnItemSelectedListener(SpinnerListener(data, 4));
            Skl22.setOnItemSelectedListener(SpinnerListener(data, 5));
            Skl23.setOnItemSelectedListener(SpinnerListener(data, 6));
            Skl31.setOnItemSelectedListener(SpinnerListener(data, 7));
            Skl32.setOnItemSelectedListener(SpinnerListener(data, 8));
            Skl33.setOnItemSelectedListener(SpinnerListener(data, 9));

            Skl11.setSelection(Integer.parseInt(data.get(1)));
            Skl12.setSelection(Integer.parseInt(data.get(2)));
            Skl13.setSelection(Integer.parseInt(data.get(3)));
            Skl21.setSelection(Integer.parseInt(data.get(4)));
            Skl22.setSelection(Integer.parseInt(data.get(5)));
            Skl23.setSelection(Integer.parseInt(data.get(6)));
            Skl31.setSelection(Integer.parseInt(data.get(7)));
            Skl32.setSelection(Integer.parseInt(data.get(8)));
            Skl33.setSelection(Integer.parseInt(data.get(9)));
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
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Vector<String> data = Data.get(position);
            Skl1 = view.findViewById(R.id.skill_1_1);
            Skl2 = view.findViewById(R.id.skill_1_2);
            Skl3 = view.findViewById(R.id.skill_1_3);
            SklX = view.findViewById(R.id.skill_2_1);

            Skl1.setOnItemSelectedListener(SpinnerListener(data, 1));
            Skl2.setOnItemSelectedListener(SpinnerListener(data, 2));
            Skl3.setOnItemSelectedListener(SpinnerListener(data, 3));
            SklX.setOnItemSelectedListener(SpinnerListener(data, 4));

            Skl1.setSelection(Integer.parseInt(data.get(1)));
            Skl2.setSelection(Integer.parseInt(data.get(2)));
            Skl3.setSelection(Integer.parseInt(data.get(3)));
            SklX.setSelection(Integer.parseInt(data.get(4)));
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
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
            super.onBind(order, position, Data);
            Vector<String> data = Data.get(position);

            N1 = view.findViewById(R.id.switch1);
            N1.setOnCheckedChangeListener(getOnCheckedChange(data,1));
            N1.setChecked(data.get(1).equals("1"));

            N2 = view.findViewById(R.id.switch2);
            N2.setOnCheckedChangeListener(getOnCheckedChange(data,2));
            N2.setChecked(data.get(2).equals("1"));

            N3 = view.findViewById(R.id.switch3);
            N3.setOnCheckedChangeListener(getOnCheckedChange(data,3));
            N3.setChecked(data.get(3).equals("1"));

            Color = view.findViewById(R.id.card_color);
            Color.setOnItemSelectedListener(SpinnerListener(data, 4));
            Color.setSelection(Integer.parseInt(data.get(4)));
        }
    }

    public static class EndVH extends FGOViewHolder {
        public EndVH(View v) {
            super(v);
        }
        @Override
        public void onBind(updateOrder order, int position, Vector<Vector<String>> Data) {
        }
    }
}
