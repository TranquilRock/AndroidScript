package com.example.androidscript.Menu.FGO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.Menu.StartService;
import com.example.androidscript.R;
import com.example.androidscript.UserInterface.UIActivity;
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;

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


    public static String linx(float x, float w, float m) {
        return Integer.toString((int) Math.round(w / 2.0 + m * (x - 960)));
    }

    public static String liny(float y, float h, float m) {
        return Integer.toString((int) Math.round(h / 2.0 + m * (y - 600)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BtnMaker.registerOnClick(R.id.start_service, this, v -> startActivity(new Intent(this, StartService.class).putExtra("Orientation", "Landscape")));
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

    public static final String FolderName = "FGO/";

    @Override
    public String getFolderName() {
        return FolderName;
    }

    public static void compile(String filename, Vector<Vector<String>> data, float w, float h) {
        if (h > w) {
            float tmp = w;
            w = h;
            h = tmp;
        }
        float m = 1;
        if (9 * w < 16 * h) {
            m = w / 1920;
        } else {
            m = h / 1200;
        }

        Vector<String> save = new Vector<String>();
        int tag = 0;

        for (Vector<String> block : data) {
            if ("PreStage".equals(block.get(0))) {
                PreStage(save, block, w, h, m);

            } else if ("CraftSkill".equals(block.get(0))) {
                CraftSkill(save, block, w, h, m, tag);

            } else if ("Skill".equals(block.get(0))) {
                Skill(save, block, w, h, m, tag);

            } else if ("NoblePhantasms".equals(block.get(0))) {
                NoblePhantasms(save, block, w, h, m);

            } else if ("End".equals(block.get(0))) {
                End(save, w, h, m);
            }
            FileOperation.writeToFile(FolderName + filename, save);
            FloatingWidgetService.setScript(new Interpreter(FolderName,filename),null);
        }
    }

    public static void PreStage(Vector<String> save ,Vector<String> block, float w, float h, float m){
        //重複
        save.add("Var $Loop 1");
        save.add("IfGreater $Loop "+block.get(4));
        save.add("JumpTo $End");
        save.add("Click " + linx(1400, w, m) + " " + liny(320, h, m)); //選擇上次關卡
        save.add("Wait 3000");
        save.add("Compare "+linx(757, w, m)+" "+liny(922, w, m)+" "+linx(1149, w, m)+" "+liny(1041, h, m)+" missionstartbtn.png");
        save.add("JumpTo $Apple");
        save.add("jumpTo $AppleEnd");
        save.add("Tag $Apple");
        switch (block.get(1)){
            case"0":
                save.add("Click "+linx(960, w, m)+" "+liny(330, h, m));
                break;
            case"1":
                save.add("Click "+linx(960, w, m)+" "+liny(540, h, m));
                break;
            case"2":
                save.add("Click "+linx(960, w, m)+" "+liny(750, h, m));
                break;
            case"3":
                save.add("Swipe "+linx(960, w, m)+" "+liny(786, h, m)+" "+linx(960, w, m)+" "+liny(516, h, m));
                save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
                break;
        }
        save.add("Click "+linx(960, w, m)+" "+liny(320, h, m));
        save.add("jumpTo $AppleEnd");
        save.add("Tag $AppleEnd");

        save.add("Click " + linx(1050, w, m) + " " + liny(260, h, m));//選擇MIX職階
        save.add("Wait 500");
        switch (block.get(2)) {
            case "0":
                break;
            case"1":
                save.add("Click " + linx(800, w, m) + " " + liny(467, h, m));
                break;
            case"2":
                save.add("Tag $FriendsDNE");
                save.add("Compare "+linx(68, w, m)+" "+liny(350, w, m)+" "+linx(323, w, m)+" "+liny(554, h, m)+" CBA.png");
                save.add("jumpTo $Friends");
                save.add("Swipe "+linx(960, w, m)+" "+liny(731, h, m)+" "+linx(960, w, m)+" "+liny(620, h, m));
                save.add("jumpTo $FriendsDNE");
                save.add("Tag $Friends");
                save.add("Click " + linx(800, w, m) + " " + liny(467, h, m));
                save.add("jumpTo $FriendsEnd");
                save.add("Tag $FriendsEnd");
                break;
            case"3":
                save.add("Tag $FriendsDNE");
                save.add("Compare "+linx(68, w, m)+" "+liny(350, w, m)+" "+linx(323, w, m)+" "+liny(554, h, m)+" KM.png");
                save.add("jumpTo $Friends");
                save.add("Swipe "+linx(960, w, m)+" "+liny(731, h, m)+" "+linx(960, w, m)+" "+liny(620, h, m));
                save.add("jumpTo $FriendsDNE");
                save.add("Tag $Friends");
                save.add("Click " + linx(800, w, m) + " " + liny(467, h, m));
                save.add("jumpTo $FriendsEnd");
                save.add("Tag $FriendsEnd");
                break;

            //TODO　選擇禮裝
        }
        save.add("Wait 3000");
        save.add("Click " + linx(1785, w, m) + " " + liny(1077, h, m));//任務開始
        save.add("Wait 25000");
    }

    public static void CraftSkillAux(Vector<String> save, float w, float h, float m, float varx, int tag, float sev){
        save.add("Click " + linx(1798, w, m) + " " + liny(530, h, m));//御主技能
        save.add("Wait 500");
        save.add("Click " + linx(varx, w, m) + " " + liny(530, h, m));//開技能
        save.add("Compare "+linx(382, w, m)+" "+liny(626, w, m)+" "+linx(908, w, m)+" "+liny(766, h, m)+" canclebtn.png");
        save.add("jumpTo $CraftSkill"+Integer.toString(tag));
        save.add("Click " + linx(sev, w, m) + " " + liny(731, h, m));//從者
        save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
        save.add("Tag $CraftSkill"+Integer.toString(tag));
        save.add("Click " + linx(645, w, m) + " " + liny(696, h, m));//取消BUG
        save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
        save.add("Tag $CraftSkillEnd"+Integer.toString(tag));
        save.add("Wait 4000");
        tag++;
    }

    public static void CraftSkillChangeAux(Vector<String> save, float w, float h, float m, int tag, float sev1, float sev2){
        save.add("Click " + linx(1798, w, m) + " " + liny(530, h, m));//御主技能
        save.add("Wait 500");
        save.add("Click " + linx(1622, w, m) + " " + liny(530, h, m));//開技能
        save.add("Wait 500");
        save.add("Compare "+linx(382, w, m)+" "+liny(626, w, m)+" "+linx(908, w, m)+" "+liny(766, h, m)+" canclebtn.png");
        save.add("jumpTo $CraftSkill"+Integer.toString(tag));
        save.add("Click " + linx(sev1, w, m) + " " + liny(sev2, h, m));//換
        save.add("Click " + linx(1120, w, m) + " " + liny(590, h, m));
        save.add("Click " + linx(950, w, m) + " " + liny(1000, h, m));
        save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
        save.add("Tag $CraftSkill"+Integer.toString(tag));
        save.add("Click " + linx(645, w, m) + " " + liny(696, h, m));//取消BUG
        save.add("Wait 500");
        save.add("Click " + linx(1798, w, m) + " " + liny(530, h, m));//御主技能
        save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
        save.add("Tag $CraftSkillEnd"+Integer.toString(tag));
        save.add("Wait 8000");
    }

    public static void CraftSkill(Vector<String> save, Vector<String>block, float w, float h, float m, int tag){
        for (int j = 1; j < 4; j++) {
            float varx = 0;
            switch (j) {
                case (1):
                    varx = 1359;
                    break;
                case (2):
                    varx = 1490;
                    break;
                case (3):
                    varx = 1616;
                    break;
            }
            switch (block.get(j)) {
                case "0":
                    break;
                case "1":
                    save.add("Click " + linx(1798, w, m) + " " + liny(530, h, m));//御主技能
                    save.add("Wait 500");
                    save.add("Click " + linx(varx, w, m) + " " + liny(530, h, m));//開技能
                    save.add("Compare "+linx(382, w, m)+" "+liny(626, w, m)+" "+linx(908, w, m)+" "+liny(766, h, m)+" canclebtn.png");
                    save.add("jumpTo $CraftSkill"+Integer.toString(tag));
                    save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
                    save.add("Tag $CraftSkill"+Integer.toString(tag));
                    save.add("Click " + linx(645, w, m) + " " + liny(696, h, m));//取消BUG
                    save.add("jumpTo $CraftSkillEnd"+Integer.toString(tag));
                    save.add("Tag $CraftSkillEnd"+Integer.toString(tag));
                    save.add("Wait 4000");
                    tag++;
                    break;
                case "2":
                    CraftSkillAux(save, w, h, m, varx, tag, 507);
                    break;
                case "3":
                    CraftSkillAux(save, w, h, m, varx, tag, 957);
                    break;
                case "4":
                    CraftSkillAux(save, w, h, m, varx, tag, 1434);
                    break;
            }
        }

        switch (block.get(4)) {
            case "0":
                break;
            case "1":
                CraftSkillChangeAux(save, w, h, m, tag, 210, 1120);
                break;
            case "2":
                CraftSkillChangeAux(save, w, h, m, tag, 210, 1414);
                break;
            case "3":
                CraftSkillChangeAux(save, w, h, m, tag, 210, 1700);
                break;
            case "4":
                CraftSkillChangeAux(save, w, h, m, tag, 507, 1120);
                break;
            case "5":
                CraftSkillChangeAux(save, w, h, m, tag, 507, 1414);
                break;
            case "6":
                CraftSkillChangeAux(save, w, h, m, tag, 507, 1700);
                break;
            case "7":
                CraftSkillChangeAux(save, w, h, m, tag, 810, 1120);
                break;
            case "8":
                CraftSkillChangeAux(save, w, h, m, tag, 810, 1414);
                break;
            case "9":
                CraftSkillChangeAux(save, w, h, m, tag, 810, 1700);
                break;
        }
    }

    public static void SkillAux(Vector<String> save, float w, float h, float m, float varx, int tag, float sev){
        save.add("Click " + linx(varx, w, m) + " " + liny(930, h, m));//開技能
        save.add("Wait 500");
        save.add("Compare "+linx(382, w, m)+" "+liny(626, w, m)+" "+linx(908, w, m)+" "+liny(766, h, m)+" canclebtn.png");
        save.add("jumpTo $Skill"+Integer.toString(tag));
        save.add("Click " + linx(sev, w, m) + " " + liny(731, h, m));//從者一
        save.add("jumpTo $SkillEnd"+Integer.toString(tag));
        save.add("Tag $Skill"+Integer.toString(tag));
        save.add("Click " + linx(645, w, m) + " " + liny(696, h, m));//取消BUG
        save.add("jumpTo $SkillEnd"+Integer.toString(tag));
        save.add("Tag $SkillEnd"+Integer.toString(tag));
        save.add("Wait 4000");
        tag++;
    }

    public static void Skill(Vector<String> save, Vector<String>block, float w, float h, float m, int tag){
        float varx = 0;
        for (int j = 1; j < 10; j++) {
            switch (j) {
                case (1):
                    varx = 114;
                    break;
                case (2):
                    varx = 241;
                    break;
                case (3):
                    varx = 380;
                    break;
                case (4):
                    varx = 581;
                    break;
                case (5):
                    varx = 718;
                    break;
                case (6):
                    varx = 862;
                    break;
                case (7):
                    varx = 1055;
                    break;
                case (8):
                    varx = 1200;
                    break;
                case (9):
                    varx = 1333;
                    break;
            }
            switch (block.get(j)) {
                case "0":
                    break;
                case "1":
                    save.add("Click " + linx(varx, w, m) + " " + liny(930, h, m));//開技能
                    save.add("Wait 500");
                    save.add("Compare "+linx(382, w, m)+" "+liny(626, w, m)+" "+linx(908, w, m)+" "+liny(766, h, m)+" canclebtn.png");
                    save.add("jumpTo $Skill"+Integer.toString(tag));
                    save.add("jumpTo $SkillEnd"+Integer.toString(tag));
                    save.add("Tag $Skill"+Integer.toString(tag));
                    save.add("Click " + linx(645, w, m) + " " + liny(696, h, m));//取消BUG
                    save.add("jumpTo $SkillEnd"+Integer.toString(tag));
                    save.add("Tag $SkillEnd"+Integer.toString(tag));
                    save.add("Wait 4000");
                    tag++;
                    break;
                case "2":
                    SkillAux(save, w, h, m, varx, tag, 507);
                    break;
                case "3":
                    SkillAux(save, w, h, m, varx, tag, 957);
                    break;
                case "4":
                    SkillAux(save, w, h, m, varx, tag, 1434);
                    break;
            }
        }
    }

    public static void NoblePhantasms(Vector<String> save, Vector<String> block, float w, float h, float m){
        save.add("Click " + linx(1694, w, m) + " " + liny(969, h, m));//攻擊鈕
        save.add("Wait 2500");
        for (int j = 1; j < 4; j++) {
            float varx = 0;
            if (j == 1) {
                varx = 611;
            } else if (j == 2) {
                varx = 972;
            } else if (j == 3) {
                varx = 1330;
            }
            if (block.get(j)=="1") {
                save.add("Click " + linx(varx, w, m) + " " + liny(364, h, m));//寶具
                save.add("Wait 500");
            }
        }
        save.add("Click " + linx(190, w, m) + " " + liny(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + linx(611, w, m) + " " + liny(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + linx(1032, w, m) + " " + liny(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + linx(1453, w, m) + " " + liny(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + linx(1874, w, m) + " " + liny(835, h, m));//指令卡
        save.add("Tag $WaveOverAgain");
        save.add("Compare "+linx(1560, w, m)+" "+liny(830, w, m)+" "+linx(1843, w, m)+" "+liny(1109, h, m)+" attack.png");
        save.add("JumpTo $WaveOver");
        save.add("Wait 5000");
        save.add("JumpTo $WaveOverAgain");
        save.add("Tag $WaveEOver");

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
    }

    public static void End(Vector<String> save, float w, float h, float m){
        save.add("Tag $EndStageAgain");
        save.add("Compare "+linx(86, w, m)+" "+liny(301, w, m)+" "+linx(474, w, m)+" "+liny(382, h, m)+" end.png");
        save.add("JumpTo $EndStage");
        save.add("Wait 5000");
        save.add("JumpTo $EndStageAgain");
        save.add("Tag $EndStage");

        save.add("Tag $EndStageAgain2");
        save.add("Compare "+linx(453, w, m)+" "+liny(855, w, m)+" "+linx(878, w, m)+" "+liny(971, h, m)+" close2btn.png");
        save.add("JumpTo $EndStage2");
        save.add("Wait 2000");
        save.add("Click " + linx(1639, w, m) + " " + liny(1080, h, m));
        save.add("JumpTo $EndStageAgain2");
        save.add("Tag $EndStage2");

        save.add("Click " + linx(660, w, m) + " " + liny(900, h, m));
        save.add("Wait 1000");
        save.add("Tag $End");
    }

}
