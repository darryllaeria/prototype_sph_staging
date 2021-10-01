package com.proto.type.chat.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.*
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.extension.setImage
import com.proto.type.base.extension.showView
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.chat.R
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import kotlinx.android.synthetic.main.item_inbox.view.*

class SearchChatGroupAdapter(
    val context: Context,
    private var chatList: MutableList<ChatModel>,
    private var disableSwipe: Boolean,
    private var isBasicView: Boolean, // Removes left and right margin; remove read, bots, pinned, notification icons
    val onItemSelected: (action: Int, chat: ChatModel, position: Int) -> Unit
): RecyclerSwipeAdapter<SearchChatGroupAdapter.ViewHolder>() {

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.inboxSwipe
    }

    fun setData(list: MutableList<ChatModel>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_inbox, parent, false)

        if (disableSwipe) {
            v.inboxSwipe.isLeftSwipeEnabled = false
            v.inboxSwipe.isRightSwipeEnabled = false
        } else {
            v.inboxSwipe.isClickToClose = true
            v.inboxSwipe.addDrag(SwipeLayout.DragEdge.Right, v.inboxSwipe.inboxBottomRightLayer)
            v.inboxSwipe.addDrag(SwipeLayout.DragEdge.Left, v.inboxSwipe.inboxBottomLeftLayer)
        }

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chatList[position]
        val itemView = holder.itemView

        itemView.clMain.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, item, position)
        }

        itemView.civAvatar.loadAvatar(item)
        itemView.tvName.text = item.name
        itemView.tvDate.text = item.last_message?.sent_ts?.let { DateTimeUtils.timeAgoDisplay(context, it) }

        itemView.ivImageIcon.hideView()
        val lastMessage = item.last_message?.data
        when (lastMessage?.type) {
            MessageDataType.Text -> {
                itemView.tvSubTitle.text = item.last_message?.data?.value
                itemView.ivImageIcon.hideView()
            }
            MessageDataType.Image -> {
                itemView.tvSubTitle.text = context.getString(R.string.image)
                itemView.ivImageIcon.showView()
                itemView.ivImageIcon.setImage(R.drawable.ic_image)
            }
            MessageDataType.Chart -> {
                itemView.tvSubTitle.text = context.getString(R.string.chart)
                itemView.ivImageIcon.showView()
                itemView.ivImageIcon.setImage(R.drawable.ic_chart)
            }
        }

        if (isBasicView) {
            val avatarParams = holder.itemView.civAvatar.layoutParams as ConstraintLayout.LayoutParams
            avatarParams.marginStart = 0
            holder.itemView.civAvatar.layoutParams = avatarParams

            val dateParams = holder.itemView.tvDate.layoutParams as ConstraintLayout.LayoutParams
            dateParams.marginEnd = 0
            holder.itemView.tvDate.layoutParams = dateParams

            itemView.ivUnreadBot.hideView()
            itemView.ivUnread.hideView()
            itemView.ivPin.hideView()
            itemView.ivMute.hideView()
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}