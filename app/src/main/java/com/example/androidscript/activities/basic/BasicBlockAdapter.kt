package com.example.androidscript.activities.basic

import com.example.androidscript.R
import com.example.androidscript.uitemplate.BlockAdapter
import android.view.*

import com.example.androidscript.activities.basic.BasicViewHolder.OneVH
import com.example.androidscript.activities.basic.BasicViewHolder.ZeroVH
import com.example.androidscript.activities.basic.BasicViewHolder.TwoVH
import com.example.androidscript.activities.basic.BasicViewHolder.ThreeVH
import com.example.androidscript.activities.basic.BasicViewHolder.FourVH
import com.example.androidscript.activities.basic.BasicViewHolder.FiveVH

import java.lang.RuntimeException
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
            "Exit" -> {}
            "Log" -> holder.inputs[0]!!.hint = "LogString"
            "JumpTo" -> holder.inputs[0]!!.hint = "Line"
            "Wait" -> holder.inputs[0]!!.hint = "ms"
            "Call" -> holder.inputs[0]!!.hint = ".txt"
            "Tag" -> holder.inputs[0]!!.hint = "\$Var"
            "Return" -> holder.inputs[0]!!.hint = "Value"
            "ClickPic" -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = "Image"
                holder.inputs[1]!!.hint = "Ratio"
            }
            "Click" -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = "X"
                holder.inputs[1]!!.hint = "Y"
            }
            "CallArg" -> {
                (holder as TwoVH).titleMiddle.text = ""
                holder.inputs[0]!!.hint = ".txt"
                holder.inputs[1]!!.hint = "Value"
            }
            "IfGreater" -> {
                holder.title.text = "If"
                (holder as TwoVH).titleMiddle.text = ">"
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            "IfSmaller" -> {
                holder.title.text = "If"
                (holder as TwoVH).titleMiddle.text = "<"
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            "Add" -> {
                (holder as TwoVH).titleMiddle.text = "+="
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            "Subtract" -> {
                (holder as TwoVH).titleMiddle.text = "-="
                holder.inputs[0]!!.hint = "\$Var"
                holder.inputs[1]!!.hint = "Value"
            }
            "Var" -> {
                (holder as TwoVH).titleMiddle.text = "="
                holder.inputs[0]!!.hint = "\$Name"
                holder.inputs[1]!!.hint = "Value"
            }
            "Check" -> {
                holder.inputs[0]!!.hint = "x"
                holder.inputs[1]!!.hint = "y"
                holder.inputs[2]!!.hint = "color"
            }
            "Swipe" -> {
                holder.inputs[0]!!.hint = "FromX"
                holder.inputs[1]!!.hint = "FromY"
                holder.inputs[2]!!.hint = "ToX"
                holder.inputs[3]!!.hint = "ToY"
            }
            "Compare" -> {
                holder.inputs[0]!!.hint = "X1"
                holder.inputs[1]!!.hint = "Y1"
                holder.inputs[2]!!.hint = "X2"
                holder.inputs[3]!!.hint = "Y2"
                holder.inputs[4]!!.hint = "Image"
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position][0]) {
            "Exit" -> return 0
            "Log", "JumpTo", "Wait", "Call", "Tag", "Return" -> return 1
            "ClickPic", "Click", "CallArg", "IfGreater", "IfSmaller", "Add", "Subtract", "Var" -> return 2
            "Check" -> return 3
            "Swipe" -> return 4
            "Compare" -> return 5
        }
        throw RuntimeException("Invalid command: " + data[position][0])
    }

    override fun getItemCount(): Int {
        return data.size
    }

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

            override fun insert() {
                notifyDataSetChanged()
            }

            override fun self(index: Int) {}
        }
    }
}