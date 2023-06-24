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
    val onOrderChange: Updater

    init {
        data = content
        onOrderChange = object : Updater {
            override fun swap(id1: Int, id2: Int) {
                if (id1 >= 0 && id2 < data.size && id1 < id2) {
                    Collections.swap(data, id1, id2)
                    notifyItemMoved(id1, id2)
                }
            }
            override fun delete(id: Int) {
                if (id >= 0 && id <= data.size - 1) {
                    data.removeAt(id)
                    notifyItemRemoved(id)
                }
            }
            override fun insert() = notifyItemInserted(0)
            override fun self(id: Int) = notifyItemChanged(id)
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

    }

    override fun getItemViewType(position: Int) = Command.getCommandLength(data[position][0])

    override fun getItemCount() = data.size
}