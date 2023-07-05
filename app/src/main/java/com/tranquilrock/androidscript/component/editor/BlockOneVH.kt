package com.tranquilrock.androidscript.component.editor

import android.view.View
import com.tranquilrock.androidscript.R

class BlockOneVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
    override fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        super.onBind(order, blockData)
        Input(view.findViewById(R.id.OneVarInput), blockDef[1] as List<String>, blockData, this, 1)
    }
}