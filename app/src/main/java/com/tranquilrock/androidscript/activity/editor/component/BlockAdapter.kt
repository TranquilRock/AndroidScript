package com.tranquilrock.androidscript.activity.editor.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.core.Command
import java.util.Collections


class BlockAdapter(content: MutableList<MutableList<String>>) :
    RecyclerView.Adapter<BlockViewHolder>() {

    var data: MutableList<MutableList<String>>
    var onOrderChange: Updater

    init {
        data = content
        // TODO check these notify works properly
        onOrderChange = object : Updater {
            override fun swap(a: Int, b: Int) {
                if (a >= 0 && b < data.size && a < b) {
                    Collections.swap(data, a, b)
//                    notifyDataSetChanged()
                    notifyItemMoved(a, b)
                }
            }

            override fun delete(a: Int) {
                if (a >= 0 && a <= data.size - 1) {
                    data.removeAt(a)
//                    notifyDataSetChanged()
                    notifyItemRemoved(a)
                }
            }

            override fun insert() = notifyItemInserted(0)// notifyDataSetChanged()
            override fun self(index: Int) {}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_zero_var_format, parent, false)
                return BlockViewHolder.ZeroVH(view)
            }

            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_one_var_format, parent, false)
                return BlockViewHolder.OneVH(view)
            }

            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_two_var_format, parent, false)
                return BlockViewHolder.TwoVH(view)
            }

            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_three_var_format, parent, false)
                return BlockViewHolder.ThreeVH(view)
            }

            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_four_var_format, parent, false)
                return BlockViewHolder.FourVH(view)
            }

            5 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_five_var_format, parent, false)
                return BlockViewHolder.FiveVH(view)
            }
        }
        throw RuntimeException("Invalid view type!")
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.onBind(onOrderChange, data)
        holder.title.text = data[position][0]
        when (data[position][0]) {
            // TODO extract this to Basic.meta
            Command.EXIT -> {}
            Command.LOG -> holder.inputs[0].hint = "LogString"
            Command.JUMP_TO -> holder.inputs[0].hint = "Line"
            Command.WAIT -> holder.inputs[0].hint = "ms"
            Command.CALL -> holder.inputs[0].hint = ".txt"
            Command.TAG -> holder.inputs[0].hint = "\$Var"
            Command.RETURN -> holder.inputs[0].hint = "Value"
            Command.CLICK_PIC -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = ""
                holder.inputs[0].hint = "Image"
                holder.inputs[1].hint = "Ratio"
            }

            Command.CLICK -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = ""
                holder.inputs[0].hint = "X"
                holder.inputs[1].hint = "Y"
            }

            Command.CALL_ARG -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = ""
                holder.inputs[0].hint = ".txt"
                holder.inputs[1].hint = "Value"
            }

            Command.IF_GREATER -> {
                holder.title.text = "If"
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = ">"
                holder.inputs[0].hint = "\$Var"
                holder.inputs[1].hint = "Value"
            }

            Command.IF_SMALLER -> {
                holder.title.text = "If"
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = "<"
                holder.inputs[0].hint = "\$Var"
                holder.inputs[1].hint = "Value"
            }

            Command.ADD -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = "+="
                holder.inputs[0].hint = "\$Var"
                holder.inputs[1].hint = "Value"
            }

            Command.SUBTRACT -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = "-="
                holder.inputs[0].hint = "\$Var"
                holder.inputs[1].hint = "Value"
            }

            Command.VAR -> {
                (holder as BlockViewHolder.TwoVH).titleMiddle.text = "="
                holder.inputs[0].hint = "\$Name"
                holder.inputs[1].hint = "Value"
            }

            Command.CHECK -> {
                holder.inputs[0].hint = "x"
                holder.inputs[1].hint = "y"
                holder.inputs[2].hint = "color"
            }

            Command.SWIPE -> {
                holder.inputs[0].hint = "FromX"
                holder.inputs[1].hint = "FromY"
                holder.inputs[2].hint = "ToX"
                holder.inputs[3].hint = "ToY"
            }

            Command.COMPARE -> {
                holder.inputs[0].hint = "X1"
                holder.inputs[1].hint = "Y1"
                holder.inputs[2].hint = "X2"
                holder.inputs[3].hint = "Y2"
                holder.inputs[4].hint = "Image"
            }
        }
    }

    override fun getItemViewType(position: Int) = Command.getCommandLength(data[position][0])

    override fun getItemCount() = data.size
}