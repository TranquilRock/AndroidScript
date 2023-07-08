package com.tranquilrock.androidscript.component.manage

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.databinding.ManageResourceBlockBinding
import com.tranquilrock.androidscript.databinding.ManageScriptBlockBinding
import com.tranquilrock.androidscript.utils.ResourceRemover
import java.lang.RuntimeException

class ManageAdapter(
    private val data: List<Pair<String, Bitmap?>>, private val resourceRemover: ResourceRemover
) : RecyclerView.Adapter<ManageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        return when (viewType) {
            1 -> ManageViewHolder.ImageViewHolder(
                ManageResourceBlockBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )

            0 -> ManageViewHolder.ScriptViewHolder(
                ManageScriptBlockBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )

            else -> throw RuntimeException("Impossible")
        }

    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        holder.display(data[position].first,
            data[position].second,
            when (getItemViewType(position)) {
                1 -> View.OnClickListener { resourceRemover.removeImage(data[position].first) }
                0 -> View.OnClickListener {
                    resourceRemover.removeImage(
                        data[position].first
                    )
                }
                else -> View.OnClickListener { }
            })
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].second?.run { 1 } ?: 0
    }
}