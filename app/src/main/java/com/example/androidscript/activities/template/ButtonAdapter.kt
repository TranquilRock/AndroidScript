package com.example.androidscript.activities.template

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.androidscript.R
import com.example.androidscript.activities.template.ButtonAdapter.ButtonViewHolder
import java.util.*

abstract class ButtonAdapter(protected var buttonText: Vector<String>) :
    RecyclerView.Adapter<ButtonViewHolder>() {
    class ButtonViewHolder(itemView: View, var button: Button) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.button_item, parent, false)
        return ButtonViewHolder(view, view.findViewById(R.id.button_item))
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.button.text = buttonText[position]
    }

    override fun getItemCount(): Int {
        return buttonText.size
    }
}