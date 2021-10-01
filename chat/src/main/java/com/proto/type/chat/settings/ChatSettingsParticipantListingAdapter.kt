package com.proto.type.chat.settings

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.inflate
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_create_group_member.view.civAvatar
import kotlinx.android.synthetic.main.list_item_user.view.*

class ChatSettingsParticipantListingAdapter(
    val context: Context,
    private val participantsList: MutableList<UserModel> = mutableListOf(),
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
) : RecyclerView.Adapter<ChatSettingsParticipantListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<UserModel>) {
        participantsList.clear()
        participantsList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = parent.inflate(R.layout.list_item_user, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = participantsList[position]
        val itemView = holder.itemView
        itemView.civAvatar.loadAvatar(user)
        itemView.tvUserName.text = user.displayingName()
        if (user.last_online_ts == 0.0) {
            itemView.tvUserLastSeen.text = context.getString(R.string.last_seen_a_long_time_ago)
            itemView.ivOnlineIndicator.visibility = View.GONE
        } else {
            itemView.tvUserLastSeen.text = user.lastOnlineTime(context)
            itemView.ivOnlineIndicator.visibility = View.VISIBLE
        }
        itemView.layoutItemUser.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, user, position)
        }
    }

    override fun getItemCount() = participantsList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}