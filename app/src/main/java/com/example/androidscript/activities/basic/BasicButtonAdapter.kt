package com.example.androidscript.activities.basic

import android.view.View
import com.example.androidscript.uitemplate.BlockAdapter.Updater
import com.example.androidscript.uitemplate.ButtonAdapter
import java.util.*

class BasicButtonAdapter(
    _blockContent: Vector<Vector<String>>,
    _buttonText: Vector<String>,
    _onInsert: Updater
) : ButtonAdapter(_buttonText) {
    private val blockContent: Vector<Vector<String>>
    private var onInsert: Updater
    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.button.text = buttonText[position]
        when (buttonText[position]) {
            "Exit", "Log", "JumpTo", "Wait", "Call", "Tag", "Return", "ClickPic", "Click", "CallArg", "IfGreater", "IfSmaller", "Add", "Subtract", "Var", "Check", "Swipe", "Compare" -> holder.button.setOnClickListener(
                buttonListener(
                    buttonText[position]!!
                )
            )
            else -> throw RuntimeException("Unrecognized button!")
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

    init {
        buttonText = _buttonText
        blockContent = _blockContent
        onInsert = _onInsert
    }
}