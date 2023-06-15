package com.example.androidscript.activities.template

import java.util.*

interface UICompiler {
    fun compile(data: Vector<Vector<String>>)

    class ImageLocation(
        private val upperLeft: Pair<Int, Int>,
        private val lowerRight: Pair<Int, Int>,
        private val imageFileName: String
    ) {
        override fun toString(): String {
            return "${upperLeft.first} ${upperLeft.second} ${lowerRight.first} ${lowerRight.second} $imageFileName"
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
