package com.example.androidscript.floatingwidget

import android.graphics.Bitmap
import android.util.Log
import com.example.androidscript.util.*
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList

open class Interpreter(
    private val ScriptFolderName: String,
    private val ScriptName: String,
    private val board: FloatingWidgetService.Bulletin
) : Thread() {

    init {
        interpret(ScriptName)
    }

    var runningFlag = false

    private var scriptCode: MutableMap<String, Code> = HashMap()

    private fun readCode(fileName: String): Vector<String> {
        return FileOperation.readFromFileLines(ScriptFolderName + fileName)
    }

    private fun readImg(fileName: String): Bitmap {
        return FileOperation.readPicAsBitmap(ScriptFolderName + fileName)
    }

    private fun interpret(fileName: String) {
        DebugMessage.set("Interpreting:$fileName")
        val command = readCode(fileName)
        try {
            scriptCode[fileName] = Code(SUPPORTED_COMMAND, command)
        } catch (e: Exception) {
            DebugMessage.set("Bug in $fileName")
            DebugMessage.printStackTrace(e)
        }
        for (depend in scriptCode[fileName]!!.dependency) {
            if (!scriptCode.containsKey(depend)) {
                interpret(depend)
            }
        }
    }

    //==========================================
    private lateinit var runArgs: ArrayList<String>

    fun runCode(argv: ArrayList<String>) {
        runArgs = argv
        runningFlag = true
        start()
    }

    override fun run() {
        board.announce("Running")
        execute(ScriptName, runArgs, 0)
        runningFlag = false
        board.announce("IDLE")
    }

    private fun execute(
        fileName: String,
        args: ArrayList<String>?,
        depth: Int,
    ): Int { //Run code that is already read in MyCode

        val localVar: MutableMap<String, String> = HashMap()
        parseArguments(localVar, args)
        DebugMessage.set("Running $fileName")
        val codeLength = scriptCode[fileName]!!.codes.size
        for (commandIndex in 0 until codeLength) {
            val command = scriptCode[fileName]!!.codes[commandIndex]
            if (command[0] == "Tag") {
                localVar[command[1]] = commandIndex.toString()
            }
        }
        var commandIndex = 0
        while (commandIndex < codeLength && runningFlag) {
            val command = scriptCode[fileName]!!.codes[commandIndex]
            val arguments = command.copyOfRange(1, command.size - 1)
            varsSubstitution(localVar, command[0], arguments)
            //=====================================================
            DebugMessage.set("$commandIndex  ${arguments.joinToString(" ")}")
            delay()
            when (command[0]) {
                "Exit" -> {
                    runningFlag = false
                    return 1
                }
                "Log" -> board.announce(arguments[0])
                "JumpTo" -> commandIndex = arguments[0].toInt() - 1 //One-based
                "Wait" -> sleep(
                    arguments[0].toInt()
                )
                "Call" -> localVar["\$R"] = execute(arguments[0], null, depth + 1).toString()
                "Tag" -> {}
                "Return" -> return arguments[0].toInt()
                "ClickPic" -> {
                    val tmp = readImg(arguments[0])
                    val target = ImageHandler.findLocation(
                        ScreenShotService.shot(), tmp, arguments[1]
                            .toDouble()
                    )
                    if (target != null) {
                        DebugMessage.set("Clicking Picture:" + target.x + " " + target.y)
                        AutoClickService.click(target.x.toInt(), target.y.toInt())
                        localVar["\$R"] = "0"
                    } else {
                        localVar["\$R"] = "1"
                    }
                }
                "Click" -> {
                    AutoClickService.click(arguments[0].toInt(), arguments[1].toInt())
                }
                "CallArg" -> {
                    val nextArgv =
                        arguments.copyOfRange(1, command.size - 2).toCollection(ArrayList())
                    localVar["\$R"] = execute(arguments[0], nextArgv, depth + 1).toString()
                }
                "IfGreater" -> if (arguments[0].toInt() <= arguments[1].toInt()) { //Failed, skip next line
                    commandIndex++
                }
                "IfSmaller" -> if (arguments[0].toInt() >= arguments[1].toInt()) { //Failed, skip next line
                    commandIndex++
                }
                "Add" -> localVar[arguments[0]] = (localVar[arguments[0]]!!
                    .toInt() + arguments[1].toInt()).toString()
                "Subtract" -> localVar[arguments[0]] = (localVar[arguments[0]]!!
                    .toInt() - arguments[1].toInt()).toString()
                "Var" -> localVar[arguments[0]] = arguments[1]
                "Check" -> if (ImageHandler.checkColor(
                        ScreenShotService.shot(), arguments[0]
                            .toInt(), arguments[1].toInt(), arguments[2].toInt()
                    )
                ) {
                    localVar["\$R"] = "0"
                } else {
                    localVar["\$R"] = "1"
                }
                "Swipe" -> {
                    delay()
                    AutoClickService.swipe(
                        arguments[0].toInt(),
                        arguments[1].toInt(),
                        arguments[2].toInt(),
                        arguments[3].toInt(),
                    )
                }
                "Compare" -> {
                    ScreenShotService.shot() //Empty shot to make sure Image be the newest.
                    localVar["\$R"] = ScreenShotService.compare(
                        readImg(arguments[4]), arguments[0]
                            .toInt(), arguments[1].toInt(), arguments[2].toInt(), arguments[3]
                            .toInt()
                    ).toString() // similarity
                }
                else -> throw RuntimeException("Cannot Recognize " + command[0])
            }
            commandIndex++
        }
        return 0
    }

    companion object {
        // Every child only need to specify where and how to fetch files, as well as what kind of commands are accepted;
        // private const val strFormat = "([A-Za-z0-9_-]*)"
        private const val imgFormat = "([A-Za-z0-9_/-]*).(jpg|png)"
        private const val scriptFormat = "([A-Za-z0-9_-]*).txt"
        private const val varFormat = "\\$([A-Za-z0-9_-]*)"
        private const val intFormat = "[0-9-]*"
        private const val floatFormat = "[0-9.]*"
        private const val intVarFormat = "($intFormat||$varFormat)"
        private const val floatVarFormat = "($floatFormat||$varFormat)"
        private const val imgVarFormat = "($imgFormat||$varFormat)"
        private const val intVarFloatFormat = "($intFormat||$varFormat||$floatFormat)"
        private const val anyFormat = "[a-zA-Z.0-9 $]*"
        val SUPPORTED_COMMAND = arrayOf(
            "Exit",
            "Log $anyFormat",
            "JumpTo $intVarFormat",
            "Wait $intVarFormat",
            "Call $scriptFormat",
            "Tag $varFormat",
            "Return $intVarFormat",
            "ClickPic $imgVarFormat $floatVarFormat",
            "Click $intVarFormat $intVarFormat",
            "CallArg $scriptFormat $anyFormat",
            "IfGreater $intVarFormat $intVarFormat",
            "IfSmaller $intVarFormat $intVarFormat",
            "Add $varFormat $intVarFormat",
            "Subtract $varFormat $intVarFormat",
            "Var $varFormat $intVarFloatFormat",
            "Check $intVarFormat $intVarFormat $intFormat",
            "Swipe $intVarFormat $intVarFormat $intVarFormat $intVarFormat",
            "Compare $intVarFormat $intVarFormat $intVarFormat $intVarFormat $imgVarFormat"
        )

        //==================== Helper =======================
        protected fun varsSubstitution(
            localVar: MutableMap<String, String>,
            command: String,
            args: Array<String>
        ) {
            if (args.isNotEmpty() && args[0][0] == '$' //Exit Has No Arg
                && command != "Var"
                && command != "Subtract"
                && command != "Add"
            ) {
                args[0] = localVar[args[0]]!!
            }
            for (z in 1 until args.size) {
                if (args[z][0] == '$') { //There might be Var command, that should replace $V
                    args[z] = localVar[args[z]]!!
                }
            }
        }

        protected fun parseArguments(
            LocalVar: MutableMap<String, String>,
            argv: ArrayList<String>?
        ) {
            LocalVar["\$R"] = "0"
            var argCount = 1
            argv?.run {
                for (arg in this) {
                    LocalVar["$$argCount"] = arg
                    argCount++
                }
            }
        }

        protected fun sleep(ms: Int) {
            sleep(ms.toLong())
        }

        protected fun delay() {
            sleep(100)
        }
    }

    class InvalidCodeException(s: String) : Exception() {
        init {
            Log.d("InvalidCodeException", s)
        }
    }
}