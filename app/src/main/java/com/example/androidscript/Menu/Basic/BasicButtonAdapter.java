package com.example.androidscript.Menu.Basic;

import androidx.annotation.NonNull;

import com.example.androidscript.UserInterface.ButtonAdapter;

import java.util.Vector;

public class BasicButtonAdapter extends ButtonAdapter {
    private final Vector<String> BlockContent;
    private static final int insertPosition = 0;
    BasicBlockAdapter.updateOrder onInsert;

    public BasicButtonAdapter(Vector<String> _BlockContent, Vector<String> _ButtonText, BasicBlockAdapter.updateOrder _onInsert) {
        super(_ButtonText);
        this.ButtonText = _ButtonText;
        this.BlockContent = _BlockContent;
        this.onInsert = _onInsert;
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position){
        holder.button.setText(ButtonText.get(position));
        switch (ButtonText.get(position)) {
            case "Click":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Click", insertPosition);
                    onInsert.insert();
                });
                break;

            case "Compare":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Compare", insertPosition);
                    onInsert.insert();
                });
                break;

            case "Call":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Call", insertPosition);
                    onInsert.insert();
                });
                break;
            case "IfGreater":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("IfGreater", insertPosition);
                    onInsert.insert();
                });
                break;
            case "IfSmaller":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("IfSmaller", insertPosition);
                    onInsert.insert();
                });
                break;
            case "Var":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Var", insertPosition);
                    onInsert.insert();
                });
                break;
            case "JumpToLine":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("JumpToLine", insertPosition);
                    onInsert.insert();
                });
                break;
            case "Wait":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Wait", insertPosition);
                    onInsert.insert();
                });
                break;
            case "Return":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Return", insertPosition);
                    onInsert.insert();
                });
                break;
            case "Exit":
                holder.button.setOnClickListener(v -> {
                    BlockContent.insertElementAt("Exit", insertPosition);
                    onInsert.insert();
                });
                break;


            default:
                throw new RuntimeException("Unrecognized button!");
        }
    }
}
