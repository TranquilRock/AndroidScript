package com.example.androidscript.core

import java.util.*
import java.util.regex.Pattern

class Code(RawCode: Vector<String>) {
    //Valid code that interpreter can recognize
    var codes = Vector<Array<String>>()
    var dependency = Vector<String>()

    init {
        for (line in RawCode) {
            var valid = false
            for (format in Commands.SUPPORTED_COMMAND) {
                if (format.let { Pattern.matches(it, line) }) {
                    valid = true
                    val command: Array<String> = line!!.split(" ".toRegex()).toTypedArray()
                    if (command[0] == Commands.CALL || command[0] == Commands.CALL_ARG) {
                        dependency.add(command[1])
                    }
                    codes.add(command)
                    break
                }
            }
            if (!valid) {
                throw Interpreter.InvalidCodeException("Invalid code \"$line\"")
            }
        }
    }
}