package com.tranquilrock.androidscript.component.editor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import java.util.Collections


class BlockAdapter(
    private val blockMeta: Array<Array<Any>>, val blockData: ArrayList<ArrayList<String>>
) : RecyclerView.Adapter<BlockViewHolder>() {

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
        val blockDef = blockMeta[viewType] // `blockDef` should be [str, List, List, ....]
        if (blockDef.size > 6) {
            Log.e(TAG, "Block too long!")
            throw RuntimeException("Block too long!")
        }
        val (layoutId, inputIds) = gg[blockDef.size - 1]
        val view: View = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BlockViewHolder(view, blockDef, inputIds)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.onBind(onOrderChange, blockData)
    }

    override fun getItemViewType(position: Int) = blockData[position][0].toInt()

    override fun getItemCount() = blockData.size

    companion object {
        private val TAG = BlockAdapter::class.java.simpleName
        val gg = listOf(
            Pair(R.layout.edit_block_0, emptyList()),
            Pair(R.layout.edit_block_1, listOf(R.id.OneVarInput)),
            Pair(R.layout.edit_block_2, listOf(R.id.TwoVarLeftInput, R.id.TwoVarRightInput)),
            Pair(
                R.layout.edit_block_3, listOf(
                    (R.id.ThreeVarInputLeft), (R.id.ThreeVarInputMiddle), (R.id.ThreeVarInputRight)
                )
            ),
            Pair(
                R.layout.edit_block_4, listOf(
                    R.id.FourVarInputLeftUp,
                    R.id.FourVarInputRightUp,
                    R.id.FourVarInputLeftBottom,
                    R.id.FourVarInputRightBottom
                )
            ),
            Pair(
                R.layout.edit_block_5, listOf(
                    R.id.FiveVarInputLeftUp,
                    R.id.FiveVarInputRightUp,
                    R.id.FiveVarInputLeftBottom,
                    R.id.FiveVarInputRightBottom,
                    R.id.FiveVarInputLast
                )
            ),
        )
    }
}