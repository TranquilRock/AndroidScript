/** Defines multiple blocks' view holder.
 * BlockViewHolder is inherited by ZeroVH, ..., FiveVH
 * Each VH has its own input list.
 * blockData contains blocks data, where blockData[0] is the title.
 * */
package com.tranquilrock.androidscript.component.editor

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.R


class BlockViewHolder(
    private val view: View,
    private val blockName: String,
    private val blockDef: List<List<String>>,
    private val inputIds: List<Int>
) : RecyclerView.ViewHolder(
    view
) {
    // TODO, this should be done in adapter?
    // TODO view binding
    fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        for ((index, id) in inputIds.withIndex()) {
            Input(
                view.findViewById(id), blockDef[index], blockData, this, index + 1
                // The index of BlockData should be offset by one, as the first is block type number.
            )
        }
        view.findViewById<ImageButton>(R.id.btn_up).setOnClickListener {
            order.swap(
                adapterPosition - 1, adapterPosition
            )
        }
        view.findViewById<ImageButton>(R.id.btn_down).setOnClickListener {
            order.swap(
                adapterPosition, adapterPosition + 1
            )
        }
        view.findViewById<ImageButton>(R.id.manage_delete).setOnClickListener {
            order.delete(
                adapterPosition
            )
        }
        view.findViewById<TextView>(R.id.Title).text = blockName
    }

    class Input(
        val view: View,
        inputDef: List<String>,
        blockData: ArrayList<ArrayList<String>>,
        blockViewHolder: BlockViewHolder,
        index: Int
    ) {

        private val spinner = view.findViewById<Spinner>(R.id.input_spinner)
        private val editText = view.findViewById<EditText>(R.id.input_edit_text)
        private val toggleButton = view.findViewById<ToggleButton>(R.id.input_toggle_button)

        init {
            spinner.visibility = GONE
            editText.visibility = GONE
            toggleButton.visibility = GONE
            when (inputDef[0]) {
                "Spinner" -> { // TODO replace data with file list?
                    if (inputDef.size < 2) Log.e(
                        TAG, "Spinner def error, ${inputDef.joinToString { " " }}"
                    )

                    spinner.visibility = VISIBLE
                    spinner.adapter = object : ArrayAdapter<String>(
                        view.context,
                        android.R.layout.simple_spinner_item,
                        inputDef.subList(1, inputDef.size)
                    ) {
                        override fun isEnabled(position: Int) = position > 0
                        override fun getDropDownView(
                            position: Int, convertView: View?, parent: ViewGroup
                        ): View {
                            val view = super.getDropDownView(position, convertView, parent)
                            (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                            return view
                        }
                    }

                    spinner.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {
                            blockData[blockViewHolder.adapterPosition][index] =
                                parent!!.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            blockData[blockViewHolder.adapterPosition][index] = ""
                        }
                    }

                    if (blockData[blockViewHolder.adapterPosition][index] !in inputDef) {
                        blockData[blockViewHolder.adapterPosition][index] = inputDef[1]
                    }
                    spinner.setSelection(inputDef.indexOf(blockData[blockViewHolder.adapterPosition][index]) - 1)
                }

                "EditText" -> {
                    if (inputDef.size != 2) Log.e(
                        TAG, "Edittext def error, ${inputDef.joinToString { " " }}"
                    )

                    editText.visibility = VISIBLE
                    editText.hint = inputDef[1]
                    editText.doAfterTextChanged {
                        val clean = regex.replace(it.toString(), "")
                        blockData[blockViewHolder.adapterPosition][index] = clean
                    }

                    blockData[blockViewHolder.adapterPosition][index] =
                        regex.replace(blockData[blockViewHolder.adapterPosition][index], "")
                    editText.setText(blockData[blockViewHolder.adapterPosition][index])
                }

                "ToggleButton" -> {
                    if (inputDef.size != 3) Log.e(
                        TAG, "Spinner def error, ${inputDef.joinToString { " " }}"
                    )

                    toggleButton.visibility = VISIBLE
                    toggleButton.textOn = inputDef[1]
                    toggleButton.textOff = inputDef[2]
                    toggleButton.setOnCheckedChangeListener { buttonView, _ ->
                        Log.d(TAG, "Toggle ${buttonView.text}")
                        blockData[blockViewHolder.adapterPosition][index] =
                            buttonView.text as String
                    }

                    if (blockData[blockViewHolder.adapterPosition][index] != inputDef[1] || blockData[blockViewHolder.adapterPosition][index] != inputDef[2]) {
                        blockData[blockViewHolder.adapterPosition][index] = inputDef[1]
                    }
                    toggleButton.isChecked =
                        blockData[blockViewHolder.adapterPosition][index] == inputDef[1]
                }

                else -> Log.e(TAG, "Unrecognized block ${inputDef.joinToString(" ")}")
            }
        }
    }

    companion object {
        private val regex = Regex("[^A-Za-z0-9]")
        val TAG: String = BlockViewHolder::class.java.simpleName
    }
}