package com.proto.type.chat.creategroup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.utils.AppLog
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_create_group_member.view.*

class CreateGroupListingAdapter(
    val context: Context,
    private val usersList: MutableList<UserModel>,
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
): RecyclerView.Adapter<CreateGroupListingAdapter.ViewHolder>() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = CreateGroupListingAdapter::class.java.simpleName
    }

    // MARK: - Object
    object Actions {
        const val ACTION_REMOVE = 0
    }

    // MARK: - Inner class
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    // MARK: - Override Functions
    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usersList[position]
        val itemView = holder.itemView
        itemView.civAvatar.loadAvatar(item)

        if (item.local_name == "") {
            itemView.tvParticipantName.text = item.display_name
        } else {
            itemView.tvParticipantName.text = item.local_name
        }

        itemView.tvParticipantLastSeen.hideView()
        if (item.last_online_ts == 0.0) {
            itemView.tvParticipantLastSeen.text = context.getString(R.string.last_seen_a_long_time_ago)
        } else {
            itemView.tvParticipantLastSeen.text = item.lastOnlineTime(context)
        }

        if (position == 0) {
            itemView.ivDelete.visibility = View.INVISIBLE
        }
        itemView.ivDelete.setOnClickListener {
            onItemSelected(Actions.ACTION_REMOVE, item, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_create_group_member, parent, false)
        return ViewHolder(v)
    }

    // MARK: - Public Functions
    fun removeItem(position: Int) {
        try {
            usersList.removeAt(position)
            notifyItemRangeRemoved(position, 1)
            notifyDataSetChanged()
        } catch (e: Exception) {
            AppLog.d(TAG, "Sign in failed with exception: $e")
            e.printStackTrace()
        }
    }

    fun setData(list: MutableList<UserModel>) {
        usersList.clear()
        usersList.addAll(list)
        notifyDataSetChanged()
    }
}