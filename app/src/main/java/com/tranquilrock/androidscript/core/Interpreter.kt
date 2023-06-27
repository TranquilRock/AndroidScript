package com.tranquilrock.androidscript.core

import android.graphics.Bitmap
import android.util.Log
import com.tranquilrock.androidscript.service.ClickService
import com.tranquilrock.androidscript.service.WidgetService
import com.tranquilrock.androidscript.utils.ImageParser
import java.lang.Thread.sleep
import java.util.Vector
import kotlin.collections.HashMap

/**
 * Class that handles block to code.
 * */
@Suppress("unused")
class Interpreter(
    blockData: MutableList<MutableList<String>>,
    blockMeta: List<List<String>>,
    private val clicker: ClickService,
    private val imageParser: ImageParser,
    private val board: WidgetService.Bulletin,
) {

    private var scriptCode: HashMap<String, Code> = HashMap()

    init {
        val rootRawCode = Vector<String>()

        for (block in blockData) {
            // Replace block typeNum with block name
            block[0] = blockMeta[block[0].toInt()][0] + EXECUTABLE_EXTENSION_NAME
            rootRawCode.add(Command.CALL_ARG + block.joinToString(" "))
        }
        Code(rootRawCode).run {
            scriptCode[ROOT_RAW_CODE_KEY] = this
            loadAllCodeRecursive(this)
        }
    }

    /**
     * Line TAGs are replaced here and stored in scriptCode mapping.
     * */
    private fun loadAllCodeRecursive(code: Code) {
        for (dependency in code.dependency) {
            if (!scriptCode.containsKey(dependency)) {
                Code(mockRead(dependency)).run {
                    scriptCode[dependency] = this
                    loadAllCodeRecursive(this)
                }
            }
        }
    }

    /**
     * Script entry.
     * */

    private var runningFlag = false
    suspend fun run() {
        board.announce("Running")
        runningFlag = true
        execute(ROOT_RAW_CODE_KEY, emptyList(), 0)
        board.announce("IDLE")
    }

    private suspend fun execute(
        fileName: String,
        args: List<String>,
        depth: Int,
    ): Int { // The return value will be used by CALL && CALL_ARG
        val localVar = initLocalVar(args)
        var pc = 0
        while (runningFlag && (pc < scriptCode[fileName]!!.codes.size)) {
            val command = scriptCode[fileName]!!.codes[pc]
            val parameters = command.copyOfRange(1, command.size)
            varsSubstitution(localVar, command[0], parameters)
            //=====================================================
            Log.d(LOG_TAG, "$pc  ${command.joinToString(" ")}")
            when (command[0]) {
                Command.EXIT -> {
                    runningFlag = false
                    return 1
                }

                Command.LOG -> board.announce(parameters[0])
                Command.JUMP_TO -> pc = parameters[0].toInt() - 1 // One-based to zero-based
                Command.WAIT -> sleep(parameters[0].toLong())
                Command.CALL -> localVar["\$R"] =
                    execute(parameters[0], emptyList(), depth + 1).toString()

                // This command is a placeholder for Line Number, so no action needed.
                Command.TAG -> {}
                Command.RETURN -> return parameters[0].toInt()
                Command.CLICK_PIC -> {
                    val tmp = mockReadImage(parameters[0])
                    val target = imageParser.findLocTODO(tmp, parameters[1].toDouble())
                    if (target != null) {
                        Log.i(LOG_TAG, "Clicking Picture:" + target.x + " " + target.y)
                        clicker.click(target.x.toInt(), target.y.toInt())
                        localVar["\$R"] = "0"
                    } else {
                        localVar["\$R"] = "1"
                    }
                }

                Command.CLICK -> {
                    clicker.click(parameters[0].toInt(), parameters[1].toInt())
                }

                Command.CALL_ARG -> {
                    val nextArgv = parameters.copyOfRange(1, command.size - 1).toList()
                    localVar["\$R"] = execute(parameters[0], nextArgv, depth + 1).toString()
                }

                Command.IF_GREATER -> if (parameters[0].toInt() <= parameters[1].toInt()) { //Failed, skip next line
                    pc++
                }

                Command.IF_SMALLER -> if (parameters[0].toInt() >= parameters[1].toInt()) { //Failed, skip next line
                    pc++
                }

                Command.ADD -> {
                    val result = localVar[parameters[0]]!!.toInt() + parameters[1].toInt()
                    localVar[parameters[0]] = result.toString()
                }

                Command.SUBTRACT -> {
                    val result = localVar[parameters[0]]!!.toInt() - parameters[1].toInt()
                    localVar[parameters[0]] = result.toString()
                }

                Command.VAR -> localVar[parameters[0]] = parameters[1]
                Command.CHECK -> {
                    if (imageParser.checkScreenColor(
                            parameters[0].toInt(), parameters[1].toInt(), parameters[2].toInt()
                        )
                    ) {
                        localVar["\$R"] = "0"
                    } else {
                        localVar["\$R"] = "1"
                    }
                }

                Command.SWIPE -> {
                    clicker.swipe(
                        parameters[0].toInt(),
                        parameters[1].toInt(),
                        parameters[2].toInt(),
                        parameters[3].toInt(),
                    )
                }

                Command.COMPARE -> {
                    localVar["\$R"] = imageParser.compare(
                        mockReadImage(parameters[4]),
                        parameters[0].toInt(),
                        parameters[1].toInt(),
                        parameters[2].toInt(),
                        parameters[3].toInt()
                    ).toString() // similarity
                }

                else -> throw RuntimeException("Cannot Recognize $command in $fileName")
            }
            pc++
            delay()
        }
        return 0
    }

    private fun mockRead(@Suppress("UNUSED_PARAMETER") fileName: String): List<String> = emptyList()
    private fun mockReadImage(@Suppress("UNUSED_PARAMETER") fileName: String) = Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8)


    companion object {
        private const val EXECUTABLE_EXTENSION_NAME = ".txt"
        private const val ROOT_RAW_CODE_KEY = "ROOT_RAW_CODE"
        private val LOG_TAG = Interpreter::class.java.simpleName

        //==================== Helper =======================
        fun varsSubstitution(
            localVar: MutableMap<String, String>, command: String, parameters: Array<String>
        ) {
            if (parameters.isNotEmpty() && parameters[0][0] == '$' //Exit Has No Arg
                && command != "Var" && command != "Subtract" && command != "Add"
            ) {
                parameters[0] = localVar[parameters[0]]!!
            }
            for (z in 1 until parameters.size) {
                if (parameters[z][0] == '$') { //There might be Var command, that should replace $V
                    parameters[z] = localVar[parameters[z]]!!
                }
            }
        }

        fun initLocalVar(args: List<String>): HashMap<String, String> {
            val localVariables = HashMap<String, String>()
            localVariables["\$R"] = "0" // $R stores commands' return value.
            for (arg in args) {
                localVariables["$$localVariables.size"] = arg
            }
            return localVariables
        }

        fun delay() = sleep(100)
    }
}