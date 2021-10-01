package com.proto.type.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.extension.hideView
import com.proto.type.profile.R

class MuteAdapter(private val data: List<String>): RecyclerView.Adapter<MuteAdapter.MuteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuteHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mute_sound, parent, false)
        return MuteHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MuteHolder, position: Int) {
        holder.bindView(data[position], position == data.size - 1)
    }

    inner class MuteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv: TextView = itemView.findViewById(R.id.tvMute)

        fun bindView(data: String, isLastItem: Boolean) {
            tv.text = data
            if (isLastItem) {
                itemView.findViewById<View>(R.id.divider).hideView()
            }
        }

    }

}