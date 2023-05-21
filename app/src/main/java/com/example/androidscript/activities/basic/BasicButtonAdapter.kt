package com.example.androidscript.activities.basic

import android.view.View
import com.example.androidscript.activities.template.BlockAdapter.Updater
import com.example.androidscript.activities.template.ButtonAdapter
import com.example.androidscript.core.Commands
import java.util.*

class BasicButtonAdapter(
    private val blockContent: Vector<Vector<String>>,
    private val onInsert: Updater,
    buttonText: Vector<String>
) : ButtonAdapter(buttonText) {
    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.run {
            button.text = buttonText[position]
            when (buttonText[position]) {
                in Commands.ALL_COMMAND_LIST -> button.setOnClickListener(
                    buttonListener(
                        buttonText[position]!!
                    )
                )
                else -> throw RuntimeException("Unrecognized button!")
            }
        }
    }

    private fun buttonListener(blockTitle: String): View.OnClickListener {
        return View.OnClickListener {
            blockContent.insertElementAt(
                Vector<String>(BasicEditor.Blocks[blockTitle]!!),
                insertPosition
            )
            onInsert.insert()
        }
    }

    companion object {
        private const val insertPosition = 0
    }
}