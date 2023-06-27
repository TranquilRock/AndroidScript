/** Defines multiple blocks' view holder.
 * BlockViewHolder is inherited by ZeroVH, ..., FiveVH
 * Each VH has its own input list.
 * blockData contains blocks data, where blockData[0] is the title.
 * */
package com.tranquilrock.androidscript.activity.editor.component

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R

abstract class BlockViewHolder(protected var view: View, val blockDef: Array<*>) :
    RecyclerView.ViewHolder(
        view
    ) {
    // TODO change `inputs` to InputView
    lateinit var inputs: List<EditText>
    lateinit var title: TextView
    open fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
        (view.findViewById(R.id.btn_up) as ImageButton).setOnClickListener {
            order.swap(
                adapterPosition - 1,
                adapterPosition
            )
        }
        (view.findViewById(R.id.btn_down) as ImageButton).setOnClickListener {
            order.swap(
                adapterPosition,
                adapterPosition + 1
            )
        }
        (view.findViewById(R.id.btn_del) as ImageButton).setOnClickListener {
            order.delete(
                adapterPosition
            )
        }
        title = view.findViewById(R.id.Title)
    }

    class ZeroVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef)
        // TODO config view based on blockDef

    class OneVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
        override fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
            super.onBind(order, blockData)
            inputs = listOf(view.findViewById(R.id.OneVarInput))
            inputs[0].setText(blockData[adapterPosition][1])
            inputs[0].addTextChangedListener(getTextWatcher(blockData, 1, this))
        }
    }


    class TwoVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
        lateinit var titleMiddle: TextView
        override fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
            super.onBind(order, blockData)
            titleMiddle = view.findViewById(R.id.TwoVarTextMiddle)

            inputs = listOf(
                view.findViewById(R.id.TwoVarLeftInput),
                view.findViewById(R.id.TwoVarRightInput)
            )

            for (z in 0..1) {
                inputs[z].setText(blockData[adapterPosition][z + 1])
                inputs[z].addTextChangedListener(
                    getTextWatcher(
                        blockData,
                        z + 1,
                        this
                    )
                )
            }
        }
    }


    class ThreeVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
        override fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
            super.onBind(order, blockData)
            inputs = listOf(
                view.findViewById(R.id.ThreeVarInputLeft),
                view.findViewById(R.id.ThreeVarInputMiddle),
                view.findViewById(R.id.ThreeVarInputRight)
            )
            for (z in 0..2) {
                inputs[z].setText(blockData[adapterPosition][z + 1])
                inputs[z].addTextChangedListener(
                    getTextWatcher(
                        blockData,
                        z + 1,
                        this
                    )
                )
            }
        }
    }


    class FourVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
        override fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
            super.onBind(order, blockData)
            inputs = listOf(
                view.findViewById(R.id.FourVarInputLeftUp),
                view.findViewById(R.id.FourVarInputRightUp),
                view.findViewById(R.id.FourVarInputLeftBottom),
                view.findViewById(R.id.FourVarInputRightBottom)
            )
            for (z in 0..3) {
                inputs[z].setText(blockData[adapterPosition][z + 1])
                inputs[z].addTextChangedListener(
                    getTextWatcher(
                        blockData,
                        z + 1,
                        this
                    )
                )
            }
        }
    }


    class FiveVH(view: View, blockDef: Array<*>) : BlockViewHolder(view, blockDef) {
        override fun onBind(order: Updater, blockData: MutableList<MutableList<String>>) {
            super.onBind(order, blockData)
            inputs = listOf(
                view.findViewById(R.id.FiveVarInputLeftUp),
                view.findViewById(R.id.FiveVarInputRightUp),
                view.findViewById(R.id.FiveVarInputLeftBottom),
                view.findViewById(R.id.FiveVarInputRightBottom),
                view.findViewById(R.id.FiveVarInputLast)
            )
            for (z in 0..4) {
                inputs[z].setText(blockData[adapterPosition][z + 1])
                inputs[z].addTextChangedListener(
                    getTextWatcher(
                        blockData,
                        z + 1,
                        this
                    )
                )
            }
        }
    }

    companion object {
        protected fun getTextWatcher(
            blockData: MutableList<MutableList<String>>,
            index: Int,
            blockViewHolder: BlockViewHolder
        ): TextWatcher {
            return object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    blockData[blockViewHolder.adapterPosition][index] = s.toString()
                }
            }
        }
    }
}