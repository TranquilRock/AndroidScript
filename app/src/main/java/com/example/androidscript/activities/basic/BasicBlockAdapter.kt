package com.example.androidscript.activities.basic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidscript.R
import com.example.androidscript.activities.basic.BasicViewHolder.*
import com.example.androidscript.activities.template.BlockAdapter
import com.example.androidscript.core.Commands
import com.example.androidscript.core.Commands.getCommandLength
import java.util.*

class BasicBlockAdapter(content: Vector<Vector<String>>) : BlockAdapter<BasicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_zero_var_format, parent, false)
                return ZeroVH(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_one_var_format, parent, false)
                return OneVH(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_two_var_format, parent, false)
                return TwoVH(view)
            }
            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_three_var_format, parent, false)
                return ThreeVH(view)
            }
            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_four_var_format, parent, false)
                return FourVH(view)
            }
            5 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_five_var_format, parent, false)
                return FiveVH(view)
            }
        }
        throw RuntimeException("Invalid view type!")
    }

    override fun onBindViewHolder(holder: BasicViewHolder, position: Int) {
        holder.onBind(onOrderChange, position, data)
        holder.title.text = data[position][0]
        when (data[position][0]) {
            Commands.EXIT -> {}
            Commands.LOG -> holder.inputs[0]!!.hint = "LogString"
            Commands.JUMP_TO -> holder.inputs[0]!!.hint = "Line"
            Commands.WAIT -> holder.inputs[0]!!.hint = "ms"
            Commands.CALL -> holder.inputs[0]!!.hint = ".txt"
            Commands.TAG -> holder.inputs[0]!!.hint = "\$Var"
            Commands.RETURN -> holder.inputs[0]!!.hint = "Value"
            Commands.CLICK_PIC -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = "Image"
                holder.inputs[1]!!.hint = "Ratio"
            }
            Commands.CLICK -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = "X"
                holder.inputs[1]!!.hint = "Y"
            }
            Commands.CALL_ARG -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = ".txt"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.IF_GREATER -> {
                holder.title.text = "If"
                (holder as TwoVH).titleMiddle.text = ">"
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.IF_SMALLER -> {
                holder.title.text = "If"
                (holder as TwoVH).titleMiddle.text = "<"
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.ADD -> {
                (holder as TwoVH).titleMiddle.text = "+="
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.SUBTRACT -> {
                (holder as TwoVH).titleMiddle.text = "-="
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.VAR -> {
                (holder as TwoVH).titleMiddle.text = "="
                holder.inputs[0]!!.hint = "\$Name"
                holder.inputs[1]!!.hint = "Value"
            }
            Commands.CHECK -> {
                holder.inputs[0]!!.hint = "x"
                holder.inputs[1]!!.hint = "y"
                holder.inputs[2]!!.hint = "color"
            }
            Commands.SWIPE -> {
                holder.inputs[0]!!.hint = "FromX"
                holder.inputs[1]!!.hint = "FromY"
                holder.inputs[2]!!.hint = "ToX"
                holder.inputs[3]!!.hint = "ToY"
            }
            Commands.COMPARE -> {
                holder.inputs[0]!!.hint = "X1"
                holder.inputs[1]!!.hint = "Y1"
                holder.inputs[2]!!.hint = "X2"
                holder.inputs[3]!!.hint = "Y2"
                holder.inputs[4]!!.hint = "Image"
            }
        }
    }

    override fun getItemViewType(position: Int) = getCommandLength(data[position][0])

    override fun getItemCount() = data.size

    init {
        data = content
        onOrderChange = object : Updater {
            override fun swap(a: Int, b: Int) {
                if (a >= 0 && b < data.size && a < b) {
                    Collections.swap(data, a, b)
                    notifyDataSetChanged()
                }
            }

            override fun delete(a: Int) {
                if (a >= 0 && a <= data.size - 1) {
                    data.removeAt(a)
                    notifyDataSetChanged()
                }
            }

            override fun insert() = notifyDataSetChanged()
            override fun self(index: Int) {}
        }
    }
}