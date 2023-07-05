package com.tranquilrock.androidscript.component.editor

import android.view.View
import android.widget.EditText
import com.tranquilrock.androidscript.R

class BlockThreeVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
    override fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        super.onBind(order, blockData)
        inputs = listOf(
            view.findViewById(R.id.ThreeVarInputLeft),
            view.findViewById(R.id.ThreeVarInputMiddle),
            view.findViewById(R.id.ThreeVarInputRight)
        )
        for (z in 0..2) {
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