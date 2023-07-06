package com.tranquilrock.androidscript.component.editor

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import java.util.Collections


class BlockAdapter(
    private val blockMeta: Array<Pair<String, List<List<String>>>>,
    val blockData: ArrayList<ArrayList<String>>
) : RecyclerView.Adapter<BlockViewHolder>() {

    val onOrderChange: Updater = object : Updater {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val blockDef = blockMeta[viewType].second
        assert(blockDef.size < BLOCK_VIEWS_RID.size) { Log.e(TAG, "Block $blockMeta too long!") }
        val (layoutId, inputIds) = BLOCK_VIEWS_RID[blockDef.size]
        val view: View = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return BlockViewHolder(view, blockMeta[viewType].first, blockDef, inputIds)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) =
        holder.onBind(onOrderChange, blockData)

    override fun getItemViewType(position: Int) = blockData[position][0].toInt()

    override fun getItemCount() = blockData.size

    companion object {
        private val TAG = BlockAdapter::class.java.simpleName
        private val BLOCK_VIEWS_RID = listOf(
            Pair(R.layout.edit_block_0, emptyList()),
            Pair(R.layout.edit_block_1, listOf(R.id.OneVarInput)),
            Pair(R.layout.edit_block_2, listOf(R.id.Two1, R.id.Two2)),
            Pair(
                R.layout.edit_block_3, listOf(
                    (R.id.Three1), (R.id.Three2), (R.id.Three3)
                )
            ),
            Pair(
                R.layout.edit_block_4, listOf(
                    R.id.Four1, R.id.Four2, R.id.Four3, R.id.Four4
                )
            ),
            Pair(
                R.layout.edit_block_5, listOf(
                    R.id.Five1, R.id.Five2, R.id.Five3, R.id.Five4, R.id.Five5
                )
            ),
        )
    }
}