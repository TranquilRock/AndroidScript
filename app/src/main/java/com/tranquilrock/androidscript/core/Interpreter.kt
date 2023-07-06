package com.tranquilrock.androidscript.core

import android.util.Log
import com.tranquilrock.androidscript.App
import com.tranquilrock.androidscript.service.ClickService
import com.tranquilrock.androidscript.service.WidgetService
import com.tranquilrock.androidscript.utils.ResourceReader
import com.tranquilrock.androidscript.utils.ImageParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import java.util.Vector

/**
 * Class that handles block to code.
 * */
class Interpreter(
    blockData: ArrayList<ArrayList<String>>,
    blockMeta: Array<Array<Any>>,
    private val resourceReader: ResourceReader,
    private val clicker: ClickService?,
    private val imageParser: ImageParser,
    private val board: WidgetService.Bulletin,
) {

    private var runningFlag = false
    private var scriptCode: HashMap<String, Code> = HashMap()
    private val rootRawCode = Vector<String>()

    init {
        /**
         * Transform block data to raw code, according to block metadata.
         * */
        for (block in blockData) {
            val blockName = blockMeta[block[0].toInt()][0] as String
            block[0] = blockName
            if (resourceReader.scriptType == App.BASIC_SCRIPT_TYPE) {
                rootRawCode.add(block.joinToString(" "))
            } else if (blockName == "DoAgain") {
                rootRawCode.add(Command.JUMP_TO + " 0")
            } else {
                // Replace block typeNum with block name
                if (block.size == 1) {
                    rootRawCode.add(Command.CALL + " " + block.joinToString(" "))
                } else {
                    rootRawCode.add(Command.CALL_ARG + " " + block.joinToString(" "))
                }
            }
        }

        /**
         * Load the code files recursively and add to file mapping.
         */
        try {
            Code(rootRawCode).run {
                scriptCode[ROOT_RAW_CODE_KEY] = this
                loadAllCodeRecursive(this)
            }
        } catch (e: Code.InvalidCodeException) {
            scriptCode = HashMap()
            e.printStackTrace()
            Log.e(TAG, "Code invalid, cleanup map.")
        }
    }


    /**
     * The interpreter entry point.
     * */
    suspend fun run() {
        board.announce("Running")
        runningFlag = true
        try {
            execute(ROOT_RAW_CODE_KEY, emptyList(), 0)
            board.announce("IDLE")
        } catch (e: NumberFormatException) {
            Log.e(TAG, "Execute:: Wrong format!")
            board.announce("Error!")
        }
    }

    /**
     * Add code files to mapping recursively.
     *
     * @param code A parsed code file.
     * @throws Code.InvalidCodeException when code does not conform certain pattern,
     *  please refers to `Command.kt`.
     * */
    @Throws(Code.InvalidCodeException::class)
    private fun loadAllCodeRecursive(code: Code) {
        for (dependency in code.dependency) {
            if (!scriptCode.containsKey(dependency)) {
                Code(resourceReader.getCode(dependency)).run {
                    scriptCode[dependency] = this
                    loadAllCodeRecursive(this)
                }
            }
        }
    }

    /**
     * The core of interpreter, map code to kotlin instructions to perform actions.
     */
    private suspend fun execute(
        fileName: String,
        args: List<String>,
        depth: Int,
    ): Int { // The return value will be used by CALL && CALL_ARG

        /**
         * Initializes the local variable mapping.
         */
        val localVar = HashMap<String, String>()
        localVar["\$R"] = "0" // $R stores commands' return value.
        for (arg in args) {
            localVar["$${localVar.size}"] = arg
        }

        /**
         * Main loop.
         */
        var pc = 0 // Program Counter
        while (runningFlag && (pc < scriptCode[fileName]!!.codes.size)) {
            val command = scriptCode[fileName]!!.codes[pc]
            val parameters = command.copyOfRange(1, command.size)
            Log.d(TAG, "$pc  '$depth ${command.joinToString("' '")}'")
            varsSubstitution(localVar, command[0], parameters)
            //=====================================================
            when (command[0]) {
                Command.EXIT -> {
                    runningFlag = false
                    return 1
                }

                Command.IF_EXIST -> if (!imageParser.exist(resourceReader.getImage(parameters[0]))) {
                    pc++
                }

                Command.LOG -> board.announce(parameters[0])
                Command.JUMP_TO -> pc =
                    parameters[0].toInt() - 1 // Zero-based, -1 to cancel ++ below.
                Command.WAIT -> delay(parameters[0].toLong())
                Command.CALL -> localVar["\$R"] =
                    execute(parameters[0], emptyList(), depth + 1).toString()

                // This command is a placeholder for Line Number, so no action needed.
                Command.TAG -> {}
                Command.RETURN -> return parameters[0].toInt()
                Command.CLICK_PIC -> {
                    val tmp = resourceReader.getImage(parameters[0])
                    val target = imageParser.findLocation(tmp, parameters[1].toDouble())
                    if (target != null) {
                        Log.i(TAG, "Clicking Picture:" + target.x + " " + target.y)
                        clicker?.click(target.x.toInt(), target.y.toInt())
                        localVar["\$R"] = "0"
                    } else {
                        localVar["\$R"] = "1"
                    }
                }

                Command.CLICK -> {
                    clicker?.click(parameters[0].toInt(), parameters[1].toInt())
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

                Command.IF_EQUAL -> if (parameters[0] != parameters[1]) { //Failed, skip next line
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
                    clicker?.swipe(
                        parameters[0].toInt(),
                        parameters[1].toInt(),
                        parameters[2].toInt(),
                        parameters[3].toInt(),
                    ) ?: Log.e(TAG, "")
                }

                Command.COMPARE -> {
                    localVar["\$R"] = imageParser.compare(
                        resourceReader.getImage(parameters[4]),
                        parameters[0].toInt(),
                        parameters[1].toInt(),
                        parameters[2].toInt(),
                        parameters[3].toInt()
                    ).toString() // similarity
                }

                else -> {
                    runningFlag = false
                    Log.e(TAG, "Cannot Recognize $command in $fileName")
                }
            }
            pc++
            delay()
        }
        return 0
    }

    private suspend fun delay(millis: Long = 100L) = withContext(Dispatchers.IO) { sleep(millis) }

    companion object {
        private const val ROOT_RAW_CODE_KEY = "ROOT_RAW_CODE"
        private val TAG = Interpreter::class.java.simpleName

        /**
         * Substitute variables within statement, except the variable assign parts.
         */
        private fun varsSubstitution(
            localNameValMap: MutableMap<String, String>, command: String, parameters: Array<String>
        ) {
            try {
                if (command !in Command.ASSIGN_COMMAND && parameters.isNotEmpty() && parameters[0].startsWith(
                        "$"
                    )
                ) {
                    // The first Variable of ASSIGN_COMMAND is preserved.
                    parameters[0] = localNameValMap[parameters[0]]!!
                }
                for (z in 1 until parameters.size) {
                    if (parameters[z].startsWith("$")) {
                        parameters[z] = localNameValMap[parameters[z]]!!
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                Log.e(TAG, "$command No such parameter: ${parameters.joinToString { " " }}")
            }
        }
    }
}