package com.proto.type.chat.chat

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.TextSuggestion
import com.proto.type.base.extension.inflate
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.item_text_suggestion.view.*

class TextSuggestionListingAdapter(
    val context: Context,
    private val textSuggestions: MutableList<TextSuggestion> = mutableListOf(),
    val onItemSelected: (action: Int, user: TextSuggestion, position: Int) -> Unit
) : RecyclerView.Adapter<TextSuggestionListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<TextSuggestion>) {
        textSuggestions.clear()
        textSuggestions.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.item_text_suggestion, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = textSuggestions[position]
        val itemView = holder.itemView
        itemView.tvName.text = suggestion.name
        itemView.tvId.text = suggestion.id
        itemView.clItemTextSuggestion.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, suggestion, position)
        }
    }

    override fun getItemCount() = textSuggestions.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}