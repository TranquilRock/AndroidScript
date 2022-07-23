package com.example.androidscript.activities.fgo

import com.example.androidscript.util.FileOperation
import com.example.androidscript.R
import com.example.androidscript.uitemplate.BlockAdapter
import com.example.androidscript.activities.fgo.FGOViewHolder.SkillVH
import com.example.androidscript.activities.fgo.FGOViewHolder.NoblePhantasmsVH
import com.example.androidscript.activities.fgo.FGOViewHolder.CraftSkillVH
import com.example.androidscript.util.SpnMaker
import com.example.androidscript.activities.fgo.FGOViewHolder.PreStageVH
import com.example.androidscript.activities.fgo.FGOViewHolder.EndVH
import android.os.*
import android.view.*
import java.lang.RuntimeException
import java.util.*

class FGOBlockAdapter(_data: Vector<Vector<String>>) : BlockAdapter<FGOViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FGOViewHolder {
        val view: View
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.script_skills, parent, false)
                return SkillVH(view.findViewById(R.id.skills))
            }
            1 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.script_noble_phantasms, parent, false)
                return NoblePhantasmsVH(view.findViewById(R.id.noble_phantasms))
            }
            2 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.script_craft_skills, parent, false)
                return CraftSkillVH(view.findViewById(R.id.craft_skills))
            }
            3 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.script_prestage, parent, false)

                val friendList = Vector<String>()
                friendList.add("None")
                friendList.addAll(
                    FileOperation.browseWithoutSuffix(
                        FGOEditor.folderName + "Friend",
                        ".png"
                    )
                )
                SpnMaker.fromStringWithView(R.id.friend, view, friendList)

                val craftList = Vector<String>()
                craftList.add("None")
                craftList.addAll(
                    FileOperation.browseWithoutSuffix(
                        FGOEditor.folderName + "Craft",
                        ".png"
                    )
                )
                SpnMaker.fromStringWithView(R.id.craft, view, craftList)

                return PreStageVH(view.findViewById(R.id.pre_stage))
            }
            4 -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.script_endstage, parent, false)
                return EndVH(view.findViewById(R.id.end_stage))
            }
        }
        throw RuntimeException("Invalid Type")
    }

    override fun onBindViewHolder(holder: FGOViewHolder, position: Int) { //Do nothing
        holder.onBind(onOrderChange, position, data)
    }

    override fun getItemViewType(position: Int): Int {
        return when (data[position][0]) {
            "Skill" -> 0
            "NoblePhantasms" -> 1
            "CraftSkill" -> 2
            "PreStage" -> 3
            "End" -> 4
            else -> {
                throw RuntimeException("Invalid block " + data[position][0])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    init {
        data = _data
        onOrderChange = object : Updater {
            override fun swap(a: Int, b: Int) {
                if (a > 0 && b < data.size - 1 && a < b) {
                    Collections.swap(data, a, b)
                    notifyDataSetChanged()
                }
            }

            override fun delete(a: Int) {
                if (a > 0 && a < data.size - 1) {
                    data.removeAt(a)
                    notifyDataSetChanged()
                }
            }

            override fun insert() {
                notifyDataSetChanged()
            }

            override fun self(index: Int) {
                Handler(Looper.getMainLooper()).post { notifyItemChanged(index) }
            }
        }
    }
}