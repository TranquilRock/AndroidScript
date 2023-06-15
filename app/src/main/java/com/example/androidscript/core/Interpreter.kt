package com.example.androidscript.core

import android.graphics.Bitmap
import android.util.Log
import com.example.androidscript.services.ClickService
import com.example.androidscript.services.WidgetService
import com.example.androidscript.services.ProjectionService
import com.example.androidscript.util.FileOperation
import com.example.androidscript.util.ImageHandler
import java.lang.Thread.sleep
import java.util.*

/** Every child needs to specify where and how to fetch files, as well as what kind of commands
 *  are accepted. */
open class Interpreter(
    private val ScriptFolderName: String,
    private val ScriptName: String,
    private val board: WidgetService.Bulletin
) : Runnable {

    private var scriptCode: HashMap<String, Code> = HashMap()
    var runningFlag = false

    init {
        interpret(ScriptName)
    }

    private fun readCode(fileName: String): Vector<String> {
        return FileOperation.readFromFileLines(ScriptFolderName + fileName)
    }

    private fun readImg(fileName: String): Bitmap {
        return FileOperation.readPicAsBitmap(ScriptFolderName + fileName)
    }

    private fun interpret(fileName: String) {
        val code = Code(readCode(fileName))

        val tagVar: HashMap<String, String> = HashMap()

        for (i in 0 until code.codes.size) {
            val command = code.codes[i]
            if (command[0] == "Tag") {
                tagVar[command[1]] = i.toString()
            }
        }

        for (line in code.codes) {
            for (i in 1 until line.size) { // The first word must be tag
                if (tagVar.containsKey(line[i])) {
                    line[i] = tagVar[line[i]].toString()
                }
            }
        }

        scriptCode[fileName] = code

        for (dependency in code.dependency) {
            if (!scriptCode.containsKey(dependency)) {
                interpret(dependency)
            }
        }
    }

    //==========================================
    private var runArgs: ArrayList<String>? = null
    private lateinit var screenShot: ProjectionService

    fun setup(argv: ArrayList<String>?, projectionService: ProjectionService) {
        runArgs = argv
        screenShot = projectionService
    }

    override fun run() {
        board.announce("Running")
        runningFlag = true

        try {
            execute(ScriptName, runArgs, 0)
            board.announce("IDLE")
        } catch (e: ClickService.AccessibilityServiceOffException) {
            Log.i(LOG_TAG, "AutoClickDead, terminating.")
            board.announce("AutoClick Failed.")
        } finally {
            runningFlag = false
        }
    }

    private fun execute(
        fileName: String,
        args: ArrayList<String>?,
        depth: Int,
    ): Int { //Run code that is already read in MyCode

        val localVar: MutableMap<String, String> = HashMap()
        parseArguments(localVar, args)

        var pc = 0

        while (runningFlag && (pc < scriptCode[fileName]!!.codes.size)) {
            val command = scriptCode[fileName]!!.codes[pc]
            val arguments = command.copyOfRange(1, command.size)
            varsSubstitution(localVar, command[0], arguments)
            //=====================================================
            Log.i(LOG_TAG, "$pc  ${command.joinToString(" ")}")
            delay()
            when (command[0]) {
                Commands.EXIT -> {
                    runningFlag = false
                    return 1
                }
                Commands.LOG -> board.announce(arguments[0])
                Commands.JUMP_TO -> pc = arguments[0].toInt() - 1 //One-based
                Commands.WAIT -> sleep(
                    arguments[0].toLong()
                )
                Commands.CALL -> localVar["\$R"] = execute(arguments[0], null, depth + 1).toString()
                Commands.TAG -> {}
                Commands.RETURN -> return arguments[0].toInt()
                Commands.CLICK_PIC -> {
                    val tmp = readImg(arguments[0])
                    val target = ImageHandler.findLocation(
                        screenShot.shot(), tmp, arguments[1]
                            .toDouble()
                    )
                    if (target != null) {
                        Log.i(LOG_TAG, "Clicking Picture:" + target.x + " " + target.y)
                        ClickService.click(target.x.toInt(), target.y.toInt())
                        localVar["\$R"] = "0"
                    } else {
                        localVar["\$R"] = "1"
                    }
                }
                Commands.CLICK -> {
                    ClickService.click(arguments[0].toInt(), arguments[1].toInt())
                }
                Commands.CALL_ARG -> {
                    val nextArgv =
                        arguments.copyOfRange(1, command.size - 1).toCollection(ArrayList())
                    localVar["\$R"] = execute(arguments[0], nextArgv, depth + 1).toString()
                }
                Commands.IF_GREATER -> if (arguments[0].toInt() <= arguments[1].toInt()) { //Failed, skip next line
                    pc++
                }
                Commands.IF_SMALLER -> if (arguments[0].toInt() >= arguments[1].toInt()) { //Failed, skip next line
                    pc++
                }
                Commands.ADD -> localVar[arguments[0]] = (localVar[arguments[0]]!!
                    .toInt() + arguments[1].toInt()).toString()
                Commands.SUBTRACT -> localVar[arguments[0]] = (localVar[arguments[0]]!!
                    .toInt() - arguments[1].toInt()).toString()
                Commands.VAR -> localVar[arguments[0]] = arguments[1]
                Commands.CHECK -> if (ImageHandler.checkColor(
                        screenShot.shot(), arguments[0]
                            .toInt(), arguments[1].toInt(), arguments[2].toInt()
                    )
                ) {
                    localVar["\$R"] = "0"
                } else {
                    localVar["\$R"] = "1"
                }
                Commands.SWIPE -> {
                    delay()
                    ClickService.swipe(
                        arguments[0].toInt(),
                        arguments[1].toInt(),
                        arguments[2].toInt(),
                        arguments[3].toInt(),
                    )
                }
                Commands.COMPARE -> {
                    screenShot.shot() //Empty shot to make sure Image be the newest.
                    localVar["\$R"] = screenShot.compare(
                        readImg(arguments[4]), arguments[0]
                            .toInt(), arguments[1].toInt(), arguments[2].toInt(), arguments[3]
                            .toInt()
                    ).toString() // similarity
                }
                else -> throw RuntimeException("Cannot Recognize " + command[0])
            }
            pc++
        }
        return 0
    }

    companion object {
        private val LOG_TAG = Interpreter::class.java.simpleName


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