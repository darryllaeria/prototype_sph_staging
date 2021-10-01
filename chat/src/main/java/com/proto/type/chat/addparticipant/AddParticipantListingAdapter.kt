package com.proto.type.chat.addparticipant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_add_participants.view.*

class AddParticipantListingAdapter(
    val context: Context,
    private val usersList: MutableList<UserModel>,
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
): RecyclerView.Adapter<AddParticipantListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<UserModel>) {
        usersList.clear()
        usersList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_add_participants, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usersList[position]
        val itemView = holder.itemView

        itemView.civAvatar.loadAvatar(item)
        itemView.tvParticipantName.text = item.displayingName()

        // TODO("Parse value for last_online_ts")
        if (item.last_online_ts == 0.0) {
            itemView.tvParticipantLastSeen.text = context.getString(R.string.last_seen_a_long_time_ago)
        } else {
            itemView.tvParticipantLastSeen.text = item.lastOnlineTime(context)
        }

        itemView.cbSelect.isChecked = item.is_selected
        itemView.cbSelect.setOnCheckedChangeListener { _, isChecked ->
            item.is_selected = isChecked
            onItemSelected(Actions.ACTION_SELECT, item, position)
        }

    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_SELECT = 0
    }
}