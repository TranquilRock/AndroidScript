package com.example.androidscript.Menu.FGO;

import android.util.Log;

import java.util.Vector;

import com.example.androidscript.FloatingWidget.FloatingWidgetService;
import com.example.androidscript.util.ScriptCompiler;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.Interpreter;
import com.example.androidscript.util.ScreenShot;

public class FGOScriptCompiler extends ScriptCompiler {
    @Override
    public void compile(Vector<Vector<String>> data) {
        float h = ScreenShot.getHeight();
        float w = ScreenShot.getWidth();
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

        }
        FileOperation.writeLines(FGOEditor.FolderName + "Run.txt", save);
        Log.d("kk", "Save run script");
        FloatingWidgetService.setScript(new Interpreter(FGOEditor.FolderName, "Run.txt"), null);
        Log.d("kk", "Set FloatingScript");
    }

    public static String transform_x(float x, float w, float m) {
        return Integer.toString((int) Math.round(w / 2.0 + m * (x - 960)));
    }

    public static String transform_y(float y, float h, float m) {
        return Integer.toString((int) Math.round(h / 2.0 + m * (y - 600)));
    }

    public static void PreStage(Vector<String> save, Vector<String> block, float w, float h, float m) {
        //重複
        save.add("Var $Loop 1");
        save.add("IfGreater $Loop " + block.get(4));
        save.add("JumpTo $End");
        save.add("Click " + transform_x(1400, w, m) + " " + transform_y(320, h, m)); //選擇上次關卡
        save.add("Wait 3000");
        save.add("Compare " + transform_x(757, w, m) + " " + transform_y(922, h, m) + " " + transform_x(1149, w, m) + " " + transform_y(1041, h, m) + " missionstartbtn.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $Apple");
        save.add("JumpTo $AppleEnd");
        save.add("Tag $Apple");
        switch (block.get(1)) {
            case "0":
                save.add("Click " + transform_x(960, w, m) + " " + transform_y(330, h, m));
                break;
            case "1":
                save.add("Click " + transform_x(960, w, m) + " " + transform_y(540, h, m));
                break;
            case "2":
                save.add("Click " + transform_x(960, w, m) + " " + transform_y(750, h, m));
                break;
            case "3":
                save.add("Swipe " + transform_x(960, w, m) + " " + transform_y(786, h, m) + " " + transform_x(960, w, m) + " " + transform_y(516, h, m));
                save.add("Wait 300");
                save.add("Click " + transform_x(960, w, m) + " " + transform_y(320, h, m));
                break;
        }
        save.add("Wait 300");
        save.add("Click " + transform_x(1260, w, m) + " " + transform_y(900, h, m));
        save.add("Wait 300");
        save.add("Click " + transform_x(960, w, m) + " " + transform_y(987, h, m));
        save.add("JumpTo $AppleEnd");
        save.add("Tag $AppleEnd");
        save.add("Wait 10000");

        save.add("Click " + transform_x(1050, w, m) + " " + transform_y(260, h, m));//選擇MIX職階
        save.add("Wait 500");
        if ("0".equals(block.get(2))) {
        } else if ("1".equals(block.get(2))) {
            save.add("Click " + transform_x(800, w, m) + " " + transform_y(467, h, m));
        } else if ("2".equals(block.get(2))) {
            save.add("Tag $FriendsDNE");
            save.add("Compare " + transform_x(68, w, m) + " " + transform_y(350, h, m) + " " + transform_x(323, w, m) + " " + transform_y(554, h, m) + " CBA.png");
            save.add("IfGreater $R 30");
            save.add("JumpTo $Friends");
            save.add("Swipe " + transform_x(960, w, m) + " " + transform_y(731, h, m) + " " + transform_x(960, w, m) + " " + transform_y(620, h, m));
            save.add("JumpTo $FriendsDNE");
            save.add("Tag $Friends");
            save.add("Click " + transform_x(800, w, m) + " " + transform_y(467, h, m));
            save.add("JumpTo $FriendsEnd");
            save.add("Tag $FriendsEnd");
        } else if ("3".equals(block.get(2))) {
            save.add("Tag $FriendsDNE");
            save.add("Compare " + transform_x(68, w, m) + " " + transform_y(350, h, m) + " " + transform_x(323, w, m) + " " + transform_y(554, h, m) + " KM.png");
            save.add("IfGreater $R 30");
            save.add("JumpTo $Friends");
            save.add("Swipe " + transform_x(960, w, m) + " " + transform_y(731, h, m) + " " + transform_x(960, w, m) + " " + transform_y(620, h, m));
            save.add("JumpTo $FriendsDNE");
            save.add("Tag $Friends");
            save.add("Click " + transform_x(800, w, m) + " " + transform_y(467, h, m));
            save.add("JumpTo $FriendsEnd");
            save.add("Tag $FriendsEnd");
        } else {

        }
        save.add("Wait 3000");
        save.add("Click " + transform_x(1785, w, m) + " " + transform_y(1077, h, m));//任務開始
        save.add("Wait 25000");
    }

    public static void CraftSkillAux(Vector<String> save, float w, float h, float m, float varx, int tag, float sev) {
        save.add("Click " + transform_x(1798, w, m) + " " + transform_y(530, h, m));//御主技能
        save.add("Wait 500");
        save.add("Click " + transform_x(varx, w, m) + " " + transform_y(530, h, m));//開技能
        save.add("Compare " + transform_x(382, w, m) + " " + transform_y(626, h, m) + " " + transform_x(908, w, m) + " " + transform_y(766, h, m) + " canclebtn.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $CraftSkill" + Integer.toString(tag));
        save.add("Click " + transform_x(sev, w, m) + " " + transform_y(731, h, m));//從者
        save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
        save.add("Tag $CraftSkill" + Integer.toString(tag));
        save.add("Click " + transform_x(645, w, m) + " " + transform_y(696, h, m));//取消BUG
        save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
        save.add("Tag $CraftSkillEnd" + Integer.toString(tag));
        save.add("Wait 4000");
        tag++;
    }

    public static void CraftSkillChangeAux(Vector<String> save, float w, float h, float m, int tag, float sev1, float sev2) {
        save.add("Click " + transform_x(1798, w, m) + " " + transform_y(530, h, m));//御主技能
        save.add("Wait 500");
        save.add("Click " + transform_x(1622, w, m) + " " + transform_y(530, h, m));//開技能
        save.add("Wait 500");
        save.add("Compare " + transform_x(382, w, m) + " " + transform_y(626, w, m) + " " + transform_x(908, w, m) + " " + transform_y(766, h, m) + " canclebtn.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $CraftSkill" + Integer.toString(tag));
        save.add("Click " + transform_x(sev1, w, m) + " " + transform_y(sev2, h, m));//換
        save.add("Click " + transform_x(1120, w, m) + " " + transform_y(590, h, m));
        save.add("Click " + transform_x(950, w, m) + " " + transform_y(1000, h, m));
        save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
        save.add("Tag $CraftSkill" + Integer.toString(tag));
        save.add("Click " + transform_x(645, w, m) + " " + transform_y(696, h, m));//取消BUG
        save.add("Wait 500");
        save.add("Click " + transform_x(1798, w, m) + " " + transform_y(530, h, m));//御主技能
        save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
        save.add("Tag $CraftSkillEnd" + Integer.toString(tag));
        save.add("Wait 8000");
    }

    public static void CraftSkill(Vector<String> save, Vector<String> block, float w, float h, float m, int tag) {
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
                    save.add("Click " + transform_x(1798, w, m) + " " + transform_y(530, h, m));//御主技能
                    save.add("Wait 500");
                    save.add("Click " + transform_x(varx, w, m) + " " + transform_y(530, h, m));//開技能
                    save.add("Compare " + transform_x(382, w, m) + " " + transform_y(626, h, m) + " " + transform_x(908, w, m) + " " + transform_y(766, h, m) + " canclebtn.png");
                    save.add("IfGreater $R 30");
                    save.add("JumpTo $CraftSkill" + Integer.toString(tag));
                    save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
                    save.add("Tag $CraftSkill" + Integer.toString(tag));
                    save.add("Click " + transform_x(645, w, m) + " " + transform_y(696, h, m));//取消BUG
                    save.add("JumpTo $CraftSkillEnd" + Integer.toString(tag));
                    save.add("Tag $CraftSkillEnd" + Integer.toString(tag));
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

    public static void SkillAux(Vector<String> save, float w, float h, float m, float varx, int tag, float sev) {
        save.add("Click " + transform_x(varx, w, m) + " " + transform_y(930, h, m));//開技能
        save.add("Wait 500");
        save.add("Compare " + transform_x(382, w, m) + " " + transform_y(626, h, m) + " " + transform_x(908, w, m) + " " + transform_y(766, h, m) + " canclebtn.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $Skill" + Integer.toString(tag));
        save.add("Click " + transform_x(sev, w, m) + " " + transform_y(731, h, m));//從者一
        save.add("JumpTo $SkillEnd" + Integer.toString(tag));
        save.add("Tag $Skill" + Integer.toString(tag));
        save.add("Click " + transform_x(645, w, m) + " " + transform_y(696, h, m));//取消BUG
        save.add("JumpTo $SkillEnd" + Integer.toString(tag));
        save.add("Tag $SkillEnd" + Integer.toString(tag));
        save.add("Wait 4000");
        tag++;
    }

    public static void Skill(Vector<String> save, Vector<String> block, float w, float h, float m, int tag) {
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
                    save.add("Click " + transform_x(varx, w, m) + " " + transform_y(930, h, m));//開技能
                    save.add("Wait 500");
                    save.add("Compare " + transform_x(382, w, m) + " " + transform_y(626, h, m) + " " + transform_x(908, w, m) + " " + transform_y(766, h, m) + " canclebtn.png");
                    save.add("IfGreater $R 30");
                    save.add("JumpTo $Skill" + Integer.toString(tag));
                    save.add("JumpTo $SkillEnd" + Integer.toString(tag));
                    save.add("Tag $Skill" + Integer.toString(tag));
                    save.add("Click " + transform_x(645, w, m) + " " + transform_y(696, h, m));//取消BUG
                    save.add("JumpTo $SkillEnd" + Integer.toString(tag));
                    save.add("Tag $SkillEnd" + Integer.toString(tag));
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

    public static void NoblePhantasms(Vector<String> save, Vector<String> block, float w, float h, float m) {
        save.add("Click " + transform_x(1694, w, m) + " " + transform_y(969, h, m));//攻擊鈕
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
            if (block.get(j) == "1") {
                save.add("Click " + transform_x(varx, w, m) + " " + transform_y(364, h, m));//寶具
                save.add("Wait 500");
            }
        }
        save.add("Click " + transform_x(190, w, m) + " " + transform_y(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + transform_x(611, w, m) + " " + transform_y(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + transform_x(1032, w, m) + " " + transform_y(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + transform_x(1453, w, m) + " " + transform_y(835, h, m));//指令卡
        save.add("Wait 500");
        save.add("Click " + transform_x(1874, w, m) + " " + transform_y(835, h, m));//指令卡
        save.add("Tag $WaveOverAgain");
        save.add("Compare " + transform_x(1560, w, m) + " " + transform_y(830, h, m) + " " + transform_x(1843, w, m) + " " + transform_y(1109, h, m) + " attack.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $WaveOver");
        save.add("Wait 5000");
        save.add("JumpTo $WaveOverAgain");
        save.add("Tag $WaveOver");

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

    public static void End(Vector<String> save, float w, float h, float m) {
        save.add("Tag $EndStageAgain");
        save.add("Compare " + transform_x(86, w, m) + " " + transform_y(301, h, m) + " " + transform_x(474, w, m) + " " + transform_y(382, h, m) + " end.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $EndStage");
        save.add("Wait 5000");
        save.add("JumpTo $EndStageAgain");
        save.add("Tag $EndStage");

        save.add("Tag $EndStageAgain2");
        save.add("Compare " + transform_x(453, w, m) + " " + transform_y(855, h, m) + " " + transform_x(878, w, m) + " " + transform_y(971, h, m) + " close2btn.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $EndStage2");
        save.add("Wait 2000");
        save.add("Click " + transform_x(1639, w, m) + " " + transform_y(1080, h, m));
        save.add("JumpTo $EndStageAgain2");
        save.add("Tag $EndStage2");

        save.add("Click " + transform_x(660, w, m) + " " + transform_y(900, h, m));
        save.add("Wait 1000");
        save.add("Add $Loop 1");
        save.add("Tag $End");
    }

}
