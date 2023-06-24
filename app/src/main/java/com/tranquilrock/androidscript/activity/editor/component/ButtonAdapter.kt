package com.tranquilrock.androidscript.activity.editor.component


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R
import com.tranquilrock.androidscript.core.Command

class ButtonAdapter(
    private val blockContent: MutableList<MutableList<String>>,
    private val onInsert: Updater,
    private val buttonText: MutableList<String>
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
            button.text = buttonText[position]
            when (buttonText[position]) {
                in Command.ALL_COMMAND_LIST -> button.setOnClickListener {
                    blockContent.add(
                        INSERT_START_POSITION,
                        MutableList(Command.getCommandLength(buttonText[position]) + 1) { "" }.apply {
                            this[0] = buttonText[position]
                        }
                    )
                    onInsert.insert()
                }
                else -> throw RuntimeException("$TAG:: Unrecognized button!")
            }
        }
    }

    override fun getItemCount(): Int {
        return buttonText.size
    }

    companion object {
        private val TAG = ButtonAdapter::class.java.simpleName
        private const val INSERT_START_POSITION = 0
    }
}
