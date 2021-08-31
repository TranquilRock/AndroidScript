package com.example.androidscript.Menu.FGO;

import android.util.Size;

import com.example.androidscript.util.FileOperation;
import com.example.androidscript.util.ScreenShot;
import com.example.androidscript.util.ScriptCompiler;

import org.opencv.core.Point;

import java.util.Vector;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class FGOScriptCompiler2 extends ScriptCompiler {
    public static int tag_count;
    private static final Size dev_size = new Size(1920, 1070);
    private static final Point dev_offset = new Point(0, 64);
    private static Size user_size;
    private static Point user_offset;
    private static double ratio;
    private static Vector<String> save;

    @Override
    public void compile(Vector<Vector<String>> data) {
        save = new Vector<>();
        tag_count = 0;
        setUpScreenPara(data.get(0));
        for (Vector<String> block : data) {
            switch (block.get(0)) {//Get block name
                case "PreStage":
                    save.add("Log PreStage");
                    PreStage(block);
                    break;
                case "CraftSkill":
                    save.add("Log CraftSkill");
                    waitUntilAttackButton();
                    CraftSkill(block);
                    break;
                case "Skill":
                    save.add("Log Skill");
                    waitUntilAttackButton();
                    Skill(block);
                    break;
                case "NoblePhantasms":
                    save.add("Log NoblePhantasms");
                    waitUntilAttackButton();
                    NoblePhantasms(block);
                    break;
                case "End":
                    save.add("Log End");
                    End();
                    break;
            }
        }
        FileOperation.writeLines(FGOEditor.FolderName + "Run.txt", save);
    }

    private static void countDownAndExit(String VarName, String n) {
        save.add("IfGreater " + VarName + " " + n);
        save.add("Exit");
        save.add("Add " + VarName + " 1");
    }

    private static void checkMenu(String trueTag) {
        save.add("Compare " + transform_x(1665) + " " + transform_y(1039) + " " + transform_x(1910) + " " + transform_y(1130) + " menu.png");
        save.add("IfGreater $R 15");
        save.add("JumpTo " + trueTag);
    }

    private static void checkFriendPage(String trueTag) {
        //TODO
    }

    private static void checkApplePage(String trueTag) {
        //TODO
    }

    private static void selectFriend(String EntryTag, String Servant, String Craft) {
        if ("None".equals(Servant)) {
            save.add("Click " + transform_x(800) + " " + transform_y(467));
            save.add("JumpTo $FriendEnd");
        }
        String[] servantClass = Servant.split("_");
        switch (servantClass[servantClass.length - 1]) {
            case "All":
                save.add("Click " + transform_x(148) + " " + transform_y(260));
                break;
            case "Saber":
                save.add("Click " + transform_x(248) + " " + transform_y(260));
                break;
            case "Archer":
                save.add("Click " + transform_x(348) + " " + transform_y(260));
                break;
            case "Lancer":
                save.add("Click " + transform_x(448) + " " + transform_y(260));
                break;
            case "Rider":
                save.add("Click " + transform_x(548) + " " + transform_y(260));
                break;
            case "Caster":
                save.add("Click " + transform_x(648) + " " + transform_y(260));
                break;
            case "Assassin":
                save.add("Click " + transform_x(748) + " " + transform_y(260));
                break;
            case "Berserker":
                save.add("Click " + transform_x(848) + " " + transform_y(260));
                break;
            case "Extra":
                save.add("Click " + transform_x(948) + " " + transform_y(260));
                break;
            default:
                save.add("Click " + transform_x(1048) + " " + transform_y(260));
        }

        save.add("Wait 500");
        save.add("Tag $FriendsDNE");

        save.add("Var $FriendsDown 0");
        save.add("IfGreater $FriendsDown 5");
        save.add("JumpTo $Refresh");
        save.add("JumpTo $RefreshSkip");
        save.add("Tag $Refresh");
        save.add("Click " + transform_x(1271) + " " + transform_y(254));
        save.add("Wait 2000");
        save.add("Click " + transform_x(1259) + " " + transform_y(905));
        save.add("Wait 2000");
        save.add("Tag $RefreshSkip");
        save.add("Compare " + transform_x(70) + " " + transform_y(340) + " " + transform_x(330) + " " + transform_y(624) + " Friend/" + Servant + ".png");
        save.add("IfGreater $R 50");
        save.add("JumpTo $Friends");
        save.add("Wait 2000");
        save.add("Swipe " + transform_x(960) + " " + transform_y(937) + " " + transform_x(960) + " " + transform_y(689));
        save.add("JumpTo $FriendsDNE");
        save.add("Tag $Friends");
        if (!"None".equals(Craft)) {//Servant Craft
            save.add("Compare " + transform_x(70) + " " + transform_y(480) + " " + transform_x(330) + " " + transform_y(700) + " Craft/" + Craft + ".png");
            save.add("IfGreater $R 5");
            save.add("JumpTo $Craft");
            save.add("Wait 2000");
            save.add("Swipe " + transform_x(960) + " " + transform_y(937) + " " + transform_x(960) + " " + transform_y(689));
            save.add("JumpTo $FriendsDNE");
            save.add("Tag $Craft");
            save.add("JumpTo $CraftEnd");
            save.add("Tag $CraftEnd");
        }
        save.add("Click " + transform_x(800) + " " + transform_y(540));
        save.add("JumpTo $FriendsEnd");
        save.add("Tag $FriendsEnd");
    }

    private static void eatApple(String EntryTag, String appleType) {
        save.add("Compare " + transform_x(757) + " " + transform_y(922) + " " + transform_x(1149) + " " + transform_y(1041) + " close_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo " + EntryTag);
        save.add("JumpTo $AppleEnd");

        save.add("Tag " + EntryTag);
        switch (appleType) {
            case "0":
                save.add("Wait 300000");
                save.add("Click " + transform_x(960) + " " + transform_y(987));
                save.add("JumpTo $NotReadyToEnterStage");
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
                save.add("Click " + transform_x(960) + " " + transform_y(904));
                break;
        }
        save.add("Wait 3000");
        save.add("Compare " + transform_x(757) + " " + transform_y(922) + " " + transform_x(1149) + " " + transform_y(1041) + " close_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $AppleErr");
        save.add("JumpTo $AppleNoErr");
        save.add("Tag $AppleErr");
        save.add("Click " + transform_x(924) + " " + transform_y(1002));
        save.add("JumpTo $NotReadyToEnterStage");
        save.add("Tag $AppleNoErr");
        save.add("Wait 3000");
        save.add("Click " + transform_x(1260) + " " + transform_y(900));
        save.add("Wait 3000");
        save.add("Tag $AppleEnd");
        save.add("Wait 1000");
    }

    private static void waitUntilClickEnter(){
        //TODO:: Compare before click
        save.add("Wait 3000");
        save.add("Click " + transform_x(1785) + " " + transform_y(1077));//Start Operation
    }

    private static void PreStage(Vector<String> block) {
        //Init
        save.add("Var $Loop 1");
        //Start
        save.add("Tag $Start");
        countDownAndExit("$Loop", block.get(4));
        //================Entering Stage==========================
        save.add("Tag $NotReadyToEnterStage");
        checkMenu("$ReadyToEnterStage");
        save.add("Wait 1000");
        save.add("JumpTo $NotReadyToEnterStage");

        save.add("Tag $ReadyToEnterStage");
        save.add("Wait 1000");
        save.add("Click " + transform_x(1400) + " " + transform_y(320)); //Select stage
        //========================================================
        save.add("Tag $BeforeEnter");
        checkFriendPage("$ChooseFriend");
        checkApplePage("$EatApple");
        save.add("JumpTo $BeforeEnter");
        eatApple("$EatApple", block.get(1));
        selectFriend("$ChooseFriend", block.get(2), block.get(3));
        waitUntilClickEnter();
    }

    private static void CraftSkillAux(float x, float servant) {
        save.add("Click " + transform_x(1798) + " " + transform_y(530));//Click Master Craft Skill
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
        save.add("Wait 2100");
        tag_count++;
    }

    private static void CraftSkillChangeAux(float servant1, float servant2) {
        save.add("Click " + transform_x(1798) + " " + transform_y(530));//御主技能
        save.add("Wait 500");
        save.add("Click " + transform_x(1622) + " " + transform_y(530));//開技能
        save.add("Wait 500");
        save.add("Compare " + transform_x(382) + " " + transform_y(626) + " " + transform_x(908) + " " + transform_y(766) + " cancel_btn.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $CraftSkill" + tag_count);
        save.add("Click " + transform_x(servant1) + " " + transform_y(590));//Target 1
        save.add("Click " + transform_x(servant2) + " " + transform_y(590));//Target 2
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

    private static final int[] master_craft_x_coordinate = {-1, 1359, 1490, 1616};

    private static void CraftSkill(Vector<String> block) {
        float x;
        for (int j = 1; j < 4; j++) {
            x = master_craft_x_coordinate[j];
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
                    save.add("Wait 2100");
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

    private static void SkillAux(float x, float servant) {
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
        save.add("Wait 2100");
        tag_count++;
    }

    private static final int[] skill_x_coordinate = {-1, 114, 241, 380, 581, 718, 862, 1055, 1200, 1333};

    private static void Skill(Vector<String> block) {
        float x;
        for (int j = 1; j < 10; j++) {
            x = skill_x_coordinate[j];
            switch (block.get(j)) {
                case "0"://Do nothing
                    break;
                case "1"://No target
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
                    save.add("Wait 2100");
                    tag_count++;
                    break;
                case "2"://target servant 1
                    SkillAux(x, 507);
                    break;
                case "3"://target servant 2
                    SkillAux(x, 957);
                    break;
                case "4"://target servant 3
                    SkillAux(x, 1434);
                    break;
            }
        }
    }

    private static final int[] noble_phantasms_x_coordinate = {-1, 611, 972, 1330};

    private static void NoblePhantasms(Vector<String> block) {
        save.add("Click " + transform_x(1694) + " " + transform_y(969));//Attack
        save.add("Wait 2300");
        for (int j = 1; j <= 3; j++) {
            if (block.get(j).equals("1")) {
                save.add("Click " + transform_x(noble_phantasms_x_coordinate[j]) + " " + transform_y(364));//Noble Phantasms
            }
        }
        save.add("Click " + transform_x(190) + " " + transform_y(835));//Command Card
        save.add("Click " + transform_x(611) + " " + transform_y(835));//Command Card
        save.add("Click " + transform_x(1032) + " " + transform_y(835));//Command Card
        save.add("Click " + transform_x(1453) + " " + transform_y(835));//Command Card
        save.add("Click " + transform_x(1874) + " " + transform_y(835));//Command Card
        save.add("Wait 8000");
    }

    private static final Vector<String> ClickAll = new Vector<>();

    static {
        ClickAll.add("");
        ClickAll.add("1");
        ClickAll.add("1");
        ClickAll.add("1");
    }

    private static void End() {

        save.add("Wait 3000");
        save.add("Tag $EndStageAgain");

        save.add("Compare " + transform_x(1556) + " " + transform_y(1031) + " " + transform_x(1777) + " " + transform_y(1116) + " end.png");
        save.add("IfGreater $R 5");
        save.add("JumpTo $EndStage");

        save.add("Compare " + transform_x(1560) + " " + transform_y(830) + " " + transform_x(1843) + " " + transform_y(1109) + " attack.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $EndStageBattle");

        save.add("Click " + transform_x(960) + " " + transform_y(85));
        save.add("Wait 2000");
        save.add("JumpTo $EndStageAgain");

        save.add("Tag $EndStageBattle");
        NoblePhantasms(ClickAll);
        save.add("JumpTo $EndStageAgain");

        save.add("Tag $EndStage");
        save.add("Wait 1000");
        save.add("Click " + transform_x(1658) + " " + transform_y(1073));
        save.add("Wait 1000");
        save.add("Click " + transform_x(1658) + " " + transform_y(1073));
        save.add("JumpTo $Start");
    }

    private static void waitUntilAttackButton() {
        save.add("Tag $StillBattleAgain" + tag_count);
        save.add("Compare " + transform_x(1560) + " " + transform_y(830) + " " + transform_x(1843) + " " + transform_y(1109) + " attack.png");
        save.add("IfGreater $R 30");
        save.add("JumpTo $StillBattle" + tag_count);
        save.add("Wait 1000");
        save.add("JumpTo $StillBattleAgain" + tag_count);
        save.add("Tag $StillBattle" + tag_count);
        save.add("Wait 1000");
        tag_count++;
    }

    //====================================================================-

    private static void setUpScreenPara(Vector<String> config) {
        if (config.get(5).equals("0")) {
            user_size = new Size(max(ScreenShot.getHeight(), ScreenShot.getWidth()),
                    min(ScreenShot.getHeight(), ScreenShot.getWidth()));
            if (user_size.getWidth() * 9 >= user_size.getHeight() * 16) {
                user_offset = new Point(user_size.getWidth() / 2.0 - user_size.getHeight() * 8.0 / 9, 0);
            } else {
                user_offset = new Point(0, user_size.getHeight() / 2.0 - user_size.getWidth() * 9.0 / 32);
            }
        } else {
            user_offset = new Point(Integer.parseInt(config.get(6)), Integer.parseInt(config.get(7)));
            user_size = new Size(Integer.parseInt(config.get(8)) - Integer.parseInt(config.get(6)),
                    Integer.parseInt(config.get(9)) - Integer.parseInt(config.get(7)));
        }

        ratio = min(user_size.getWidth() / (double) dev_size.getWidth(),
                user_size.getHeight() / (double) dev_size.getHeight());
    }

    private static String transform_x(float x) {
        return String.valueOf((int) (ratio * (x - dev_offset.x) + user_offset.x));
    }

    private static String transform_y(float y) {
        return String.valueOf((int) (ratio * (y - dev_offset.y) + user_offset.y));
    }
}
