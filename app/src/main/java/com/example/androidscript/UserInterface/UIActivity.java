package com.example.androidscript.UserInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidscript.R;
import com.example.androidscript.util.DebugMessage;

import java.util.ArrayList;

public abstract class UIActivity extends AppCompatActivity {
    RecyclerView mRecyclerView, ButtonRecyclerView;
    BlockAdapter mBlockAdapter;
    ButtonAdapter mButtonAdapter;
    ArrayList<Integer> BlockContent = new ArrayList<>();
    ArrayList<String> ButtonText = new ArrayList<>();
    Button DataMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiactivity);
        DataMaker = findViewById(R.id.button);
        //取得資料
        BlockContent = BlockData();
        ButtonText = ButtonData();
        //設置Button部分
        ButtonRecyclerView = findViewById(R.id.buttongrid);
        ButtonRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mButtonAdapter = new ButtonAdapter();
        ButtonRecyclerView.setAdapter(mButtonAdapter);
        //設置Block部分
        mRecyclerView = findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBlockAdapter = new BlockAdapter();
        mRecyclerView.setAdapter(mBlockAdapter);

    }

    public abstract ArrayList<Integer> BlockData();

    public abstract ArrayList<String> ButtonData();

    //Adapter for blockview
    public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {


        class BlockViewHolder extends RecyclerView.ViewHolder {
            private CardView cardView;

            public BlockViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.battlestage);
            }
        }


        public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.script_battlestage, parent, false);
            return new BlockViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BlockViewHolder holder, int position1) {

        }

        @Override
        public int getItemCount() {
            return BlockContent.size();
        }
    }

    //Adapter for button_item
    private class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
        private int LayoutId;
        private int ButtonId;
        class ButtonViewHolder extends RecyclerView.ViewHolder {
            private Button button;

            public ButtonViewHolder(View view) {
                super(view);
                button = view.findViewById(R.id.button_item);
            }
        }

        /*public void GetLayoutId(int id){
            LayoutId = id;
        }*/

        @Override
        public ButtonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.button_item, viewGroup, false);

            return new ButtonViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ButtonViewHolder viewHolder, final int position2) {
            viewHolder.button.setText(ButtonText.get(position2));
        }

        @Override
        public int getItemCount() {
            return ButtonText.size();
        }
    }


}