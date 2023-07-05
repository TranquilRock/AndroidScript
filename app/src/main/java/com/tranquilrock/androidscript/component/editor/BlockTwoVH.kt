package com.tranquilrock.androidscript.component.editor

import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.tranquilrock.androidscript.R

class BlockTwoVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
    lateinit var titleMiddle: TextView
    override fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        super.onBind(order, blockData)
        titleMiddle = view.findViewById(R.id.TwoVarTextMiddle)

        inputs = listOf(
            view.findViewById(R.id.TwoVarLeftInput),
            view.findViewById(R.id.TwoVarRightInput)
        )

        for (z in 0..1) {
            (inputs[z] as EditText).setText(blockData[adapterPosition][z + 1])
            (inputs[z] as EditText).addTextChangedListener(
                getTextWatcher(
                    blockData,
                    z + 1,
                    this
                )
            )
        }
    }
}