package com.tranquilrock.androidscript.component.editor

interface Updater {
    fun swap(id1: Int, id2: Int)
    fun delete(id: Int)
    fun insert()
    fun self(id: Int)
}