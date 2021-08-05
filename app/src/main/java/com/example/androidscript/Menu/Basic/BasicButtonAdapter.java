package com.example.androidscript.Menu.Basic;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.androidscript.UserInterface.ButtonAdapter;

import java.util.Vector;

public class BasicButtonAdapter extends ButtonAdapter {
    private final Vector<Vector<String>> BlockContent;
    private static final int insertPosition = 0;
    BasicBlockAdapter.updateOrder onInsert;

    public BasicButtonAdapter(Vector<Vector<String>> _BlockContent, Vector<String> _ButtonText, BasicBlockAdapter.updateOrder _onInsert) {
        super(_ButtonText);
        this.ButtonText = _ButtonText;
        this.BlockContent = _BlockContent;
        this.onInsert = _onInsert;
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        holder.button.setText(ButtonText.get(position));
        switch (ButtonText.get(position)) {
            case "Exit":
            case "Contain":
            case "JumpTo":
            case "Wait":
            case "Call":
            case "Tag":
            case "Return":
            case "Click":
            case "CallArg":
            case "IfGreater":
            case "IfSmaller":
            case "Add":
            case "Subtract":
            case "Var":
            case "Swipe":
            case "Compare":
                holder.button.setOnClickListener(buttonListener(ButtonText.get(position)));
                break;
            default:
                throw new RuntimeException("Unrecognized button!");
        }
    }

    private View.OnClickListener buttonListener(String blockTitle) {
        return v -> {
            BlockContent.insertElementAt(BasicEditor.Blocks.get(blockTitle), insertPosition);
            onInsert.insert();
        };
    }
}
