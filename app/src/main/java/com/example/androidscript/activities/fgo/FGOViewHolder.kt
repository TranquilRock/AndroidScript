package com.example.androidscript.activities.fgo

import android.widget.Spinner
import com.example.androidscript.R
import android.widget.ImageButton
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import android.widget.CompoundButton
import android.widget.AdapterView
import android.text.TextWatcher
import android.text.Editable
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.androidscript.uitemplate.BlockAdapter.Updater
import java.util.*

abstract class FGOViewHolder(protected var view: View) : RecyclerView.ViewHolder(
    view
) {
    private lateinit var upBtn: ImageButton
    private lateinit var downBtn: ImageButton
    private lateinit var closeBtn: ImageButton
    open fun onBind(order: Updater, position: Int, Data: Vector<Vector<String>>) {
        upBtn = view.findViewById(R.id.btn_up)
        upBtn.setOnClickListener {
            order.swap(
                position - 1,
                position
            )
        }
        downBtn = view.findViewById(R.id.btn_down)
        downBtn.setOnClickListener {
            order.swap(
                position,
                position + 1
            )
        }
        closeBtn = view.findViewById(R.id.btn_del)
        closeBtn.setOnClickListener { order.delete(position) }
    }

    //============================================================
    class PreStageVH(v: View) : FGOViewHolder(v) {
        private lateinit var stamina: Spinner
        private lateinit var friend: Spinner
        private lateinit var craft: Spinner
        private lateinit var repeat: EditText
        private lateinit var config: SwitchCompat
        private lateinit var x1: EditText
        private lateinit var y1: EditText
        private lateinit var x2: EditText
        private lateinit var y2: EditText
        override fun onBind(
            order: Updater,
            position: Int,
            Data: Vector<Vector<String>>
        ) {
            val data = Data[position]
            stamina = view.findViewById(R.id.stamina)
            stamina.onItemSelectedListener = spinnerListener(data, 1)
            stamina.setSelection(data[1].toInt())
            friend = view.findViewById(R.id.friend)
            setRawSpinner(friend, data, 2)
            craft = view.findViewById(R.id.craft)
            setRawSpinner(craft, data, 3)
            repeat = view.findViewById(R.id.n_repeat)
            repeat.addTextChangedListener(getTextWatcher(data, 4))
            repeat.setText(data[4])
            config = view.findViewById(R.id.config)
            config.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    data[5] = "1"
                } else {
                    data[5] = "0"
                }
                order.self(position)
            }
            config.isChecked = data[5] == "1"
            x1 = view.findViewById(R.id.x1)
            y1 = view.findViewById(R.id.y1)
            x2 = view.findViewById(R.id.x2)
            y2 = view.findViewById(R.id.y2)
            if (config.isChecked) {
                view.findViewById<View>(R.id.game_pos).visibility = View.VISIBLE
                x1.visibility = View.VISIBLE
                y1.visibility = View.VISIBLE
                x2.visibility = View.VISIBLE
                y2.visibility = View.VISIBLE
                x1.addTextChangedListener(getTextWatcher(data, 6))
                x1.setText(data[6])
                y1.addTextChangedListener(getTextWatcher(data, 7))
                y1.setText(data[7])
                x2.addTextChangedListener(getTextWatcher(data, 8))
                x2.setText(data[8])
                y2.addTextChangedListener(getTextWatcher(data, 9))
                y2.setText(data[9])
            } else {
                view.findViewById<View>(R.id.game_pos).visibility = View.GONE
                x1.visibility = View.GONE
                y1.visibility = View.GONE
                x2.visibility = View.GONE
                y2.visibility = View.GONE
            }
        }
    }

    class SkillVH(v: View) : FGOViewHolder(v) {
        private lateinit var skl11: Spinner
        private lateinit var skl12: Spinner
        private lateinit var skl13: Spinner
        private lateinit var skl21: Spinner
        private lateinit var skl22: Spinner
        private lateinit var skl23: Spinner
        private lateinit var skl31: Spinner
        private lateinit var skl32: Spinner
        private lateinit var skl33: Spinner
        override fun onBind(
            order: Updater,
            position: Int,
            Data: Vector<Vector<String>>
        ) {
            super.onBind(order, position, Data)
            val data = Data[position]
            skl11 = view.findViewById(R.id.skill_1_1)
            skl12 = view.findViewById(R.id.skill_1_2)
            skl13 = view.findViewById(R.id.skill_1_3)
            skl21 = view.findViewById(R.id.skill_2_1)
            skl22 = view.findViewById(R.id.skill_2_2)
            skl23 = view.findViewById(R.id.skill_2_3)
            skl31 = view.findViewById(R.id.skill_3_1)
            skl32 = view.findViewById(R.id.skill_3_2)
            skl33 = view.findViewById(R.id.skill_3_3)
            skl11.onItemSelectedListener = (spinnerListener(data, 1))
            skl12.onItemSelectedListener = (spinnerListener(data, 2))
            skl13.onItemSelectedListener = spinnerListener(data, 3)
            skl21.onItemSelectedListener = (spinnerListener(data, 4))
            skl22.onItemSelectedListener = (spinnerListener(data, 5))
            skl23.onItemSelectedListener = spinnerListener(data, 6)
            skl31.onItemSelectedListener = (spinnerListener(data, 7))
            skl32.onItemSelectedListener = (spinnerListener(data, 8))
            skl33.onItemSelectedListener = (spinnerListener(data, 9))
            skl11.setSelection(data[1].toInt())
            skl12.setSelection(data[2].toInt())
            skl13.setSelection(data[3].toInt())
            skl21.setSelection(data[4].toInt())
            skl22.setSelection(data[5].toInt())
            skl23.setSelection(data[6].toInt())
            skl31.setSelection(data[7].toInt())
            skl32.setSelection(data[8].toInt())
            skl33.setSelection(data[9].toInt())
        }
    }

    class CraftSkillVH(v: View) : FGOViewHolder(v) {
        private lateinit var skl1: Spinner
        private lateinit var skl2: Spinner
        private lateinit var skl3: Spinner
        private lateinit var sklX: Spinner
        override fun onBind(
            order: Updater,
            position: Int,
            Data: Vector<Vector<String>>
        ) {
            super.onBind(order, position, Data)
            val data = Data[position]
            skl1 = view.findViewById(R.id.skill_1_1)
            skl2 = view.findViewById(R.id.skill_1_2)
            skl3 = view.findViewById(R.id.skill_1_3)
            sklX = view.findViewById(R.id.skill_2_1)
            skl1.onItemSelectedListener = (spinnerListener(data, 1))
            skl2.onItemSelectedListener = (spinnerListener(data, 2))
            skl3.onItemSelectedListener = (spinnerListener(data, 3))
            sklX.onItemSelectedListener = (spinnerListener(data, 4))
            skl1.setSelection(data[1].toInt())
            skl2.setSelection(data[2].toInt())
            skl3.setSelection(data[3].toInt())
            sklX.setSelection(data[4].toInt())
        }
    }

    class NoblePhantasmsVH(v: View) : FGOViewHolder(v) {
        private lateinit var noble1: SwitchCompat
        private lateinit var noble2: SwitchCompat
        private lateinit var noble3: SwitchCompat
        override fun onBind(
            order: Updater,
            position: Int,
            Data: Vector<Vector<String>>
        ) {
            super.onBind(order, position, Data)
            val data = Data[position]
            noble1 = view.findViewById(R.id.switch1)
            noble1.setOnCheckedChangeListener(getOnCheckedChange(data, 1))
            noble1.isChecked = data[1] == "1"
            noble2 = view.findViewById(R.id.switch2)
            noble2.setOnCheckedChangeListener(getOnCheckedChange(data, 2))
            noble2.isChecked = data[2] == "1"
            noble3 = view.findViewById(R.id.switch3)
            noble3.setOnCheckedChangeListener(getOnCheckedChange(data, 3))
            noble3.isChecked = data[3] == "1"
        }
    }

    class EndVH(v: View) : FGOViewHolder(v) {
        override fun onBind(
            order: Updater,
            position: Int,
            Data: Vector<Vector<String>>
        ) {
        }
    }

    companion object {
        protected fun getOnCheckedChange(
            data: Vector<String>,
            index: Int
        ): CompoundButton.OnCheckedChangeListener {
            return CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    data[index] = "1"
                } else {
                    data[index] = "0"
                }
            }
        }

        protected fun setRawSpinner(spn: Spinner?, data: Vector<String>, index: Int) {
            spn!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    data[index] = parent.selectedItem.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            for (i in 0 until spn.count) {
                if (data[index] == spn.getItemAtPosition(i)) {
                    spn.setSelection(i)
                }
            }
        }

        protected fun spinnerListener(
            data: Vector<String>,
            index: Int
        ): AdapterView.OnItemSelectedListener {
            return object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    data[index] = position.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        protected fun getTextWatcher(data: Vector<String>, index: Int): TextWatcher {
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
                    data.setElementAt(s.toString(), index)
                }
            }
        }
    }
}