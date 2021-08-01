package com.example.androidscript.Menu.Basic;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.Menu.FGO.FGOBlockAdapter;
import com.example.androidscript.Menu.FGO.FGOButtonAdapter;
import com.example.androidscript.UserInterface.UIActivity;

import java.util.Objects;
import java.util.Vector;

public class BasicEditor extends UIActivity {
    @Override
    protected Vector<String> getBlockData() {
        Vector<String> ret = new Vector<>();
        ret.add("Click");
        ret.add("Compare");
        ret.add("Var");
        ret.add("IfGreater");
        ret.add("Call");
        ret.add("Wait");
        ret.add("JumpToLine");
        ret.add("Exit");
        ret.add("IfSmaller");
        ret.add("Return");
        return ret;
    }

    @Override
    protected Vector<String> getButtonData() {
        Vector<String> ret = new Vector<>();
        ret.add("Click");
        ret.add("Compare");
        ret.add("Var");
        ret.add("IfGreater");
        ret.add("IfSmaller");
        ret.add("Call");
        ret.add("Wait");
        ret.add("JumpToLine");
        ret.add("Return");
        ret.add("Exit");
        return ret;
    }

    @Override
    protected void setRecycleBlock() {
        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new BasicBlockAdapter(BlockData));
    }

    @Override
    protected void setRecycleButton() {
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 4));
        this.ButtonView.setAdapter(new BasicButtonAdapter(BlockData, ButtonData, ((BasicBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    public String getFolderName() {
        return null;
    }
}
