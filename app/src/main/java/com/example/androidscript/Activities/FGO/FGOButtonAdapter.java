package com.example.androidscript.Activities.FGO;

import androidx.annotation.NonNull;

import com.example.androidscript.UITemplate.BlockAdapter;
import com.example.androidscript.UITemplate.ButtonAdapter;

import java.util.Arrays;
import java.util.Vector;

public class FGOButtonAdapter extends ButtonAdapter {
    private final Vector<Vector<String>> BlockContent;
    private static final int insertPosition = 1;
    BlockAdapter.updater onInsert;

    public FGOButtonAdapter(Vector<Vector<String>> _BlockContent, Vector<String> _ButtonText, BlockAdapter.updater _onInsert) {
        super(_ButtonText);
        this.ButtonText = _ButtonText;
        this.BlockContent = _BlockContent;
        this.onInsert = _onInsert;
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        switch (ButtonText.get(position)) {
            case "ServantSkill":
                holder.button.setText("從者技能");
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt(new Vector<>(Arrays.asList(FGOEditor.SkillBlock)), insertPosition);
                    onInsert.insert();
                });
                break;
            case "SelectCard":
                holder.button.setText("自動選卡");
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt(new Vector<>(Arrays.asList(FGOEditor.NoblePhantasmsBlock)), insertPosition);
                    onInsert.insert();
                });
                break;
            case "CraftSkill":
                holder.button.setText("御主技能");
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt(new Vector<>(Arrays.asList(FGOEditor.CraftSkillBlock)), insertPosition);
                    onInsert.insert();
                });
                break;
            default:
                throw new RuntimeException("Unrecognized button!");
        }
    }
}