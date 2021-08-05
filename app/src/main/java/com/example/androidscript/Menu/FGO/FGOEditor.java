package com.example.androidscript.Menu.FGO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.FileOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class FGOEditor extends UIActivity {

    public static final String[] PreStageBlock = {"PreStage", "0", "0", "0", ""};
    public static final String[] SkillBlock = {"Skill", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    public static final String[] CraftSkillBlock = {"CraftSkill", "0", "0", "0", "0"};
    public static final String[] NoblePhantasmsBlock = {"NoblePhantasms", "0", "0", "0", "0"};
    public static final String[] EndBlock = {"End"};



    public static String linx(float x, float w, float m){
        return Integer.toString(Math.round(w/2+m*(x-960)));
    }
    public static String liny(float y, float h, float m){
        return Integer.toString(Math.round(h/2+m*(y-600)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation","Landscape")));
        BtnMaker.registerOnClick(R.id.start_floating, this, v -> StartService.startFloatingWidget(this));
    }
    @Override
    protected void setRecycleButton() {
        this.ButtonView.setLayoutManager(new GridLayoutManager(this, 4));
        this.ButtonView.setAdapter(new FGOButtonAdapter(BlockData, ButtonData, ((FGOBlockAdapter) Objects.requireNonNull(BlockView.getAdapter())).onOrderChange));
    }

    @Override
    protected void setRecycleBlock() {
        this.BlockView.setLayoutManager(new LinearLayoutManager(this));
        this.BlockView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        this.BlockView.setAdapter(new FGOBlockAdapter(BlockData));
    }

    @Override
    public Vector<Vector<String>> getBlockData() {
        Vector<Vector<String>> ret = new Vector<>();
        ret.add(new Vector<>(Arrays.asList(PreStageBlock)));
        for (int i = 0; i <= 2; i++) {
            switch (i % 3) {
                case 2:
                    ret.add(new Vector<>(Arrays.asList(SkillBlock)));
                    break;
                case 1:
                    ret.add(new Vector<>(Arrays.asList(NoblePhantasmsBlock)));
                    break;
                case 0:
                    ret.add(new Vector<>(Arrays.asList(CraftSkillBlock)));
                    break;
            }
        }
        ret.add(new Vector<>(Arrays.asList(EndBlock)));

        return ret;
    }

    @Override
    public Vector<String> getButtonData() {
        Vector<String> ret = new Vector<>();
        ret.add("SelectCard");
        ret.add("CraftSkill");
        ret.add("ServantSkill");
        return ret;
    }

    @Override
    public String getFolderName() {
        return "";
    }

    public static void savetointp(String filename, Vector<Vector<String>> data, float w, float h) {
        if(h > w){
            float tmp = w;
            w = h;
            h = tmp;
        }
        float m = 1;
        if(9*w < 16*h){
            m = w/1920;
        }else{
            m = h/1200;
        }

        Vector<String> save = new Vector<String>();

        for(int i=0; i<data.size(); i++){
            switch (data.get(i).get(0)){
                case"PreStage":
                    save.add("Click "+linx(1400, w, m)+" "+liny(320, h, m)); //選擇上次關卡
                    save.add("Wait 3000");
                    //吃果 todo 單行判斷忽略 截圖判斷
//                    switch (data.get(i).get(1)){
//                        case"0":
//                            save.add("Click "+linx(960, w, m)+" "+liny(330, h, m));
//                            break;
//                        case"1":
//                            save.add("Click "+linx(960, w, m)+" "+liny(540, h, m));
//                            break;
//                        case"2":
//                            save.add("Click "+linx(960, w, m)+" "+liny(750, h, m));
//                            break;
//                        case"3":
//                            save.add("Swipe "+linx(960, w, m)+" "+liny(786, h, m)+" "+linx(960, w, m)+" "+liny(516, h, m));
//                            save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
//                            break;
//                    }
//                    save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));

                    //選擇好友 TODO 職階
                    save.add("Click "+linx(648, w, m)+" "+liny(264, h, m));//選擇術職
                    save.add("Wait 500");
                    switch (data.get(i).get(2)){
                        case"0":
                            save.add("Click "+linx(800, w, m)+" "+liny(467, h, m));
                            break;
                        //TODO 多行截圖判斷
//                        case"1":
//                            save.add("Click "+linx(960, w, m)+" "+liny(540, h, m));
//                            break;
//                        case"2":
//                            save.add("Click "+linx(960, w, m)+" "+liny(750, h, m));
//                            break;

                    //TODO 選擇禮裝
                    //TODO 重複次數
                    }
                    save.add("Wait 3000");
                    save.add("Click "+linx(1785, w, m)+" "+liny(1077, h, m));//任務開始
                    save.add("Wait 25000");
                    break;

                case"CraftSkill":
                    for(int j=1; j<4; j++){
                        float varx = 0 ;
                        switch (j){
                            case(1):
                                varx = 1359;
                                break;
                            case(2):
                                varx = 1490;
                                break;
                            case(3):
                                varx = 1616;
                                break;
                        }
                        switch (data.get(i).get(j)){
                            case"0":
                                break;
                            case"1":
                                save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
                                save.add("Wait 500");
                                save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
                                save.add("Wait 4000");
                                break;
                            case"2":
                                save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
                                save.add("Wait 500");
                                save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(507, w, m)+" "+liny(731, h, m));//從者一
                                save.add("Wait 4000");
                                break;
                            case"3":
                                save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
                                save.add("Wait 500");
                                save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(957, w, m)+" "+liny(731, h, m));//從者二
                                save.add("Wait 4000");
                                break;
                            case"4":
                                save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
                                save.add("Wait 500");
                                save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(1434, w, m)+" "+liny(731, h, m));//從者三
                                save.add("Wait 4000");
                                break;
                        }
                    }
                    //TODO 換人
//                    switch (data.get(i).get(4)){
//                        case"0":
//                            break;
//                        case"1"://TODO 刪除無對象
//                            break;
//                        case"2":
//                            save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
//                            save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
//                            save.add("Click "+linx(507, w, m)+" "+liny(731, h, m));//從者一
//                            break;
//                        case"3":
//                            save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
//                            save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
//                            save.add("Click "+linx(957, w, m)+" "+liny(731, h, m));//從者二
//                            break;
//                        case"4":
//                            save.add("Click "+linx(1798, w, m)+" "+liny(530, h, m));//御主技能
//                            save.add("Click "+linx(varx, w, m)+" "+liny(530, h, m));//開技能
//                            save.add("Click "+linx(1434, w, m)+" "+liny(731, h, m));//從者三
//                            break;
                    break;

                case"Skill":
                    for(int j=1; j<10; j++){
                        float varx = 0 ;
                        switch (j){
                            case(1):
                                varx = 114;
                                break;
                            case(2):
                                varx = 241;
                                break;
                            case(3):
                                varx = 380;
                                break;
                            case(4):
                                varx = 581;
                                break;
                            case(5):
                                varx = 718;
                                break;
                            case(6):
                                varx = 862;
                                break;
                            case(7):
                                varx = 1055;
                                break;
                            case(8):
                                varx = 1200;
                                break;
                            case(9):
                                varx = 1333;
                                break;
                        }
                        switch (data.get(i).get(j)){
                            case"0":
                                break;
                            case"1":
                                save.add("Click "+linx(varx, w, m)+" "+liny(930, h, m));//開技能
                                save.add("Wait 4000");
                                break;
                            case"2":
                                save.add("Click "+linx(varx, w, m)+" "+liny(930, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(507, w, m)+" "+liny(731, h, m));//從者一
                                save.add("Wait 4000");
                                break;
                            case"3":
                                save.add("Click "+linx(varx, w, m)+" "+liny(930, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(957, w, m)+" "+liny(731, h, m));//從者二
                                save.add("Wait 4000");
                                break;
                            case"4":
                                save.add("Click "+linx(varx, w, m)+" "+liny(930, h, m));//開技能
                                save.add("Wait 500");
                                save.add("Click "+linx(1434, w, m)+" "+liny(731, h, m));//從者三
                                save.add("Wait 4000");
                                break;
                        }
                    }
                    break;

                case"NoblePhantasms":
                    save.add("Click "+linx(1694, w, m)+" "+liny(969, h, m));//攻擊鈕
                    save.add("Wait 2500");
                    for(int j=1; j<4; j++){
                        float varx = 0 ;
                        switch (j){
                            case(1):
                                varx = 611;
                                break;
                            case(2):
                                varx = 972;
                                break;
                            case(3):
                                varx = 1330;
                                break;
                        }
                        switch (data.get(i).get(j)){
                            case"0":
                                break;
                            case"1":
                                save.add("Click "+linx(varx, w, m)+" "+liny(364, h, m));//寶具
                                save.add("Wait 300");
                                break;

                        }
                    }
                    //TODO　判斷剩餘卡　戰鬥至回合結束
                    save.add("Click "+linx(190, w, m)+" "+liny(835, h, m));//指令卡一
                    save.add("Wait 300");
                    save.add("Click "+linx(611, w, m)+" "+liny(835, h, m));//指令卡二
                    save.add("Wait 40000");
                    //TODO 配卡
//                    switch (data.get(i).get(4)){
//                        case"0":
//                            break;
//                        case"1":
//                            break;
//                        case"2":
//                            break;
//                        case"3":
//                            break;
//                        case"4":
//                            break;
//                        case"5":
//                            break;
                    break;

                case"End":
                    save.add("Wait 20000");
                    save.add("Click "+linx(1639, w, m)+" "+liny(1080, h, m));
                    save.add("Wait 1000");
                    save.add("Click "+linx(1639, w, m)+" "+liny(1080, h, m));
                    save.add("Wait 1000");
                    save.add("Click "+linx(1639, w, m)+" "+liny(1080, h, m));
                    save.add("Wait 2000");
                    save.add("Click "+linx(659, w, m)+" "+liny(907, h, m));//關閉
                    break;
            }
        FileOperation.writeToFile(filename, save);
        }
    }
}
