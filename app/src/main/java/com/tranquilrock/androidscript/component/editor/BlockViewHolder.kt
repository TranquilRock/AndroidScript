/** Defines multiple blocks' view holder.
 * BlockViewHolder is inherited by ZeroVH, ..., FiveVH
 * Each VH has its own input list.
 * blockData contains blocks data, where blockData[0] is the title.
 * */
package com.tranquilrock.androidscript.component.editor

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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

class BlockViewHolder(private val view: View, private val blockDef: Array<Any>, private val inputIds: List<Int>) :
    RecyclerView.ViewHolder(
        view
    ) {

//    lateinit var inputIds: List<Int>

     fun onBind(order: Updater, blockData: ArrayList<ArrayList<String>>) {
        for ((index, id) in inputIds.withIndex()) {
            @Suppress("UNCHECKED_CAST") // Block Def must be [String, List, List, ...]
            Input(
                view.findViewById(id),
                blockDef[index + 1] as List<String>,
                blockData,
                this,
                index + 1
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
        view.findViewById<ImageButton>(R.id.btn_del).setOnClickListener {
            order.delete(
                adapterPosition
            )
        }
        view.findViewById<TextView>(R.id.Title).text = blockDef[0] as CharSequence?
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
                    spinner.adapter = ArrayAdapter(
                        view.context,
                        android.R.layout.simple_spinner_item,
                        inputDef.subList(1, inputDef.size)
                    )
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
                    spinner.setSelection(inputDef.indexOf(blockData[blockViewHolder.adapterPosition][index]))
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