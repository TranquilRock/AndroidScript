package com.example.androidscript.activities.basic

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidscript.R
import com.example.androidscript.activities.template.BlockAdapter.Updater
import java.util.*

abstract class BasicViewHolder(protected var view: View) : RecyclerView.ViewHolder(
    view
) {
    lateinit var title: TextView
    lateinit var inputs: Array<EditText?>

    open fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
        (view.findViewById(R.id.btn_up) as ImageButton).setOnClickListener {
            order.swap(
                position - 1,
                position
            )
        }
        (view.findViewById(R.id.btn_down) as ImageButton).setOnClickListener {
            order.swap(
                position,
                position + 1
            )
        }
        (view.findViewById(R.id.btn_del) as ImageButton).setOnClickListener {
            order.delete(
                position
            )
        }
        title = view.findViewById(R.id.Title)
    }

    class ZeroVH(view: View) : BasicViewHolder(view)
    class OneVH(view: View) : BasicViewHolder(view) {
        override fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
            super.onBind(order, position, Data)
            inputs[0] = view.findViewById(R.id.OneVarInput)
            inputs[0]!!.setText(Data[position]!![1])
            inputs[0]!!.addTextChangedListener(getTextWatcher(Data, position, 1, this))
        }

        init {
            inputs = arrayOfNulls(1)
        }
    }

    class TwoVH(view: View) : BasicViewHolder(view) {
        lateinit var titleMiddle: TextView
        override fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
            super.onBind(order, position, Data)
            titleMiddle = view.findViewById(R.id.TwoVarTextMiddle)
            inputs[0] = view.findViewById(R.id.TwoVarLeftInput)
            inputs[1] = view.findViewById(R.id.TwoVarRightInput)
            for (z in 0..1) {
                inputs[z]!!.setText(Data[position]!![z + 1])
                inputs[z]!!.addTextChangedListener(getTextWatcher(Data, position, z + 1, this))
            }
        }

        init {
            inputs = arrayOfNulls(2)
        }
    }

    class ThreeVH(view: View) : BasicViewHolder(view) {
        override fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
            super.onBind(order, position, Data)
            inputs[0] = view.findViewById(R.id.ThreeVarInputLeft)
            inputs[1] = view.findViewById(R.id.ThreeVarInputMiddle)
            inputs[2] = view.findViewById(R.id.ThreeVarInputRight)
            for (z in 0..2) {
                inputs[z]!!.setText(Data[position]!![z + 1])
                inputs[z]!!.addTextChangedListener(getTextWatcher(Data, position, z + 1, this))
            }
        }

        init {
            inputs = arrayOfNulls(3)
        }
    }

    class FourVH(view: View) : BasicViewHolder(view) {
        override fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
            super.onBind(order, position, Data)
            inputs[0] = view.findViewById(R.id.FourVarInputLeftUp)
            inputs[1] = view.findViewById(R.id.FourVarInputRightUp)
            inputs[2] = view.findViewById(R.id.FourVarInputLeftBottom)
            inputs[3] = view.findViewById(R.id.FourVarInputRightBottom)
            for (z in 0..3) {
                inputs[z]!!.setText(Data[position]!![z + 1])
                inputs[z]!!.addTextChangedListener(getTextWatcher(Data, position, z + 1, this))
            }
        }

        init {
            inputs = arrayOfNulls(4)
        }
    }

    class FiveVH(view: View) : BasicViewHolder(view) {
        override fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
            super.onBind(order, position, Data)
            inputs[0] = view.findViewById(R.id.FiveVarInputLeftUp)
            inputs[1] = view.findViewById(R.id.FiveVarInputRightUp)
            inputs[2] = view.findViewById(R.id.FiveVarInputLeftBottom)
            inputs[3] = view.findViewById(R.id.FiveVarInputRightBottom)
            inputs[4] = view.findViewById(R.id.FiveVarInputLast)
            for (z in 0..4) {
                inputs[z]!!.setText(Data[position]!![z + 1])
                inputs[z]!!.addTextChangedListener(getTextWatcher(Data, position, z + 1, this))
            }
        }

        init {
            inputs = arrayOfNulls(5)
        }
    }

    companion object {
        protected fun getTextWatcher(
            Data: Vector<Vector<String>>,
            position: Int,
            index: Int,
            abc: BasicViewHolder
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
                    if (abc.adapterPosition == position) {
                        Data[position]!!.setElementAt(s.toString(), index)
                    }
                }
            }
        }
    }
}