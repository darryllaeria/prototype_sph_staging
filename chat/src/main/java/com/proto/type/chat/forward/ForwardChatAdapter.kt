package com.proto.type.chat.forward

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.StringCallback
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_forward_chat.view.*

class ForwardChatAdapter(
    private val mContext: Context,
    private val dataItemList: MutableList<ChatModel> = mutableListOf(),
    private var sendToChatCallback: StringCallback? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // MARK: - Override Functions
    override fun getItemCount() = dataItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = dataItemList[position]
        holder.itemView.civAvatar.loadAvatar(chat)
        holder.itemView.tvChatName.text = chat.name

        holder.itemView.btnSendForward.setOnClickListener {
            sendToChatCallback?.invoke(chat.id)
            holder.itemView.btnSendForward.isEnabled = false
            holder.itemView.btnSendForward.text = mContext.getString(R.string.sent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.list_item_forward_chat,
                    parent,
                    false
                )
        )

    // MARK: - Public Function
    fun setData(list: List<ChatModel>) {
        dataItemList.clear()
        dataItemList.addAll(list)
        notifyDataSetChanged()
    }

    // MARK: - Class ItemViewHolder
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}