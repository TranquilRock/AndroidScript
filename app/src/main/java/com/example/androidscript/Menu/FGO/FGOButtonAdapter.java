package com.example.androidscript.Menu.FGO;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.ButtonAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Vector;

public class FGOButtonAdapter extends ButtonAdapter {
    private final Vector<Vector<String>> BlockContent;
    private static final int insertPosition = 1;
    FGOBlockAdapter.updateOrder onInsert;

    public FGOButtonAdapter(Vector<Vector<String>> _BlockContent, Vector<String> _ButtonText, FGOBlockAdapter.updateOrder _onInsert) {
        super(_ButtonText);
        this.ButtonText = _ButtonText;
        this.BlockContent = _BlockContent;
        this.onInsert = _onInsert;
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        holder.button.setText(ButtonText.get(position));
        switch (ButtonText.get(position)) {
            case "從者技能":
                holder.button.setOnClickListener(v -> {
                    Vector<String> tmp = (new Vector<>());
                    tmp.add("Skill");
                    BlockContent.insertElementAt(tmp, insertPosition);
                    onInsert.insert();
                });
                break;

            case "自動選卡":
                holder.button.setOnClickListener(v -> {
                    Vector<String> tmp = (new Vector<>());
                    tmp.add("NoblePhantasms");
                    BlockContent.insertElementAt(tmp, insertPosition);
                    onInsert.insert();
                });
                break;

            case "御主技能":
                holder.button.setOnClickListener(v -> {
                    Vector<String> tmp = (new Vector<>());
                    tmp.add("CraftSkill");
                    BlockContent.insertElementAt(tmp, insertPosition);
                    onInsert.insert();
                });
                break;

            default:
                throw new RuntimeException("Unrecognized button!");
        }
    }
}