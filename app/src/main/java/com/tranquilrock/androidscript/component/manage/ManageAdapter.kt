package com.tranquilrock.androidscript.component.manage

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.databinding.ManageResourceBlockBinding
import com.tranquilrock.androidscript.databinding.ManageScriptBlockBinding
import com.tranquilrock.androidscript.utils.ResourceRemover
import java.lang.RuntimeException

class ManageAdapter(
    private val data: MutableList<Pair<String, Bitmap?>>,
    private val resourceRemover: ResourceRemover
) : RecyclerView.Adapter<ManageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        return when (viewType) {
            1 -> ManageViewHolder.ImageViewHolder(
                ManageResourceBlockBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            0 -> ManageViewHolder.ScriptViewHolder(
                ManageScriptBlockBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            else -> throw RuntimeException("Impossible")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val (fileName, image) = data[position]
        when (getItemViewType(position)) {
            1 -> holder.display(
                fileName, image
            ) {
                data.removeAt(holder.adapterPosition)
                resourceRemover.removeImage(fileName)
                notifyItemRemoved(holder.adapterPosition)
            }

            0 -> holder.display(
                fileName, null
            ) {
                data.removeAt(holder.adapterPosition)
                resourceRemover.removeScript(fileName)
                notifyItemRemoved(holder.adapterPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].second?.run { 1 } ?: 0
    }
}