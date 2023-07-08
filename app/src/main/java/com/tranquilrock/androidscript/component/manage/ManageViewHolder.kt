package com.tranquilrock.androidscript.component.manage

import android.graphics.Bitmap
import android.view.View
import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView
import com.tranquilrock.androidscript.databinding.ManageResourceBlockBinding
import com.tranquilrock.androidscript.databinding.ManageScriptBlockBinding

abstract class ManageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun display(fileName: String, image: Bitmap?, removeCallback: OnClickListener)

    class ImageViewHolder(private val binding: ManageResourceBlockBinding) :
        ManageViewHolder(binding.root) {
        override fun display(fileName: String, image: Bitmap?, removeCallback: OnClickListener) {
            binding.manageImageTitle.text = fileName
            binding.manageDelete.setOnClickListener(removeCallback)
            binding.manageImage.setImageBitmap(image!!)
        }
    }


    class ScriptViewHolder(private val binding: ManageScriptBlockBinding) :
        ManageViewHolder(binding.root) {
        override fun display(fileName: String, image: Bitmap?, removeCallback: OnClickListener) {
            binding.manageImageTitle.text = fileName
            binding.manageDelete.setOnClickListener(removeCallback)
        }
    }
}