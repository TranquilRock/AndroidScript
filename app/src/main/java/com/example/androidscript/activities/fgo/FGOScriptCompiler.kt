package com.example.androidscript.activities.fgo

import com.example.androidscript.util.FileOperation
import com.example.androidscript.util.ScriptCompiler
import android.util.Size
import com.example.androidscript.floatingwidget.ScreenShotService
import org.opencv.core.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Suppress("SameParameterValue") //For readability
class FGOScriptCompiler : ScriptCompiler() {
    override fun compile(data: Vector<Vector<String>>) {
        save = Vector()
        tag_count = 0
        setUpScreenPara(data[0])
        for (block in data) {
            when (block[0]) {
                "PreStage" -> {
                    save.add("Log PreStage")
                    preStage(block)
                }
                "CraftSkill" -> {
                    save.add("Log CraftSkill")
                    waitUntilAttackButton()
                    craftSkill(block)
                }
                "Skill" -> {
                    save.add("Log Skill")
                    waitUntilAttackButton()
                    skill(block)
                }
                "NoblePhantasms" -> {
                    save.add("Log NoblePhantasms")
                    waitUntilAttackButton()
                    noblePhantasms(block)
                }
                "End" -> {
                    save.add("Log End")
                    end()
                }
            }
        }
        FileOperation.writeLines(FGOEditor.folderName + "Run.txt", save)
    }

    companion object {
        // TODO
        // Discussion 2022/01/26
        //    public class resourceLocation{
        //        public final int x1;
        //        public final int y1;
        //        public final int x2;
        //        public final int y2;
        //        public resourceLocation(int _x1,int _y1, int _x2, int _y2){
        //            this.x1 = _x1;
        //            this.y1 = _y1;
        //            this.x2 = _x2;
        //            this.y2 = _y2;
        //        }
        //
        //        @Override
        //        public String toString() {
        //            return "resourceLocation{" +
        //                    "x1=" + x1 +
        //                    ", y1=" + y1 +
        //                    ", x2=" + x2 +
        //                    ", y2=" + y2 +
        //                    '}';
        //        }
        //    }
        //
        //    private resourceLocation menuButton = new resourceLocation(1,1,2,2);
        //
        var tag_count = 0
        private val dev_size = Size(1920, 1070)
        private val dev_offset = Point(0.0, 64.0)
        private lateinit var user_size: Size
        private lateinit var user_offset: Point
        private var ratio = 0.0
        private lateinit var save: Vector<String>

        private fun countDownAndExit(varName: String, n: String?) {
            save.add("IfGreater $varName $n")
            save.add("Exit")
            save.add("Add $varName 1")
        }

        private fun waitUntilMenuAndSelectStage() {
            save.add("Tag \$NotReadyToEnterStage")
            save.add(
                "Compare " + transformX(1665f) + " " + transformY(1039f) + " " + transformX(
                    1910f
                ) + " " + transformY(1130f) + " menu.png"
            )
            save.add("IfGreater \$R 15")
            save.add("JumpTo \$ReadyToEnterStage")
            save.add("Wait 1000")
            save.add("JumpTo \$NotReadyToEnterStage")
            save.add("Tag \$ReadyToEnterStage")
            save.add("Wait 1000")
            save.add("Click " + transformX(1400f) + " " + transformY(320f)) //Select stage
        }

        private fun checkFriendPage(trueTag: String) {
            save.add(
                "Compare " + transformX(1551f) + " " + transformY(67f) + " " + transformX(
                    1917f
                ) + " " + transformY(154f) + " support_select.png"
            )
            save.add("IfGreater \$R 15")
            save.add("JumpTo $trueTag")
        }

        private fun checkApplePage(trueTag: String) {
            save.add(
                "Compare " + transformX(757f) + " " + transformY(922f) + " " + transformX(
                    1149f
                ) + " " + transformY(1041f) + " close_btn.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo $trueTag")
        }

        private fun selectFriend(EntryTag: String, Servant: String?, Craft: String?) {
            save.add("Tag $EntryTag")
            val servantClass = Servant!!.split("_".toRegex()).toTypedArray()
            when (servantClass[servantClass.size - 1]) {
                "All" -> save.add("Click " + transformX(148f) + " " + transformY(260f))
                "Saber" -> save.add("Click " + transformX(248f) + " " + transformY(260f))
                "Archer" -> save.add("Click " + transformX(348f) + " " + transformY(260f))
                "Lancer" -> save.add("Click " + transformX(448f) + " " + transformY(260f))
                "Rider" -> save.add("Click " + transformX(548f) + " " + transformY(260f))
                "Caster" -> save.add("Click " + transformX(648f) + " " + transformY(260f))
                "Assassin" -> save.add("Click " + transformX(748f) + " " + transformY(260f))
                "Berserker" -> save.add("Click " + transformX(848f) + " " + transformY(260f))
                "Extra" -> save.add("Click " + transformX(948f) + " " + transformY(260f))
                else -> save.add("Click " + transformX(1048f) + " " + transformY(260f))
            }
            save.add("Wait 500")
            save.add("Tag \$FriendsDNE")
            save.add("Var \$FriendsDown 0")
            save.add("IfGreater \$FriendsDown 5")
            save.add("JumpTo \$Refresh")
            save.add("JumpTo \$RefreshSkip")
            save.add("Tag \$Refresh")
            save.add("Click " + transformX(1271f) + " " + transformY(254f))
            save.add("Wait 2000")
            save.add("Click " + transformX(1259f) + " " + transformY(905f))
            save.add("Wait 2000")
            save.add("Tag \$RefreshSkip")
            if ("None" != Servant) {
                save.add(
                    "Compare " + transformX(70f) + " " + transformY(340f) + " " + transformX(
                        330f
                    ) + " " + transformY(624f) + " Friend/" + Servant + ".png"
                )
                save.add("IfGreater \$R 50")
            }
            save.add("JumpTo \$Friends")
            save.add("Wait 2000")
            save.add(
                "Swipe " + transformX(960f) + " " + transformY(937f) + " " + transformX(
                    960f
                ) + " " + transformY(689f)
            )
            save.add("JumpTo \$FriendsDNE")
            save.add("Tag \$Friends")
            if ("None" != Craft) { //Servant Craft
                save.add(
                    "Compare " + transformX(70f) + " " + transformY(480f) + " " + transformX(
                        330f
                    ) + " " + transformY(700f) + " Craft/" + Craft + ".png"
                )
                save.add("IfGreater \$R 5")
                save.add("JumpTo \$Craft")
                save.add("Wait 2000")
                save.add(
                    "Swipe " + transformX(960f) + " " + transformY(937f) + " " + transformX(
                        960f
                    ) + " " + transformY(689f)
                )
                save.add("JumpTo \$FriendsDNE")
                save.add("Tag \$Craft")
                save.add("JumpTo \$CraftEnd")
                save.add("Tag \$CraftEnd")
            }
            save.add("Click " + transformX(800f) + " " + transformY(540f))
            save.add("Tag \$FriendsEnd")
        }

        private fun eatApple(EntryTag: String, appleType: String?) {
            save.add(
                "Compare " + transformX(757f) + " " + transformY(922f) + " " + transformX(
                    1149f
                ) + " " + transformY(1041f) + " close_btn.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo $EntryTag")
            save.add("JumpTo \$AppleEnd")
            save.add("Tag $EntryTag")
            when (appleType) {
                "0" -> {
                    save.add("Wait 300000")
                    save.add("Click " + transformX(960f) + " " + transformY(987f))
                    save.add("JumpTo \$NotReadyToEnterStage")
                }
                "1" -> save.add("Click " + transformX(960f) + " " + transformY(330f))
                "2" -> save.add("Click " + transformX(960f) + " " + transformY(540f))
                "3" -> save.add("Click " + transformX(960f) + " " + transformY(750f))
                "4" -> save.add("Click " + transformX(960f) + " " + transformY(904f))
            }
            save.add("Wait 3000")
            save.add("Click " + transformX(1260f) + " " + transformY(900f))
            save.add("Wait 3000")
            save.add("Tag \$AppleEnd")
            save.add("Wait 1000")
        }

        private fun waitUntilClickEnter() {
            //TODO:: Compare before click
            save.add("Wait 3000")
            save.add("Click " + transformX(1785f) + " " + transformY(1077f)) //Start Operation
        }

        private fun preStage(block: Vector<String>) {
            //================Init======================
            save.add("Var \$Continue 0")
            save.add("Var \$Loop 1")
            //================Start=====================
            save.add("Tag \$Start")
            countDownAndExit("\$Loop", block[4])
            save.add("IfGreater \$Continue 0")
            save.add("JumpTo \$BeforeEnter")
            waitUntilMenuAndSelectStage()
            save.add("Tag \$BeforeEnter")
            checkFriendPage("\$ChooseFriend")
            checkApplePage("\$EatApple")
            save.add("JumpTo \$BeforeEnter")
            eatApple("\$EatApple", block[1])
            save.add("Tag \$WaitToSelectFriend")
            checkFriendPage("\$ChooseFriend")
            save.add("JumpTo \$WaitToSelectFriend")
            selectFriend("\$ChooseFriend", block[2], block[3])
            save.add("IfGreater \$Continue 0")
            save.add("JumpTo \$PreStageEnd")
            waitUntilClickEnter()
            save.add("Tag \$PreStageEnd")
            save.add("Var \$Continue 0")
        }

        private fun craftSkillAux(x: Float, servant: Float) {
            save.add("Click " + transformX(1798f) + " " + transformY(530f)) //Click Master Craft Skill
            save.add("Wait 500")
            save.add("Click " + transformX(x) + " " + transformY(530f)) //開技能
            save.add(
                "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                    908f
                ) + " " + transformY(766f) + " cancel_btn.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo \$CraftSkill$tag_count")
            save.add("Wait 300")
            save.add("Click " + transformX(servant) + " " + transformY(731f)) //從者
            save.add("JumpTo \$CraftSkillEnd$tag_count")
            save.add("Tag \$CraftSkill$tag_count")
            save.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
            save.add("JumpTo \$CraftSkillEnd$tag_count")
            save.add("Tag \$CraftSkillEnd$tag_count")
            save.add("Wait 2500")
            tag_count++
        }

        private fun craftSkillChangeAux(servant1: Float, servant2: Float) {
            save.add("Click " + transformX(1798f) + " " + transformY(530f)) //御主技能
            save.add("Wait 500")
            save.add("Click " + transformX(1622f) + " " + transformY(530f)) //開技能
            save.add("Wait 500")
            save.add(
                "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                    908f
                ) + " " + transformY(766f) + " cancel_btn.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo \$CraftSkill$tag_count")
            save.add("Click " + transformX(servant1) + " " + transformY(590f)) //Target 1
            save.add("Click " + transformX(servant2) + " " + transformY(590f)) //Target 2
            save.add("Click " + transformX(950f) + " " + transformY(1000f))
            save.add("JumpTo \$CraftSkillEnd$tag_count")
            save.add("Tag \$CraftSkill$tag_count")
            save.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
            save.add("Wait 500")
            save.add("Click " + transformX(1798f) + " " + transformY(530f)) //御主技能
            save.add("JumpTo \$CraftSkillEnd$tag_count")
            save.add("Tag \$CraftSkillEnd$tag_count")
            save.add("Wait 8000")
            tag_count++
        }

        private val master_craft_x_coordinate = intArrayOf(-1, 1366, 1490, 1616)
        private fun craftSkill(block: Vector<String>) {
            var x: Float
            for (j in 1..3) {
                x = master_craft_x_coordinate[j].toFloat()
                when (block[j]) {
                    "0" -> {}
                    "1" -> {
                        save.add("Click " + transformX(1798f) + " " + transformY(530f)) //御主技能
                        save.add("Wait 1000")
                        save.add("Click " + transformX(x) + " " + transformY(530f)) //開技能
                        save.add(
                            "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                                908f
                            ) + " " + transformY(766f) + " cancel_btn.png"
                        )
                        save.add("IfGreater \$R 5")
                        save.add("JumpTo \$CraftSkill$tag_count")
                        save.add("JumpTo \$CraftSkillEnd$tag_count")
                        save.add("Tag \$CraftSkill$tag_count")
                        save.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
                        save.add("JumpTo \$CraftSkillEnd$tag_count")
                        save.add("Tag \$CraftSkillEnd$tag_count")
                        save.add("Wait 2500")
                        tag_count++
                    }
                    "2" -> craftSkillAux(x, 507f)
                    "3" -> craftSkillAux(x, 957f)
                    "4" -> craftSkillAux(x, 1434f)
                }
            }
            when (block[4]) {
                "0" -> {}
                "1" -> craftSkillChangeAux(210f, 1120f)
                "2" -> craftSkillChangeAux(210f, 1414f)
                "3" -> craftSkillChangeAux(210f, 1700f)
                "4" -> craftSkillChangeAux(507f, 1120f)
                "5" -> craftSkillChangeAux(507f, 1414f)
                "6" -> craftSkillChangeAux(507f, 1700f)
                "7" -> craftSkillChangeAux(810f, 1120f)
                "8" -> craftSkillChangeAux(810f, 1414f)
                "9" -> craftSkillChangeAux(810f, 1700f)
            }
        }

        private fun skillAux(x: Float, servant: Float) {
            save.add("Click " + transformX(x) + " " + transformY(930f)) //開技能
            save.add("Wait 500")
            save.add(
                "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                    908f
                ) + " " + transformY(766f) + " cancel_btn.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo \$Skill$tag_count")
            save.add("Click " + transformX(servant) + " " + transformY(731f)) //從者一
            save.add("JumpTo \$SkillEnd$tag_count")
            save.add("Tag \$Skill$tag_count")
            save.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
            save.add("JumpTo \$SkillEnd$tag_count")
            save.add("Tag \$SkillEnd$tag_count")
            save.add("Wait 2500")
            tag_count++
        }

        private val skill_x_coordinate =
            intArrayOf(-1, 114, 241, 380, 581, 718, 862, 1055, 1200, 1333)

        private fun skill(block: Vector<String>) {
            var x: Float
            for (j in 1..9) {
                x = skill_x_coordinate[j].toFloat()
                when (block[j]) {
                    "0" -> {}
                    "1" -> {
                        save.add("Click " + transformX(x) + " " + transformY(930f)) //開技能
                        save.add("Wait 500")
                        save.add(
                            "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                                908f
                            ) + " " + transformY(766f) + " cancel_btn.png"
                        )
                        save.add("IfGreater \$R 5")
                        save.add("JumpTo \$Skill$tag_count")
                        save.add("JumpTo \$SkillEnd$tag_count")
                        save.add("Tag \$Skill$tag_count")
                        save.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
                        save.add("JumpTo \$SkillEnd$tag_count")
                        save.add("Tag \$SkillEnd$tag_count")
                        save.add("Wait 2500")
                        tag_count++
                    }
                    "2" -> skillAux(x, 507f)
                    "3" -> skillAux(x, 957f)
                    "4" -> skillAux(x, 1434f)
                }
            }
        }

        private val noble_phantasms_x_coordinate = intArrayOf(-1, 611, 972, 1330)
        private fun noblePhantasms(block: Vector<String>) {
            save.add("Click " + transformX(1694f) + " " + transformY(969f)) //Attack
            save.add("Wait 2300")
            for (j in 1..3) {
                if (block[j] == "1") {
                    save.add(
                        "Click " + transformX(
                            noble_phantasms_x_coordinate[j].toFloat()
                        ) + " " + transformY(364f)
                    ) //Noble Phantasms
                }
            }
            save.add("Click " + transformX(190f) + " " + transformY(835f)) //Command Card
            save.add("Click " + transformX(611f) + " " + transformY(835f)) //Command Card
            save.add("Click " + transformX(1032f) + " " + transformY(835f)) //Command Card
            save.add("Click " + transformX(1453f) + " " + transformY(835f)) //Command Card
            save.add("Click " + transformX(1874f) + " " + transformY(835f)) //Command Card
            save.add("Wait 8000")
        }

        private val ClickAll = Vector<String>()
        private fun end() {
            save.add("Wait 3000")
            save.add("Tag \$EndStageAgain")
            save.add(
                "Compare " + transformX(1556f) + " " + transformY(1031f) + " " + transformX(
                    1777f
                ) + " " + transformY(1116f) + " end.png"
            )
            save.add("IfGreater \$R 5")
            save.add("JumpTo \$EndStage")
            save.add(
                "Compare " + transformX(1560f) + " " + transformY(830f) + " " + transformX(
                    1843f
                ) + " " + transformY(1109f) + " attack.png"
            )
            save.add("IfGreater \$R 30")
            save.add("JumpTo \$EndStageBattle")
            safeClick()
            save.add("Wait 2000")
            save.add("JumpTo \$EndStageAgain")
            save.add("Tag \$EndStageBattle")
            noblePhantasms(ClickAll)
            save.add("JumpTo \$EndStageAgain")
            save.add("Tag \$EndStage")
            save.add("Wait 1000")
            save.add("Click " + transformX(1658f) + " " + transformY(1073f))
            save.add("Wait 1000")
            save.add("Click " + transformX(1658f) + " " + transformY(1073f))
            save.add("Tag \$Ending")
            //Check is menu
            save.add(
                "Compare " + transformX(1665f) + " " + transformY(1039f) + " " + transformX(
                    1910f
                ) + " " + transformY(1130f) + " menu.png"
            )
            save.add("IfGreater \$R 15")
            save.add("JumpTo \$Start")

            //Check Continue
            save.add(
                "Compare " + transformX(1046f) + " " + transformY(852f) + " " + transformX(
                    1472f
                ) + " " + transformY(975f) + " contdbtn.png"
            )
            save.add("IfSmaller \$R 15")
            save.add("JumpTo \$SafeClick")
            save.add("Click " + transformX(1211f) + " " + transformY(910f))
            save.add("Var \$Continue 1")
            save.add("JumpTo \$Start")
            save.add("Tag \$SafeClick")
            save.add("Wait 1000")
            safeClick()
            save.add("JumpTo \$Ending")
        }

        private fun waitUntilAttackButton() {
            save.add("Tag \$StillBattleAgain$tag_count")
            save.add(
                "Compare " + transformX(1560f) + " " + transformY(830f) + " " + transformX(
                    1843f
                ) + " " + transformY(1109f) + " attack.png"
            )
            save.add("IfGreater \$R 30")
            save.add("JumpTo \$StillBattle$tag_count")
            save.add("Wait 1000")
            safeClick()
            save.add("JumpTo \$StillBattleAgain$tag_count")
            save.add("Tag \$StillBattle$tag_count")
            save.add("Wait 1000")
            tag_count++
        }

        //====================================================================-
        private fun setUpScreenPara(config: Vector<String>) {
            if (config[5] == "0") {
                user_size = Size(
                    max(ScreenShotService.height, ScreenShotService.width),
                    min(ScreenShotService.height, ScreenShotService.width)
                )
                user_offset = if (user_size.width * 9 >= user_size.height * 16) {
                    Point(
                        user_size.width / 2.0 - user_size.height * 8.0 / 9, 0.0
                    )
                } else {
                    Point(0.0, user_size.height / 2.0 - user_size.width * 9.0 / 32)
                }
            } else {
                user_offset = Point(
                    config[6]!!.toInt().toDouble(), config[7]!!.toInt().toDouble()
                )
                user_size = Size(
                    config[8]!!.toInt() - config[6]!!.toInt(),
                    config[9]!!.toInt() - config[7]!!.toInt()
                )
            }
            ratio = min(
                user_size.width / dev_size.width.toDouble(),
                user_size.height / dev_size.height.toDouble()
            )
        }

        private fun transformX(x: Float): String {
            return (ratio * (x - dev_offset.x) + user_offset.x).toInt().toString()
        }

        private fun transformY(y: Float): String {
            return (ratio * (y - dev_offset.y) + user_offset.y).toInt().toString()
        }

        private fun safeClick() {
            save.add("Click " + transformX(1278f) + " " + transformY(85f))
        }

        init {
            ClickAll.add("")
            ClickAll.add("1")
            ClickAll.add("1")
            ClickAll.add("1")
        }
    }
}