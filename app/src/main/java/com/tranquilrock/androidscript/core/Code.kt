package com.tranquilrock.androidscript.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

import android.util.Log
import java.util.*
import java.util.regex.Pattern

@Parcelize
data class Code(val codes: Vector<Array<String>>, val dependency: Vector<String>) : Parcelable {

    constructor(rawCode: List<String>) : this(Vector<Array<String>>(), Vector<String>()) {
        for (line in rawCode) {
            var valid = false
            for (format in Command.COMMAND_FORMATS) {
                if (format.let { Pattern.matches(it, line) }) {
                    valid = true
                    val command: Array<String> = line.split(" ".toRegex()).toTypedArray()
                    if (command[0] == Command.CALL || command[0] == Command.CALL_ARG) {
                        dependency.add(command[1])
                    }
                    codes.add(command)
                    break
                }
            }
            if (!valid) {
                throw InvalidCodeException("Invalid code \"$line\"")
            }
        }

        // Replace TAGs with line numbers.
        val tagToLineCount: HashMap<String, String> = HashMap()

        for (i in 0 until codes.size) {
            val command = codes[i]
            if (command[0] == "Tag") {
                tagToLineCount[command[1]] = i.toString()
            }
        }

        for (line in codes) {
            for (i in 1 until line.size) { // The first word must be command
                if (tagToLineCount.containsKey(line[i])) {
                    line[i] = tagToLineCount[line[i]]!!
                }
            }
        }
    }

    class InvalidCodeException(s: String) : Exception() {
        init {
            Log.d(InvalidCodeException::class.java.simpleName, s)
        }
    }
}