package com.example.androidscript.Menu.FGO;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;

import java.util.Vector;

import com.example.androidscript.util.DebugMessage;
import com.example.androidscript.util.ImageHandler;
import com.example.androidscript.util.ScriptCompiler;
import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.ScreenShot;

import org.opencv.core.Point;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FGOScriptCompiler extends ScriptCompiler {
    public static int tag_count = 0;
    private static final Size dev_size = new Size(1920, 1070);
    private static final Point dev_offset = new Point(0, 64);
    private static Size user_size;
    private static Point user_offset;
    private static double ratio;
    private static Vector<String> save;

    @Override
    public void compile(Vector<Vector<String>> data) {
        save = new Vector<>();
        if (data.get(0).get(5).equals("0")) {
            user_size = new Size(max(ScreenShot.getHeight(), ScreenShot.getWidth()),
                    min(ScreenShot.getHeight(), ScreenShot.getWidth()));
            user_offset = new Point(0, 0);
        } else {
            user_offset = new Point(Integer.parseInt(data.get(0).get(6)), Integer.parseInt(data.get(0).get(7)));
            user_size = new Size(Integer.parseInt(data.get(0).get(8)) - Integer.parseInt(data.get(0).get(6)),
                    Integer.parseInt(data.get(0).get(9)) - Integer.parseInt(data.get(0).get(7)));
        }

        ratio = min(user_size.getWidth() / (double) dev_size.getWidth(),
                user_size.getHeight() / (double) dev_size.getHeight());

        for (Vector<String> block : data) {
            switch (block.get(0)) {
                case "PreStage":
                    PreStage(block);
                    break;
                case "CraftSkill":
                    CraftSkill(block);
                    break;
                case "Skill":
                    Skill(block);
                    break;
                case "NoblePhantasms":
                    NoblePhantasms(block);
                    break;
                case "End":
                    End();
                    break;
            }
        }
        FileOperation.writeLines(FGOEditor.FolderName + "Run.txt", save);
    }

    private static String transform_x(float x) {
        return String.valueOf((int)(ratio * (x - dev_offset.x) + user_offset.x));
    }

    private static String transform_y(float y) {
        return String.valueOf((int)(ratio * (y - dev_offset.y) + user_offset.y));
    }

    public static void PreStage(Vector<String> block) {
        //重複
        save.add("Var $Loop 1");
        save.add("Tag $Start");

        save.add("IfGreater $Loop " + block.get(4));
        save.add("Exit");

        save.add("Tag $ReadyAgain");
        save.add("Compare " + transform_x(1665) + " " + transform_y(1039) + " " + transform_x(1910) + " " + transform_y(1130) + " menu.png");
        save.add("IfGreater $R 15");
        save.add("JumpTo $Ready");
        save.add("Wait 3000");
        save.add("JumpTo $ReadyAgain");
        save.add("Tag $Ready");
        save.add("Wait 1000");

        save.add("Click " + transform_x(1400) + " " + transform_y(320)); //選擇上次關卡
        save.add("Wait 3000");
        save.add("Compare " + transform_x(757) + " " + transform_y(922) + " " + transform_x(1149) + " " + transform_y(1041) + " close_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $Apple");
        save.add("JumpTo $AppleEnd");
        save.add("Tag $Apple");
        switch (block.get(1)) {
            case "0":
                save.add("Wait 300000");
                save.add("Click " + transform_x(960) + " " + transform_y(987));
                save.add("JumpTo $Start");
                break;
            case "1":
                save.add("Click " + transform_x(960) + " " + transform_y(330));
                break;
            case "2":
                save.add("Click " + transform_x(960) + " " + transform_y(540));
                break;
            case "3":
                save.add("Click " + transform_x(960) + " " + transform_y(750));
                break;
            case "4":
                save.add("Swipe " + transform_x(960) + " " + transform_y(786) + " " + transform_x(960) + " " + transform_y(516));
                save.add("Wait 300");
                save.add("Click " + transform_x(960) + " " + transform_y(320));
                break;
        }
        save.add("Wait 300");
        save.add("Click " + transform_x(1260) + " " + transform_y(900));
        save.add("Wait 300");
        save.add("Click " + transform_x(960) + " " + transform_y(987));
        save.add("Tag $AppleEnd");
        save.add("Wait 5000");

        if ("無".equals(block.get(2))) {
            save.add("Click " + transform_x(800) + " " + transform_y(467));
        } else {
            switch (block.get(2).charAt(block.get(2).length() - 3)) {
                case 'A':
                    save.add("Click " + transform_x(148) + " " + transform_y(260));
                    break;
                case 'b':
                    save.add("Click " + transform_x(248) + " " + transform_y(260));
                    break;
                case 'h':
                    save.add("Click " + transform_x(348) + " " + transform_y(260));
                    break;
                case 'c':
                    save.add("Click " + transform_x(448) + " " + transform_y(260));
                    break;
                case 'd':
                    save.add("Click " + transform_x(548) + " " + transform_y(260));
                    break;
                case 't':
                    save.add("Click " + transform_x(648) + " " + transform_y(260));
                    break;
                case 's':
                    save.add("Click " + transform_x(748) + " " + transform_y(260));
                    break;
                case 'k':
                    save.add("Click " + transform_x(848) + " " + transform_y(260));
                    break;
                case 'i':
                    save.add("Click " + transform_x(948) + " " + transform_y(260));
                    break;
                case 'M':
                    save.add("Click " + transform_x(1048) + " " + transform_y(260));
                    break;
            }

            save.add("Wait 500");
            save.add("Tag $FriendsDNE");
            save.add("Compare " + transform_x(70) + " " + transform_y(310) + " " + transform_x(330) + " " + transform_y(700) + " Friend/" + block.get(2) + ".png");
            save.add("IfGreater $R 20");
            save.add("JumpTo $Friends");
            save.add("Wait 2000");
            save.add("Swipe " + transform_x(960) + " " + transform_y(937) + " " + transform_x(960) + " " + transform_y(660));
            save.add("JumpTo $FriendsDNE");
            save.add("Tag $Friends");
            if (!block.get(3).equals("None")) {
                save.add("Compare " + transform_x(70) + " " + transform_y(310) + " " + transform_x(330) + " " + transform_y(700) + " Craft/" + block.get(3) + ".png");
                save.add("IfGreater $R 5");
                save.add("JumpTo $Craft");
                save.add("Wait 2000");
                save.add("Swipe " + transform_x(960) + " " + transform_y(937) + " " + transform_x(960) + " " + transform_y(660));
                save.add("JumpTo $FriendsDNE");
                save.add("Tag $Craft");
                save.add("Click " + transform_x(800) + " " + transform_y(540));
                save.add("JumpTo $CraftEnd");
                save.add("Tag $CraftEnd");
            }
            save.add("Click " + transform_x(800) + " " + transform_y(480));
            save.add("JumpTo $FriendsEnd");
            save.add("Tag $FriendsEnd");
        }

        save.add("Wait 3000");
        save.add("Click " + transform_x(1785) + " " + transform_y(1077));//任務開始
    }

    public static void CraftSkillAux(float x, float servant) {
        save.add("Click " + transform_x(1798) + " " + transform_y(530));//御主技能
        save.add("Wait 500");
        save.add("Click " + transform_x(x) + " " + transform_y(530));//開技能
        save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $CraftSkill" + tag_count);
        save.add("Click " + transform_x(servant) + " " + transform_y(731));//從者
        save.add("JumpTo $CraftSkillEnd" + tag_count);
        save.add("Tag $CraftSkill" + tag_count);
        save.add("Click " + transform_x(645) + " " + transform_y(696));//取消BUG
        save.add("JumpTo $CraftSkillEnd" + tag_count);
        save.add("Tag $CraftSkillEnd" + tag_count);
        save.add("Wait 3300");
        tag_count++;
    }

    public static void CraftSkillChangeAux(float servant1, float servant2) {
        save.add("Click " + transform_x(1798) + " " + transform_y(530));//御主技能
        save.add("Wait 500");
        save.add("Click " + transform_x(1622) + " " + transform_y(530));//開技能
        save.add("Wait 500");
        save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $CraftSkill" + tag_count);
        save.add("Click " + transform_x(servant1) + " " + transform_y(servant2));//換
        save.add("Click " + transform_x(1120) + " " + transform_y(590));
        save.add("Click " + transform_x(950) + " " + transform_y(1000));
        save.add("JumpTo $CraftSkillEnd" + tag_count);
        save.add("Tag $CraftSkill" + tag_count);
        save.add("Click " + transform_x(645) + " " + transform_y(696));//取消BUG
        save.add("Wait 500");
        save.add("Click " + transform_x(1798) + " " + transform_y(530));//御主技能
        save.add("JumpTo $CraftSkillEnd" + tag_count);
        save.add("Tag $CraftSkillEnd" + tag_count);
        save.add("Wait 8000");
        tag_count++;
    }

    public static void CraftSkill(Vector<String> block) {
        CheckStillBattle();
        for (int j = 1; j < 4; j++) {
            float x = 0;
            switch (j) {
                case (1):
                    x = 1359;
                    break;
                case (2):
                    x = 1490;
                    break;
                case (3):
                    x = 1616;
                    break;
            }
            switch (block.get(j)) {
                case "0":
                    break;
                case "1":
                    save.add("Click " + transform_x(1798) + " " + transform_y(530));//御主技能
                    save.add("Wait 500");
                    save.add("Click " + transform_x(x) + " " + transform_y(530));//開技能
                    save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
                    save.add("IfGreater $R 5");
                    save.add("JumpTo $CraftSkill" + tag_count);
                    save.add("JumpTo $CraftSkillEnd" + tag_count);
                    save.add("Tag $CraftSkill" + tag_count);
                    save.add("Click " + transform_x(645) + " " + transform_y(696));//取消BUG
                    save.add("JumpTo $CraftSkillEnd" + tag_count);
                    save.add("Tag $CraftSkillEnd" + tag_count);
                    save.add("Wait 3300");
                    tag_count++;
                    break;
                case "2":
                    CraftSkillAux(x, 507);
                    break;
                case "3":
                    CraftSkillAux(x, 957);
                    break;
                case "4":
                    CraftSkillAux(x, 1434);
                    break;
            }
        }

        switch (block.get(4)) {
            case "0":
                break;
            case "1":
                CraftSkillChangeAux(210, 1120);
                break;
            case "2":
                CraftSkillChangeAux(210, 1414);
                break;
            case "3":
                CraftSkillChangeAux(210, 1700);
                break;
            case "4":
                CraftSkillChangeAux(507, 1120);
                break;
            case "5":
                CraftSkillChangeAux(507, 1414);
                break;
            case "6":
                CraftSkillChangeAux(507, 1700);
                break;
            case "7":
                CraftSkillChangeAux(810, 1120);
                break;
            case "8":
                CraftSkillChangeAux(810, 1414);
                break;
            case "9":
                CraftSkillChangeAux(810, 1700);
                break;
        }
    }

    public static void SkillAux(float x, float servant) {
        save.add("Click " + transform_x(x) + " " + transform_y(930));//開技能
        save.add("Wait 500");
        save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $Skill" + tag_count);
        save.add("Click " + transform_x(servant) + " " + transform_y(731));//從者一
        save.add("JumpTo $SkillEnd" + tag_count);
        save.add("Tag $Skill" + tag_count);
        save.add("Click " + transform_x(645) + " " + transform_y(696));//取消BUG
        save.add("JumpTo $SkillEnd" + tag_count);
        save.add("Tag $SkillEnd" + tag_count);
        save.add("Wait 3300");
        tag_count++;
    }

    public static void Skill(Vector<String> block) {
        CheckStillBattle();
        float x = 0;
        for (int j = 1; j < 10; j++) {
            switch (j) {
                case (1):
                    x = 114;
                    break;
                case (2):
                    x = 241;
                    break;
                case (3):
                    x = 380;
                    break;
                case (4):
                    x = 581;
                    break;
                case (5):
                    x = 718;
                    break;
                case (6):
                    x = 862;
                    break;
                case (7):
                    x = 1055;
                    break;
                case (8):
                    x = 1200;
                    break;
                case (9):
                    x = 1333;
                    break;
            }
            switch (block.get(j)) {
                case "0":
                    break;
                case "1":
                    save.add("Click " + transform_x(x) + " " + transform_y(930));//開技能
                    save.add("Wait 500");
                    save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
                    save.add("IfGreater $R 5");
                    save.add("JumpTo $Skill" + tag_count);
                    save.add("JumpTo $SkillEnd" + tag_count);
                    save.add("Tag $Skill" + tag_count);
                    save.add("Click " + transform_x(645) + " " + transform_y(696));//取消BUG
                    save.add("JumpTo $SkillEnd" + tag_count);
                    save.add("Tag $SkillEnd" + tag_count);
                    save.add("Wait 3300");
                    tag_count++;
                    break;
                case "2":
                    SkillAux(x, 507);
                    break;
                case "3":
                    SkillAux(x, 957);
                    break;
                case "4":
                    SkillAux(x, 1434);
                    break;
            }
        }
    }

    public static void NoblePhantasms(Vector<String> block) {
        CheckStillBattle();
        save.add("Click " + transform_x(1694) + " " + transform_y(969));//攻擊鈕
        save.add("Wait 2500");
        int[] x_cor = {-1, 611, 972, 1330};
        for (int j = 1; j <= 3; j++) {
            Log.d("kk", block.get(j));
            if (block.get(j).equals("1")) {
                save.add("Click " + transform_x(x_cor[j]) + " " + transform_y(364));//寶具
            }
        }
        save.add("Click " + transform_x(190) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(611) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1032) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1453) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1874) + " " + transform_y(835));//指令卡
        save.add("Wait 10000");
    }

    public static void End() {

        save.add("Tag $EndStageAgain");
        save.add("Compare " + transform_x(50) + " " + transform_y(200) + " " + transform_x(500) + " " + transform_y(380) + " end.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $EndStage");

        save.add("Wait 2000");
        save.add("Compare " + transform_x(1560) + " " + transform_y(830) + " " + transform_x(1843) + " " + transform_y(1109) + " attack.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $EndStageBattle");
        save.add("Wait 2000");


        save.add("JumpTo $EndStageAgain");
        save.add("Tag $EndStageBattle");

        save.add("Click " + transform_x(1694) + " " + transform_y(969));//攻擊鈕
        save.add("Wait 2500");
        save.add("Click " + transform_x(611) + " " + transform_y(364));//寶具
        save.add("Click " + transform_x(972) + " " + transform_y(364));//寶具
        save.add("Click " + transform_x(1330) + " " + transform_y(364));//寶具
        save.add("Click " + transform_x(190) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(611) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1032) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1453) + " " + transform_y(835));//指令卡
        save.add("Click " + transform_x(1874) + " " + transform_y(835));//指令卡

        save.add("JumpTo $EndStageAgain");
        save.add("Tag $EndStage");
        save.add("Wait 1000");
        save.add("Click " + transform_x(1800) + " " + transform_y(1100));
        save.add("Wait 1000");
        save.add("Click " + transform_x(1800) + " " + transform_y(1100));
        save.add("Wait 1000");
        save.add("Click " + transform_x(1800) + " " + transform_y(1100));
        save.add("Wait 1000");
        save.add("Click " + transform_x(1800) + " " + transform_y(1100));
        save.add("Wait 1000");
        save.add("Click " + transform_x(660) + " " + transform_y(900));
        save.add("Add $Loop 1");
        save.add("JumpTo $Start");
    }

    public static void CheckStillBattle() {
        save.add("Tag $StillBattleAgain" + tag_count);
        save.add("Compare " + transform_x(1560) + " " + transform_y(830) + " " + transform_x(1843) + " " + transform_y(1109) + " attack.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $StillBattle" + tag_count);
        save.add("Wait 3000");
        save.add("JumpTo $StillBattleAgain" + tag_count);
        save.add("Tag $StillBattle" + tag_count);
        save.add("Wait 3000");
        tag_count++;
    }
}
