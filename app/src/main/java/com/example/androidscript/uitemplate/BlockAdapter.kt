package com.example.androidscript.uitemplate

import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class BlockAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    lateinit var data: Vector<Vector<String>>
    lateinit var onOrderChange: Updater

    interface Updater {
        fun swap(a: Int, b: Int)
        fun delete(a: Int)
        fun insert()
        fun self(index: Int)
    }
}