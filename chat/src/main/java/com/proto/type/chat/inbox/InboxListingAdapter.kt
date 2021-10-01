package com.proto.type.chat.inbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.*
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.extension.*
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.chat.R
import com.daimajia.swipe.SwipeLayout
import kotlinx.android.synthetic.main.item_inbox.view.*

class InboxListingAdapter(
    val context: Context,
    private var disableSwipe: Boolean,
    private var isBasicView: Boolean, // Removes left and right margin; remove read, bots, pinned, notification icons
    private val onItemSelected: (action: Int, chat: ChatModel, position: Int) -> Unit): PagedListAdapter<ChatModel, InboxListingAdapter.ViewHolder>(ChatDiffCallback()) {

    private var curHolder: ViewHolder? = null

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
        val item = getItem(position)
        val itemView = holder.itemView

        itemView.civAvatar.loadAvatar(item!!)
        itemView.tvName.text = item.name
        itemView.tvDate.text = item.last_message?.sent_ts?.let { DateTimeUtils.timeAgoDisplay(itemView.context, it) }

        itemView.ivImageIcon.hideView()
        val lastMessage = item.last_message?.data
        when (lastMessage?.type) {
            MessageDataType.Text -> {
                itemView.tvSubTitle.text = item.last_message?.data?.value
                itemView.ivImageIcon.hideView()
            }
            MessageDataType.Image -> {
                itemView.tvSubTitle.text = itemView.context.getString(R.string.image)
                itemView.ivImageIcon.showView()
                itemView.ivImageIcon.setImage(R.drawable.ic_image)
            }
            MessageDataType.Chart -> {
                itemView.tvSubTitle.text = itemView.context.getString(R.string.chart)
                itemView.ivImageIcon.showView()
                itemView.ivImageIcon.setImage(R.drawable.ic_chart)
            }
        }

        // Logic for item is pinned
        setInboxPin(itemView, item)
        holder.itemView.ivLeftPin.setOnClickListener {
            holder.itemView.inboxSwipe.close()
            item.is_pinned = !item.is_pinned

            onItemSelected(Actions.ACTION_PIN, item, position)

            setInboxPin(itemView, item)
        }

        // Logic for item is mute
        setInboxMute(itemView, item)
        holder.itemView.ivLeftMute.setOnClickListener {
            holder.itemView.inboxSwipe.close()
            item.mute_notification = !item.mute_notification
            onItemSelected(Actions.ACTION_MUTE, item, position)
            setInboxMute(itemView, item)
        }

        if (item.unread_count > 0) {
            itemView.ivUnread.showView()
        } else {
            itemView.ivUnread.invisibleView()
        }

        if (item.unread_bot_count > 0) {
            itemView.ivUnreadBot.showView()
        } else {
            itemView.ivUnreadBot.invisibleView()
        }

        holder.itemView.ivRightLeave.setOnClickListener {
            holder.itemView.inboxSwipe.close()
            onItemSelected(Actions.ACTION_LEAVE, item, position)
        }

        holder.itemView.clMain.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, item, position)
            holder.itemView.inboxSwipe.close()
        }

        holder.itemView.inboxSwipe.addSwipeListener(object : SwipeLayout.SwipeListener {
            override fun onClose(layout: SwipeLayout) {}

            override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}

            override fun onStartOpen(layout: SwipeLayout) {}

            override fun onOpen(layout: SwipeLayout) {}

            override fun onStartClose(layout: SwipeLayout) {}

            override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {
                curHolder?.itemView?.inboxSwipe?.close()
                curHolder = holder
            }
        })

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

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_PIN = 0
        const val ACTION_MUTE = 1
        const val ACTION_LEAVE = 2
        const val ACTION_SELECT = 3
    }

    private fun setInboxPin(v: View, item: ChatModel) {
        if (item.is_pinned) {
            v.ivLeftPin.setImageDrawable(ContextCompat.getDrawable(v.context, R.drawable.ic_inbox_unpin))
            v.ivPin.visibility = View.VISIBLE

        } else {
            v.ivLeftPin.setImageDrawable(ContextCompat.getDrawable(v.context, R.drawable.ic_inbox_pin))
            v.ivPin.visibility = View.GONE
        }
    }

    private fun setInboxMute(v: View, item: ChatModel) {
        if (item.mute_notification) {
            v.ivLeftMute.setImageDrawable(ContextCompat.getDrawable(v.context, R.drawable.ic_inbox_unmute))
            v.ivMute.visibility = View.VISIBLE
        } else {
            v.ivLeftMute.setImageDrawable(ContextCompat.getDrawable(v.context, R.drawable.ic_inbox_mute))
            v.ivMute.visibility = View.GONE
        }
    }

//    fun removeItem(position: Int) {
//        try {
//            notifyItemRangeRemoved(position, 1)
//            notifyDataSetChanged()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    class ChatDiffCallback: DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }
    }
}