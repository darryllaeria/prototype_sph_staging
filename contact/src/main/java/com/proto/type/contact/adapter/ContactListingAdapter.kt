package com.proto.type.contact.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.R
import com.proto.type.base.ui.widget.CircularImageView

class ContactListingAdapter(
    val context: Context,
    private var userList: MutableList<UserModel>,
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
): RecyclerView.Adapter<ContactListingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
         val civAvatar: CircularImageView = itemView.findViewById(R.id.civAvatar)
         val tvFriendName: TextView = itemView.findViewById(R.id.tvFriendName)
         val tvLastSeen: TextView = itemView.findViewById(R.id.tvPhoneNumber)
         val ivOnlineIndicator: ImageView = itemView.findViewById(R.id.ivOnlineIndicator)
    }

    object Actions {
        const val ACTION_SELECT = 0
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]
        val itemView = holder.itemView

        Glide.with(context)
            .load(item.avatar?.url)
            .centerCrop()
            .placeholder(R.drawable.ic_group)
            .into(holder.civAvatar)

        holder.tvFriendName.text = item.local_name

        if (item.last_online_ts == 0.0) {
            holder.tvLastSeen.text = context.getString(R.string.last_seen_a_long_time_ago)
        } else {
            holder.tvLastSeen.text = item.lastOnlineTime(context)
        }

        itemView.setOnClickListener {
            onItemSelected(Actions.ACTION_SELECT, item, position)
        }
    }

    fun setData(list: MutableList<UserModel>) {
        userList.clear()
        userList.addAll(list)
        notifyDataSetChanged()
    }
}