@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.tranquilrock.androidscript.core

/**
 * Lists all fundamental commands here.
 *
 * Suppress Unused and Visibility for future use.
 */
object Command {
    const val EXIT = "Exit"
    const val IF_EXIST = "IfExist"
    const val LOG = "Log"
    const val JUMP_TO = "JumpTo"
    const val WAIT = "Wait"
    const val CALL = "Call"
    const val TAG = "Tag"
    const val RETURN = "Return"
    const val CLICK_PIC = "ClickPic"
    const val CLICK = "Click"
    const val CALL_ARG = "CallArg"
    const val IF_GREATER = "IfGreater"
    const val IF_SMALLER = "IfSmaller"
    const val IF_EQUAL = "IfEqual"
    const val ADD = "Add"
    const val SUBTRACT = "Subtract"
    const val VAR = "Var"
    const val CHECK = "Check"
    const val SWIPE = "Swipe"
    const val COMPARE = "Compare"

    private const val strFormat = "([A-Za-z0-9_-]*)"
    private const val imgFormat = strFormat // "$strFormat.(jpg|png)"
    private const val scriptFormat = strFormat // "$strFormat.txt"
    private const val varFormat = "\\$$strFormat"
    private const val intFormat = "[0-9-]*"
    private const val floatFormat = "[0-9.]*"
    private const val intVarFormat = "($intFormat||$varFormat)"
    private const val floatVarFormat = "($floatFormat||$varFormat)"
    private const val imgVarFormat = "($imgFormat||$varFormat)"
    private const val intVarFloatFormat = "($intFormat||$varFormat||$floatFormat)"
    private const val anyFormat = "[a-zA-Z.0-9 $]*"

    val COMMAND_LIST = arrayOf(
        EXIT,
        IF_EXIST,
        LOG,
        JUMP_TO,
        WAIT,
        CALL,
        TAG,
        RETURN,
        CLICK_PIC,
        CLICK,
        CALL_ARG,
        IF_GREATER,
        IF_SMALLER,
        IF_EQUAL,
        ADD,
        SUBTRACT,
        VAR,
        CHECK,
        SWIPE,
        COMPARE,
    )

    val ASSIGN_COMMAND = arrayOf(
        ADD,
        SUBTRACT,
        VAR,
    )

    val COMMAND_FORMATS = arrayOf(
        EXIT,
        "$IF_EXIST $imgFormat",
        "$LOG $anyFormat",
        "$JUMP_TO $intVarFormat",
        "$WAIT $intVarFormat",
        "$CALL $scriptFormat",
        "$TAG $varFormat",
        "$RETURN $intVarFormat",
        "$CLICK_PIC $imgVarFormat $floatVarFormat",
        "$CLICK $intVarFormat $intVarFormat",
        "$CALL_ARG $scriptFormat $anyFormat",
        "$IF_GREATER $intVarFormat $intVarFormat",
        "$IF_SMALLER $intVarFormat $intVarFormat",
        "$IF_EQUAL $intVarFormat $intVarFormat",
        "$ADD $varFormat $intVarFormat",
        "$SUBTRACT $varFormat $intVarFormat",
        "$VAR $varFormat $intVarFloatFormat",
        "$CHECK $intVarFormat $intVarFormat $intFormat",
        "$SWIPE $intVarFormat $intVarFormat $intVarFormat $intVarFormat",
        "$COMPARE $intVarFormat $intVarFormat $intVarFormat $intVarFormat $imgVarFormat"
    )

    /** CALL & CALL_ARG are not exposed. */
    val BASIC_META = listOf(
        Pair(
            EXIT, emptyList()
        ),
        Pair(
            IF_EXIST, listOf(listOf("EditText", "Image"))
        ),
        Pair(
            LOG, listOf(listOf("EditText", "Info"))
        ),
        Pair(
            JUMP_TO, listOf(listOf("EditText", "LineNum"))
        ),
        Pair(
            WAIT, listOf(listOf("EditText", "Millis"))
        ),
        Pair(
            TAG, listOf(listOf("EditText", "\$Name"))
        ),
        Pair(
            RETURN, listOf(listOf("EditText", "Value"))
        ),
        Pair(
            CLICK_PIC, listOf(listOf("EditText", "Image"), listOf("EditText", "Ratio"))
        ),
        Pair(
            CLICK, listOf(listOf("EditText", "x"), listOf("EditText", "y"))
        ),
        Pair(
            IF_GREATER, listOf(listOf("EditText", "var1"), listOf("EditText", "var2"))
        ),
        Pair(
            IF_SMALLER, listOf(listOf("EditText", "var1"), listOf("EditText", "var2"))
        ),
        Pair(
            IF_EQUAL, listOf(listOf("EditText", "var1"), listOf("EditText", "var2"))
        ),
        Pair(
            ADD, listOf(listOf("EditText", "\$Name"), listOf("EditText", "Value"))
        ),
        Pair(
            SUBTRACT, listOf(listOf("EditText", "\$Name"), listOf("EditText", "Value"))
        ),
        Pair(
            VAR, listOf(listOf("EditText", "\$Name"), listOf("EditText", "value"))
        ),
        Pair(
            CHECK, listOf(
                listOf("EditText", "x"),
                listOf("EditText", "y"),
                listOf("EditText", "Color"),
            )
        ),
        Pair(
            SWIPE, listOf(
                listOf("EditText", "x1"),
                listOf("EditText", "y1"),
                listOf("EditText", "x2"),
                listOf("EditText", "y2"),
            )
        ),
        Pair(
            COMPARE, listOf(
                listOf("EditText", "x1"),
                listOf("EditText", "y1"),
                listOf("EditText", "x2"),
                listOf("EditText", "y2"),
                listOf("EditText", "Image"),
            )
        ),
    )

    private val L0_COMMAND_LIST = listOf(EXIT)
    private val L1_COMMAND_LIST = listOf(IF_EXIST, LOG, JUMP_TO, WAIT, CALL, TAG, RETURN)
    private val L2_COMMAND_LIST = listOf(
        CLICK_PIC, CLICK, CALL_ARG, IF_GREATER, IF_SMALLER, IF_EQUAL, ADD, SUBTRACT, VAR
    )
    private val L3_COMMAND_LIST = listOf(CHECK)
    private val L4_COMMAND_LIST = listOf(SWIPE)
    private val L5_COMMAND_LIST = listOf(COMPARE)

    val ALL_COMMAND_LIST =
        L0_COMMAND_LIST + L1_COMMAND_LIST + L2_COMMAND_LIST + L3_COMMAND_LIST + L4_COMMAND_LIST + L5_COMMAND_LIST

    fun getCommandLength(Command: String): Int {
        return when (Command) {
            in L0_COMMAND_LIST -> 0
            in L1_COMMAND_LIST -> 1
            in L2_COMMAND_LIST -> 2
            in L3_COMMAND_LIST -> 3
            in L4_COMMAND_LIST -> 4
            in L5_COMMAND_LIST -> 5
            else -> throw RuntimeException("Unrecognized command!")
        }
    }
}