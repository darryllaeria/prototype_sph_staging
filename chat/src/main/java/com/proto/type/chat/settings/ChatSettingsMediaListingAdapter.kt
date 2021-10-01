package com.proto.type.chat.settings

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.proto.type.base.data.model.MessageMediaItemModel
import com.proto.type.base.extension.dpToPx
import com.proto.type.base.extension.inflate
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.grid_item_shared_media.view.*

class ChatSettingsMediaListingAdapter(
    val context: Context,
    private val mediasList: MutableList<MessageMediaItemModel> = mutableListOf(),
    val onItemSelected: (action: Int, user: MessageMediaItemModel, position: Int) -> Unit
) : RecyclerView.Adapter<ChatSettingsMediaListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<MessageMediaItemModel>) {
        while (list.size < 5) { list.add(MessageMediaItemModel()) } // Always show 5 items
        mediasList.clear()
        mediasList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.grid_item_shared_media, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val media = mediasList[position]
        val itemView = holder.itemView
        val context = itemView.ivMediaItem.context
        Glide.with(context)
            .load(media.data.url)
            .fitCenter()
            .transform(CenterCrop(), RoundedCorners(context.dpToPx(4.toFloat())))
            .into(itemView.ivMediaItem)
        itemView.ivMediaItem.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, media, position)
        }
    }

    override fun getItemCount() = mediasList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}