package com.proto.type.profile.adapter

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.profile.R
import java.util.*


data class LanguageMenuItem(val title: String = "",
                            val languageCode: String)

class LanguageAdapter: ChatQRecyclerView.ChatQAdapter<LanguageMenuItem, LanguageAdapter.LanguageHolder>() {

    private var selectedPos: Int = -1

    override var layoutItemId = R.layout.item_settings_option

    override fun createHolder(view: View) = LanguageHolder(view)

    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
        holder.bindView(items[position], position)
    }

    fun setSelectedCode(code: String) {
        val prevPos = selectedPos
        items.forEachIndexed { index, languageMenuItem ->
            if (languageMenuItem.languageCode.toLowerCase(Locale.getDefault()) == code.toLowerCase(Locale.getDefault())) {
                selectedPos = index
            }
        }
        notifyItemChanged(prevPos)
        notifyItemChanged(selectedPos)
    }

    fun getLanguageCode(): String {
        return items[selectedPos].languageCode
    }

    inner class LanguageHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvLanguage = view.findViewById<TextView>(R.id.tvMenuTitle)
        private val checkBox = view.findViewById<CheckBox>(R.id.cbSelected)

        fun bindView(item: LanguageMenuItem, position: Int) {
            tvLanguage.text = item.title
            checkBox.isChecked = position == selectedPos

            checkBox.setOnTouchListener { _, _ ->
                val prevPos = selectedPos
                selectedPos = position
                notifyItemChanged(prevPos)
                notifyItemChanged(selectedPos)
                true
            }
        }
    }
}