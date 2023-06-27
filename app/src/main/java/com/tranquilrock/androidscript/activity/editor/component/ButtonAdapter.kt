package com.tranquilrock.androidscript.activity.editor.component


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R

class ButtonAdapter(
    private val blockMeta: List<Array<*>>,
    private val blockData: MutableList<MutableList<String>>,
    private val onInsert: Updater
) : RecyclerView.Adapter<ButtonViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.button_item, parent, false)
        return ButtonViewHolder(view, view.findViewById(R.id.button_item))
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.run {
            button.text = blockMeta[position][0] as CharSequence?
            button.setOnClickListener {
                blockData.add(
                    MutableList(
                        blockMeta[position].size
                    ) { "" }.apply {
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

    companion object {
        private val TAG = ButtonAdapter::class.java.simpleName
    }
}