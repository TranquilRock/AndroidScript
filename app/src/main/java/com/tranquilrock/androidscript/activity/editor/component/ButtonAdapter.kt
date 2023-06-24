package com.tranquilrock.androidscript.activity.editor.component


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.core.Command

class ButtonAdapter(
    private val blockData: MutableList<MutableList<String>>,
    private val onInsert: Updater,
    private val buttonData: List<Array<*>>
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
            button.text = buttonData[position][0] as CharSequence?
            button.setOnClickListener {
                blockData.add(
                    INSERT_START_POSITION,
                    MutableList(
                        buttonData[position].size
                    ) { "" }.apply {
                        // TODO change data to be add
                        this[0] = buttonData[position][0] as String
                    }
                )
                onInsert.insert()
            }
        }
    }

    override fun getItemCount(): Int {
        return buttonData.size
    }

    companion object {
        private val TAG = ButtonAdapter::class.java.simpleName
        private const val INSERT_START_POSITION = 0
    }
}
