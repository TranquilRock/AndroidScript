package com.example.androidscript.activities.fgo

import android.util.Size
import com.example.androidscript.services.ScreenShotService
import com.example.androidscript.util.FileOperation
import org.opencv.core.Point
import java.util.*
import kotlin.math.max
import kotlin.math.min

interface UICompiler {
    fun compile(data: Vector<Vector<String>>)

    class ImageLocation(
        private val upperLeft: Pair<Int, Int>,
        private val lowerRight: Pair<Int, Int>
    ) {
        override fun toString(): String {
            return "${upperLeft.first} ${upperLeft.second} ${lowerRight.first} ${lowerRight.second}"
        }
    }

    class PointLocation(
        private val p: Pair<Int, Int>
    ) {
        override fun toString(): String {
            return "${p.first} ${p.second}"
        }
    }
}

@Suppress("SameParameterValue") //For readability
interface FGOUICompiler2 : UICompiler {
    override fun compile(data: Vector<Vector<String>>) {
        fileContent = Vector()
        tagCount = 0
        setUpScreenPara(data[0])
        for (block in data) {
            when (block[0]) {
                "PreStage" -> {
                    fileContent.add("Log PreStage")
                    preStage(block)
                }

                "CraftSkill" -> {
                    fileContent.add("Log CraftSkill")
                    waitUntilAttackButton()
                    craftSkillBlock(block)
                }

                "Skill" -> {
                    fileContent.add("Log Skill")
                    waitUntilAttackButton()
                    skillBlock(block)
                }

                "NoblePhantasms" -> {
                    fileContent.add("Log NoblePhantasms")
                    waitUntilAttackButton()
                    noblePhantasms(block)
                }

                "End" -> {
                    fileContent.add("Log End")
                    end()
                }
            }
        }
        FileOperation.writeLines(FGOEditor.folderName + "Run.txt", fileContent)
    }

    companion object {
        var tagCount = 0
        private lateinit var fileContent: Vector<String>

        private val devSize = Size(1920, 1070)
        private val devOffset = Point(0.0, 64.0)
        private lateinit var userSize: Size
        private lateinit var userOffset: Point
        private var ratio = 0.0
        private fun transformX(x: Float): String {
            return (ratio * (x - devOffset.x) + userOffset.x).toInt().toString()
        }

        private fun transformY(y: Float): String {
            return (ratio * (y - devOffset.y) + userOffset.y).toInt().toString()
        }

        private fun transform(x: Float, y: Float): Pair<Int, Int> {
            return Pair(
                (ratio * (x - devOffset.x) + userOffset.x).toInt(),
                (ratio * (y - devOffset.y) + userOffset.y).toInt()
            )
        }

        // ========================= Locations ===============================
        private val servantLocations: Array<UICompiler.PointLocation> = arrayOf(
            UICompiler.PointLocation(transform(507f, 731f)),
            UICompiler.PointLocation(transform(957f, 731f)),
            UICompiler.PointLocation(transform(1434f, 731f))
        )

        private val skillButtonLocations: Array<UICompiler.PointLocation> = arrayOf(
            UICompiler.PointLocation(transform(-1f, -1f)),
            UICompiler.PointLocation(transform(114f, 930f)),
            UICompiler.PointLocation(transform(241f, 930f)),
            UICompiler.PointLocation(transform(380f, 930f)),
            UICompiler.PointLocation(transform(581f, 930f)),
            UICompiler.PointLocation(transform(718f, 930f)),
            UICompiler.PointLocation(transform(862f, 930f)),
            UICompiler.PointLocation(transform(1055f, 930f)),
            UICompiler.PointLocation(transform(1200f, 930f)),
            UICompiler.PointLocation(transform(1333f, 930f)),
        )

        private val craftListLocation = UICompiler.PointLocation(transform(1798f, 530f))
        private val craftButtonLocations: Array<UICompiler.PointLocation> = arrayOf(
            UICompiler.PointLocation(transform(-1f, -1f)),
            UICompiler.PointLocation(transform(1366f, 530f)),
            UICompiler.PointLocation(transform(1490f, 530f)),
            UICompiler.PointLocation(transform(1616f, 530f))
        )
        private val craftSwitchServantsLocations: Array<UICompiler.PointLocation> = arrayOf(
            UICompiler.PointLocation(transform(210f, 530f)),
            UICompiler.PointLocation(transform(507f, 530f)),
            UICompiler.PointLocation(transform(810f, 530f)),
            UICompiler.PointLocation(transform(1120f, 530f)),
            UICompiler.PointLocation(transform(1414f, 530f)),
            UICompiler.PointLocation(transform(1700f, 530f))
        )
        private val craftSwitchButtonLocation = UICompiler.PointLocation(transform(950f, 1000f))

        private val masterCraftXCoordinate = intArrayOf(-1, 1366, 1490, 1616)
        private val noblePhantasmsXCoordinate = intArrayOf(-1, 611, 972, 1330)

        private val ClickAll = Vector<String>()

        init {
            ClickAll.add("")
            ClickAll.add("1")
            ClickAll.add("1")
            ClickAll.add("1")
        }
    }

    private fun countDownAndExit(varName: String, n: String?) {
        fileContent.add("IfGreater $varName $n")
        fileContent.add("Exit")
        fileContent.add("Add $varName 1")
    }

    private fun waitUntilMenuAndSelectStage() {
        fileContent.add("Tag \$NotReadyToEnterStage")
        fileContent.add(
            "Compare " + transformX(1665f) + " " + transformY(1039f) + " " + transformX(
                1910f
            ) + " " + transformY(1130f) + " menu.png"
        )
        fileContent.add("IfGreater \$R 15")
        fileContent.add("JumpTo \$ReadyToEnterStage")
        fileContent.add("Wait 1000")
        fileContent.add("JumpTo \$NotReadyToEnterStage")
        fileContent.add("Tag \$ReadyToEnterStage")
        fileContent.add("Wait 1000")
        fileContent.add("Click " + transformX(1400f) + " " + transformY(320f)) //Select stage
    }

    private fun checkFriendPage(trueTag: String) {
        fileContent.add(
            "Compare " + transformX(1551f) + " " + transformY(67f) + " " + transformX(
                1917f
            ) + " " + transformY(154f) + " support_select.png"
        )
        fileContent.add("IfGreater \$R 15")
        fileContent.add("JumpTo $trueTag")
    }

    private fun checkApplePage(trueTag: String) {
        fileContent.add(
            "Compare " + transformX(757f) + " " + transformY(922f) + " " + transformX(
                1149f
            ) + " " + transformY(1041f) + " close_btn.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo $trueTag")
    }

    private fun selectFriend(EntryTag: String, Servant: String?, Craft: String?) {
        fileContent.add("Tag $EntryTag")
        val servantClass = Servant!!.split("_".toRegex()).toTypedArray()
        when (servantClass[servantClass.size - 1]) {
            "All" -> fileContent.add("Click " + transformX(148f) + " " + transformY(260f))
            "Saber" -> fileContent.add("Click " + transformX(248f) + " " + transformY(260f))
            "Archer" -> fileContent.add("Click " + transformX(348f) + " " + transformY(260f))
            "Lancer" -> fileContent.add("Click " + transformX(448f) + " " + transformY(260f))
            "Rider" -> fileContent.add("Click " + transformX(548f) + " " + transformY(260f))
            "Caster" -> fileContent.add("Click " + transformX(648f) + " " + transformY(260f))
            "Assassin" -> fileContent.add("Click " + transformX(748f) + " " + transformY(260f))
            "Berserker" -> fileContent.add("Click " + transformX(848f) + " " + transformY(260f))
            "Extra" -> fileContent.add("Click " + transformX(948f) + " " + transformY(260f))
            else -> fileContent.add("Click " + transformX(1048f) + " " + transformY(260f))
        }
        fileContent.add("Wait 500")
        fileContent.add("Tag \$FriendsDNE")
        fileContent.add("Var \$FriendsDown 0")
        fileContent.add("IfGreater \$FriendsDown 5")
        fileContent.add("JumpTo \$Refresh")
        fileContent.add("JumpTo \$RefreshSkip")
        fileContent.add("Tag \$Refresh")
        fileContent.add("Click " + transformX(1271f) + " " + transformY(254f))
        fileContent.add("Wait 2000")
        fileContent.add("Click " + transformX(1259f) + " " + transformY(905f))
        fileContent.add("Wait 2000")
        fileContent.add("Tag \$RefreshSkip")
        if ("None" != Servant) {
            fileContent.add(
                "Compare " + transformX(70f) + " " + transformY(340f) + " " + transformX(
                    330f
                ) + " " + transformY(624f) + " Friend/" + Servant + ".png"
            )
            fileContent.add("IfGreater \$R 50")
        }
        fileContent.add("JumpTo \$Friends")
        fileContent.add("Wait 2000")
        fileContent.add(
            "Swipe " + transformX(960f) + " " + transformY(937f) + " " + transformX(
                960f
            ) + " " + transformY(689f)
        )
        fileContent.add("JumpTo \$FriendsDNE")
        fileContent.add("Tag \$Friends")
        if ("None" != Craft) { //Servant Craft
            fileContent.add(
                "Compare " + transformX(70f) + " " + transformY(480f) + " " + transformX(
                    330f
                ) + " " + transformY(700f) + " Craft/" + Craft + ".png"
            )
            fileContent.add("IfGreater \$R 5")
            fileContent.add("JumpTo \$Craft")
            fileContent.add("Wait 2000")
            fileContent.add(
                "Swipe " + transformX(960f) + " " + transformY(937f) + " " + transformX(
                    960f
                ) + " " + transformY(689f)
            )
            fileContent.add("JumpTo \$FriendsDNE")
            fileContent.add("Tag \$Craft")
            fileContent.add("JumpTo \$CraftEnd")
            fileContent.add("Tag \$CraftEnd")
        }
        fileContent.add("Click " + transformX(800f) + " " + transformY(540f))
        fileContent.add("Tag \$FriendsEnd")
    }

    private fun eatApple(EntryTag: String, appleType: String?) {
        fileContent.add(
            "Compare " + transformX(757f) + " " + transformY(922f) + " " + transformX(
                1149f
            ) + " " + transformY(1041f) + " close_btn.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo $EntryTag")
        fileContent.add("JumpTo \$AppleEnd")
        fileContent.add("Tag $EntryTag")
        when (appleType) {
            "0" -> {
                fileContent.add("Wait 300000")
                fileContent.add("Click " + transformX(960f) + " " + transformY(987f))
                fileContent.add("JumpTo \$NotReadyToEnterStage")
            }

            "1" -> fileContent.add("Click " + transformX(960f) + " " + transformY(330f))
            "2" -> fileContent.add("Click " + transformX(960f) + " " + transformY(540f))
            "3" -> fileContent.add("Click " + transformX(960f) + " " + transformY(750f))
            "4" -> fileContent.add("Click " + transformX(960f) + " " + transformY(904f))
        }
        fileContent.add("Wait 3000")
        fileContent.add("Click " + transformX(1260f) + " " + transformY(900f))
        fileContent.add("Wait 3000")
        fileContent.add("Tag \$AppleEnd")
        fileContent.add("Wait 1000")
    }

    private fun waitUntilClickEnter() {
        //TODO:: Compare before click
        fileContent.add("Wait 3000")
        fileContent.add("Click " + transformX(1785f) + " " + transformY(1077f)) //Start Operation
    }

    private fun preStage(block: Vector<String>) {
        //================Init======================
        fileContent.add("Var \$Continue 0")
        fileContent.add("Var \$Loop 1")
        //================Start=====================
        fileContent.add("Tag \$Start")
        countDownAndExit("\$Loop", block[4])
        fileContent.add("IfGreater \$Continue 0")
        fileContent.add("JumpTo \$BeforeEnter")
        waitUntilMenuAndSelectStage()
        fileContent.add("Tag \$BeforeEnter")
        checkFriendPage("\$ChooseFriend")
        checkApplePage("\$EatApple")
        fileContent.add("JumpTo \$BeforeEnter")
        eatApple("\$EatApple", block[1])
        fileContent.add("Tag \$WaitToSelectFriend")
        checkFriendPage("\$ChooseFriend")
        fileContent.add("JumpTo \$WaitToSelectFriend")
        selectFriend("\$ChooseFriend", block[2], block[3])
        fileContent.add("IfGreater \$Continue 0")
        fileContent.add("JumpTo \$PreStageEnd")
        waitUntilClickEnter()
        fileContent.add("Tag \$PreStageEnd")
        fileContent.add("Var \$Continue 0")
    }

    private fun craftCastSkillWithOneTarget(
        btnLocation: UICompiler.PointLocation,
        targetLocation: UICompiler.PointLocation
    ) {
        fileContent.add("Click $craftListLocation")
        fileContent.add("Wait 500")
        fileContent.add("Click $btnLocation") //開技能
        fileContent.add(
            "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                908f
            ) + " " + transformY(766f) + " cancel_btn.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo \$CraftSkill$tagCount")
        fileContent.add("Wait 300")
        fileContent.add("Click $targetLocation")
        fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
        fileContent.add("Tag \$CraftSkill$tagCount")
        fileContent.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
        fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
        fileContent.add("Tag \$CraftSkillEnd$tagCount")
        fileContent.add("Wait 2500")
        tagCount++
    }

    private fun craftSwitchServants(
        targetLocation1: UICompiler.PointLocation,
        targetLocation2: UICompiler.PointLocation
    ) {
        fileContent.add("Click $craftListLocation")
        fileContent.add("Wait 500")
        fileContent.add("Click " + transformX(1622f) + " " + transformY(530f)) //開技能
        fileContent.add("Wait 500")
        fileContent.add(
            "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                908f
            ) + " " + transformY(766f) + " cancel_btn.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo \$CraftSkill$tagCount")
        fileContent.add("Click $targetLocation1")
        fileContent.add("Click $targetLocation2")
        fileContent.add("Click $craftSwitchButtonLocation")
        fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
        fileContent.add("Tag \$CraftSkill$tagCount")
        fileContent.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
        fileContent.add("Wait 500")
        fileContent.add("Click $craftListLocation")
        fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
        fileContent.add("Tag \$CraftSkillEnd$tagCount")
        fileContent.add("Wait 8000")
        tagCount++
    }

    private fun craftSkillBlock(block: Vector<String>) {
        for (j in 1..3) {
            val btnLocation = craftButtonLocations[j]
            when (block[j]) {
                "0" -> {} // Don't use.
                "1" -> { // Use without any target.
                    fileContent.add("Click $craftListLocation")
                    fileContent.add("Wait 1000")
                    fileContent.add("Click $btnLocation")
                    fileContent.add(
                        "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                            908f
                        ) + " " + transformY(766f) + " cancel_btn.png"
                    )
                    fileContent.add("IfGreater \$R 5")
                    fileContent.add("JumpTo \$CraftSkill$tagCount")
                    fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
                    fileContent.add("Tag \$CraftSkill$tagCount")
                    fileContent.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
                    fileContent.add("JumpTo \$CraftSkillEnd$tagCount")
                    fileContent.add("Tag \$CraftSkillEnd$tagCount")
                    fileContent.add("Wait 2500")
                    tagCount++
                }

                "2" -> craftCastSkillWithOneTarget(btnLocation, servantLocations[0])
                "3" -> craftCastSkillWithOneTarget(btnLocation, servantLocations[1])
                "4" -> craftCastSkillWithOneTarget(btnLocation, servantLocations[2])
            }
        }
        when (block[4]) {
            "0" -> {} // Don't switch.
            "1" -> craftSwitchServants(
                craftSwitchServantsLocations[0],
                craftSwitchServantsLocations[3]
            )

            "2" -> craftSwitchServants(
                craftSwitchServantsLocations[0],
                craftSwitchServantsLocations[4]
            )

            "3" -> craftSwitchServants(
                craftSwitchServantsLocations[0],
                craftSwitchServantsLocations[5]
            )

            "4" -> craftSwitchServants(
                craftSwitchServantsLocations[1],
                craftSwitchServantsLocations[3]
            )

            "5" -> craftSwitchServants(
                craftSwitchServantsLocations[1],
                craftSwitchServantsLocations[4]
            )

            "6" -> craftSwitchServants(
                craftSwitchServantsLocations[1],
                craftSwitchServantsLocations[5]
            )

            "7" -> craftSwitchServants(
                craftSwitchServantsLocations[2],
                craftSwitchServantsLocations[3]
            )

            "8" -> craftSwitchServants(
                craftSwitchServantsLocations[2],
                craftSwitchServantsLocations[4]
            )

            "9" -> craftSwitchServants(
                craftSwitchServantsLocations[2],
                craftSwitchServantsLocations[5]
            )
        }
    }

    private fun servantCastSkillWithOneTarget(
        btnLocation: UICompiler.PointLocation,
        targetLocation: UICompiler.PointLocation
    ) {
        fileContent.add("Click $btnLocation")
        fileContent.add("Wait 500")
        fileContent.add(
            "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                908f
            ) + " " + transformY(766f) + " cancel_btn.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo \$Skill$tagCount")
        fileContent.add("Click $targetLocation")
        fileContent.add("JumpTo \$SkillEnd$tagCount")
        fileContent.add("Tag \$Skill$tagCount")
        fileContent.add("Click " + transformX(645f) + " " + transformY(696f)) //取消BUG
        fileContent.add("JumpTo \$SkillEnd$tagCount")
        fileContent.add("Tag \$SkillEnd$tagCount")
        fileContent.add("Wait 2500")
        tagCount++
    }

    private fun skillBlock(block: Vector<String>) {
        for (j in 1..9) {
            val btnLocation = skillButtonLocations[j]
            when (block[j]) {
                "0" -> {} // Don't use skill.
                "1" -> { // Craft skill without a target.
                    fileContent.add("Click $btnLocation")
                    fileContent.add("Wait 500")
                    fileContent.add(
                        "Compare " + transformX(382f) + " " + transformY(626f) + " " + transformX(
                            908f
                        ) + " " + transformY(766f) + " cancel_btn.png"
                    )
                    fileContent.add("IfGreater \$R 5")
                    fileContent.add("JumpTo \$Skill$tagCount")
                    fileContent.add("JumpTo \$SkillEnd$tagCount")
                    fileContent.add("Tag \$Skill$tagCount")
                    // Click to cancel, in case the skill is a targeting skill.
                    fileContent.add("Click " + transformX(645f) + " " + transformY(696f))
                    fileContent.add("JumpTo \$SkillEnd$tagCount")
                    fileContent.add("Tag \$SkillEnd$tagCount")
                    fileContent.add("Wait 2500")
                    tagCount++
                }

                "2" -> servantCastSkillWithOneTarget(btnLocation, servantLocations[0])
                "3" -> servantCastSkillWithOneTarget(btnLocation, servantLocations[1])
                "4" -> servantCastSkillWithOneTarget(btnLocation, servantLocations[2])
            }
        }
    }

    private fun noblePhantasms(block: Vector<String>) {
        fileContent.add("Click " + transformX(1694f) + " " + transformY(969f)) //Attack
        fileContent.add("Wait 2300")
        for (j in 1..3) {
            if (block[j] == "1") {
                fileContent.add(
                    "Click " + transformX(
                        noblePhantasmsXCoordinate[j].toFloat()
                    ) + " " + transformY(364f)
                ) //Noble Phantasms
            }
        }
        fileContent.add("Click " + transformX(190f) + " " + transformY(835f)) //Command Card
        fileContent.add("Click " + transformX(611f) + " " + transformY(835f)) //Command Card
        fileContent.add("Click " + transformX(1032f) + " " + transformY(835f)) //Command Card
        fileContent.add("Click " + transformX(1453f) + " " + transformY(835f)) //Command Card
        fileContent.add("Click " + transformX(1874f) + " " + transformY(835f)) //Command Card
        fileContent.add("Wait 8000")
    }

    private fun end() {
        fileContent.add("Wait 3000")
        fileContent.add("Tag \$EndStageAgain")
        fileContent.add(
            "Compare " + transformX(1556f) + " " + transformY(1031f) + " " + transformX(
                1777f
            ) + " " + transformY(1116f) + " end.png"
        )
        fileContent.add("IfGreater \$R 5")
        fileContent.add("JumpTo \$EndStage")
        fileContent.add(
            "Compare " + transformX(1560f) + " " + transformY(830f) + " " + transformX(
                1843f
            ) + " " + transformY(1109f) + " attack.png"
        )
        fileContent.add("IfGreater \$R 30")
        fileContent.add("JumpTo \$EndStageBattle")
        safeClick()
        fileContent.add("Wait 2000")
        fileContent.add("JumpTo \$EndStageAgain")
        fileContent.add("Tag \$EndStageBattle")
        noblePhantasms(ClickAll)
        fileContent.add("JumpTo \$EndStageAgain")
        fileContent.add("Tag \$EndStage")
        fileContent.add("Wait 1000")
        fileContent.add("Click " + transformX(1658f) + " " + transformY(1073f))
        fileContent.add("Wait 1000")
        fileContent.add("Click " + transformX(1658f) + " " + transformY(1073f))
        fileContent.add("Tag \$Ending")
        //Check is menu
        fileContent.add(
            "Compare " + transformX(1665f) + " " + transformY(1039f) + " " + transformX(
                1910f
            ) + " " + transformY(1130f) + " menu.png"
        )
        fileContent.add("IfGreater \$R 15")
        fileContent.add("JumpTo \$Start")

        //Check Continue
        fileContent.add(
            "Compare " + transformX(1046f) + " " + transformY(852f) + " " + transformX(
                1472f
            ) + " " + transformY(975f) + " contdbtn.png"
        )
        fileContent.add("IfSmaller \$R 15")
        fileContent.add("JumpTo \$SafeClick")
        fileContent.add("Click " + transformX(1211f) + " " + transformY(910f))
        fileContent.add("Var \$Continue 1")
        fileContent.add("JumpTo \$Start")
        fileContent.add("Tag \$SafeClick")
        fileContent.add("Wait 1000")
        fileContent.add("JumpTo \$Ending")
    }

    private fun waitUntilAttackButton() {
        fileContent.add("Tag \$StillBattleAgain$tagCount")
        fileContent.add(
            "Compare " + transformX(1560f) + " " + transformY(830f) + " " + transformX(
                1843f
            ) + " " + transformY(1109f) + " attack.png"
        )
        fileContent.add("IfGreater \$R 30")
        fileContent.add("JumpTo \$StillBattle$tagCount")
        fileContent.add("Wait 1000")
        safeClick()
        fileContent.add("JumpTo \$StillBattleAgain$tagCount")
        fileContent.add("Tag \$StillBattle$tagCount")
        fileContent.add("Wait 1000")
        tagCount++
    }

    //====================================================================-
    private fun setUpScreenPara(config: Vector<String>) {
        if (config[5] == "0") {
            userSize = Size(
                max(ScreenShotService.height, ScreenShotService.width),
                min(ScreenShotService.height, ScreenShotService.width)
            )
            userOffset = if (userSize.width * 9 >= userSize.height * 16) {
                Point(
                    userSize.width / 2.0 - userSize.height * 8.0 / 9, 0.0
                )
            } else {
                Point(0.0, userSize.height / 2.0 - userSize.width * 9.0 / 32)
            }
        } else {
            userOffset = Point(
                config[6]!!.toInt().toDouble(), config[7]!!.toInt().toDouble()
            )
            userSize = Size(
                config[8]!!.toInt() - config[6]!!.toInt(),
                config[9]!!.toInt() - config[7]!!.toInt()
            )
        }
        ratio = min(
            userSize.width / devSize.width.toDouble(),
            userSize.height / devSize.height.toDouble()
        )
    }


    private fun safeClick() {
        fileContent.add("Click " + transformX(1278f) + " " + transformY(85f))
    }

}