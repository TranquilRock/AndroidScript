package com.tranquilrock.androidscript.component.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import java.util.Collections


class BlockAdapter(
    private val blockMeta: Array<Array<Any>>,
    val blockData: ArrayList<ArrayList<String>>
) :
    RecyclerView.Adapter<BlockViewHolder>() {

    val onOrderChange: Updater

    init {
        onOrderChange = object : Updater {
            override fun swap(id1: Int, id2: Int) {
                if (id1 >= 0 && id2 < blockData.size && id1 < id2) {
                    Collections.swap(blockData, id1, id2)
                    notifyItemMoved(id1, id2)
                }
            }

            override fun delete(id: Int) {
                if (id >= 0 && id <= blockData.size - 1) {
                    blockData.removeAt(id)
                    notifyItemRemoved(id)
                }
            }

            override fun insert() = notifyItemInserted(blockData.size - 1)
            override fun self(id: Int) = notifyItemChanged(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view: View
        val blockDef = blockMeta[viewType]

        when (blockDef.size - 1) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_zero_var_format, parent, false)
                return BlockViewHolder.ZeroVH(view, blockDef)
            }

            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_one_var_format, parent, false)
                return BlockViewHolder.OneVH(view, blockDef)
            }

            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_two_var_format, parent, false)
                return BlockViewHolder.TwoVH(view, blockDef)
            }

            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_three_var_format, parent, false)
                return BlockViewHolder.ThreeVH(view, blockDef)
            }

            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_four_var_format, parent, false)
                return BlockViewHolder.FourVH(view, blockDef)
            }

            5 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.basic_five_var_format, parent, false)
                return BlockViewHolder.FiveVH(view, blockDef)
            }
        }
        throw RuntimeException("$TAG Block!")
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.onBind(onOrderChange, blockData)
        holder.title.text = blockMeta[getItemViewType(position)][0] as CharSequence?
    }

    override fun getItemViewType(position: Int) = blockData[position][0].toInt()

    override fun getItemCount() = blockData.size

    companion object {
        private val TAG = BlockAdapter::class.java.simpleName
    }
}