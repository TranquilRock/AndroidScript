package com.tranquilrock.androidscript.activity.editor.component

interface Updater {
    fun swap(a: Int, b: Int)
    fun delete(a: Int)
    fun insert()
    fun self(index: Int)
}