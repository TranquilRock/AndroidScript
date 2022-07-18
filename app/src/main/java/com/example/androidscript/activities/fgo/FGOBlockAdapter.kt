package com.example.androidscript.activities.fgo
import com.example.androidscript.util.DebugMessage
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
                var tmp = FileOperation.browseWithoutSuffix(
                    FGOEditor.folderName + "Friend",
                    ".png"
                )
                tmp.add(0, "None")
                SpnMaker.fromStringWithView(R.id.friend, view, tmp)
                tmp = FileOperation.browseWithoutSuffix(
                    FGOEditor.folderName + "Craft",
                    ".png"
                )
                tmp.add(0, "None")
                SpnMaker.fromStringWithView(R.id.craft, view, tmp)
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
        when (data[position][0]) {
            "Skill" -> return 0
            "NoblePhantasms" -> return 1
            "CraftSkill" -> return 2
            "PreStage" -> return 3
            "End" -> return 4
        }
        throw RuntimeException("Invalid Type " + data[position][0])
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
                try {
                    Handler(Looper.getMainLooper()).post { notifyItemChanged(index) }
                } catch (e: Throwable) {
                    DebugMessage.set("PreStageGG")
                }
            }
        }
    }
}