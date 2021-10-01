package com.proto.type.chat.search

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proto.type.base.Constants
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.item_message.view.*

class SearchMessageAdapter(private val context: Context, private val callback: ((HashMap<String, Any>) -> Unit)? = null): ChatQRecyclerView.ChatQAdapter<HashMap<String, Any>, SearchMessageAdapter.MessageHolder>() {

    override var layoutItemId = R.layout.item_message

    override fun createHolder(view: View) = MessageHolder(view)

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bindView(items[position])
    }

    inner class MessageHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindView(item: HashMap<String, Any>) {
            val roomSize = item[Constants.SEARCH_ROOM_SIZE_KEY].toString()
            val roomName = item[Constants.KEY_ROOM_NAME].toString()
            val roomAvatar = item[Constants.KEY_ROOM_AVATAR].toString()

            itemView.setOnClickListener { callback?.invoke(item) }
            itemView.tvRoomName.text = roomName
            itemView.tvRoomMessage.text = String.format(context.getString(R.string.search_matched_messages), roomSize)

            if (roomAvatar.isEmpty()) {
                itemView.civAvatar.loadAvatar(roomName)
            } else {
                Glide.with(context)
                    .load(roomAvatar)
                    .placeholder(R.drawable.img_placeholder)
                    .into(itemView.civAvatar)
            }
        }
    }
}