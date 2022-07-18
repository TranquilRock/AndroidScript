package com.example.androidscript.uitemplate

import com.example.androidscript.R
import com.example.androidscript.uitemplate.ButtonAdapter.ButtonViewHolder
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
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