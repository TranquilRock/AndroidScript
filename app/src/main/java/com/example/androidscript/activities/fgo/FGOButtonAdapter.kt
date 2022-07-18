package com.example.androidscript.activities.fgo

import com.example.androidscript.uitemplate.BlockAdapter.Updater
import com.example.androidscript.uitemplate.ButtonAdapter
import java.lang.RuntimeException
import java.util.*

class FGOButtonAdapter(
    _blockContent: Vector<Vector<String>>,
    _buttonText: Vector<String>,
    _onInsert: Updater
) : ButtonAdapter(_buttonText) {
    private val blockContent: Vector<Vector<String>>
    private var onInsert: Updater
    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        when (buttonText[position]) {
            "ServantSkill" -> {
                holder.button.text = "從者技能"
                holder.button.setOnClickListener {
                    blockContent.insertElementAt(
                        Vector(listOf(*FGOEditor.SkillBlock)),
                        insertPosition
                    )
                    onInsert.insert()
                }
            }
            "SelectCard" -> {
                holder.button.text = "自動選卡"
                holder.button.setOnClickListener {
                    blockContent.insertElementAt(
                        Vector(listOf(*FGOEditor.NoblePhantasmsBlock)),
                        insertPosition
                    )
                    onInsert.insert()
                }
            }
            "CraftSkill" -> {
                holder.button.text = "御主技能"
                holder.button.setOnClickListener {
                    blockContent.insertElementAt(
                        Vector(listOf(*FGOEditor.CraftSkillBlock)),
                        insertPosition
                    )
                    onInsert.insert()
                }
            }
            else -> throw RuntimeException("Unrecognized button!")
        }
    }

    companion object {
        private const val insertPosition = 1
    }

    init {
        buttonText = _buttonText
        blockContent = _blockContent
        onInsert = _onInsert
    }
}