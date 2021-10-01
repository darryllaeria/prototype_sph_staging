package com.proto.type.profile.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.profile.R


data class SoundMenuItem(val title: String = "")

class SoundAdapter: ChatQRecyclerView.ChatQAdapter<SoundMenuItem, SoundAdapter.SoundHolder>() {

    private var selectedPos = -1

    override var layoutItemId = R.layout.item_settings_option

    override fun createHolder(view: View) = SoundHolder(view)

    override fun onBindViewHolder(holder: SoundHolder, position: Int) {
        holder.bindView(items[position], position)
    }

    fun setSelectedSound(sound: SoundMenuItem) {
        val prevPos = selectedPos
        items.forEachIndexed { index, item ->
            if (item.title == sound.title) {
                selectedPos = index
            }
        }
        notifyItemChanged(prevPos)
        notifyItemChanged(selectedPos)
    }

    fun getSelectedSound(): String {
        return items[selectedPos].title
    }

    inner class SoundHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvMenu = view.findViewById<TextView>(R.id.tvMenuTitle)
        private val checkbox = view.findViewById<CheckBox>(R.id.cbSelected)

        fun bindView(item: SoundMenuItem, position: Int) {
            tvMenu.text = item.title
            checkbox.isChecked = position == selectedPos

            checkbox.setOnTouchListener { _, _ ->
                val prevPos = selectedPos
                selectedPos = position
                notifyItemChanged(prevPos)
                notifyItemChanged(selectedPos)
                true
            }
        }

    }
}