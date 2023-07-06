package com.tranquilrock.androidscript.component.editor


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import java.util.Collections

class ButtonAdapter(
    private val blockMeta: Array<Pair<String, List<List<String>>>>,
    private val blockData: ArrayList<ArrayList<String>>,
    private val onInsert: Updater
) : RecyclerView.Adapter<ButtonViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_button, parent, false)
        return ButtonViewHolder(view, view.findViewById(R.id.button_item))
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.run {
            button.text = blockMeta[position].first
            button.setOnClickListener {
                blockData.add(
                    ArrayList<String>(Collections.nCopies(blockMeta[position].second.size + 1, "")).apply {
                        // TODO change data to be add
                        this[0] = position.toString()
                    }
                )
                onInsert.insert()
            }
        }
    }

    override fun getItemCount(): Int {
        return blockMeta.size
    }
}
