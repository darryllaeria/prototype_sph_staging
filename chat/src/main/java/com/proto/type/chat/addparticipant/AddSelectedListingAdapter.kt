package com.proto.type.chat.addparticipant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.list_item_selected_user.view.*

class AddSelectedListingAdapter(
    val context: Context,
    private val usersList: MutableList<UserModel>,
    val onItemSelected: (action: Int, user: UserModel, position: Int) -> Unit
): RecyclerView.Adapter<AddSelectedListingAdapter.ViewHolder>() {

    fun setData(list: MutableList<UserModel>) {
        usersList.clear()
        usersList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        usersList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_selected_user, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = usersList[position]
        val itemView = holder.itemView

        itemView.ivAvatar.loadAvatar(item)
        itemView.tvSelectName.text = item.displayingName()
        itemView.setOnClickListener {
            onItemSelected(Actions.ACTION_REMOVE, item, position)
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    object Actions {
        const val ACTION_REMOVE = 0
    }
}