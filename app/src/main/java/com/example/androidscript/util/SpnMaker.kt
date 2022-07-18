package com.example.androidscript.util

import androidx.appcompat.app.AppCompatActivity
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.view.*

object SpnMaker {
    fun fromStringWithView(id: Int, view: View, content: List<String>): Spinner {
        val spn = view.findViewById<Spinner>(id)
        val adp = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, content)
        spn.adapter = adp
        return spn
    }

    fun fromStringWithActivity(
        id: Int,
        activity: AppCompatActivity,
        content: List<String>
    ): Spinner {
        val spn = activity.findViewById<Spinner>(id)
        val adp = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, content)
        spn.adapter = adp
        return spn
    }
}