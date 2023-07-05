package com.tranquilrock.androidscript.component.editor

import android.view.View
import android.widget.EditText
import com.tranquilrock.androidscript.R

class BlockFiveVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
    override fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        super.onBind(order, blockData)
        inputs = listOf(
            view.findViewById(R.id.FiveVarInputLeftUp),
            view.findViewById(R.id.FiveVarInputRightUp),
            view.findViewById(R.id.FiveVarInputLeftBottom),
            view.findViewById(R.id.FiveVarInputRightBottom),
            view.findViewById(R.id.FiveVarInputLast)
        )
        for (z in 0..4) {
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