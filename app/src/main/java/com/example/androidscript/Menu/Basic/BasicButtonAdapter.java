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
            case "Click":
            case "Compare":
            case "Call":
            case "IfGreater":
            case "IfSmaller":
            case "Var":
            case "JumpToLine":
            case "Wait":
            case "Return":
            case "Exit":
                holder.button.setOnClickListener(buttonListener(ButtonText.get(position)));
                break;
            default:
                throw new RuntimeException("Unrecognized button!");
        }
    }

    private View.OnClickListener buttonListener(String content) {
        return v -> {
            Vector<String> tmp = new Vector<>();
            tmp.add(content);
            BlockContent.insertElementAt(tmp, insertPosition);
            onInsert.insert();
        };
    }
}
