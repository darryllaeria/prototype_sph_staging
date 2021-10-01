package com.proto.type.contact.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.contact.R
import kotlinx.android.synthetic.main.item_people.view.*

class PeopleAdapter: ChatQRecyclerView.ChatQAdapter<UserModel, PeopleAdapter.PeopleViewHolder>() {

    override var layoutItemId = R.layout.item_people

    override fun createHolder(view: View) = PeopleViewHolder(view)

    override fun onBindViewHolder(viewHolder: PeopleViewHolder, position: Int) {
        viewHolder.bindView(items[position])
    }

    inner class PeopleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindView(user: UserModel) {
            itemView.ivAvatar.loadAvatar(user)
            itemView.tvName.text = user.display_name
        }
    }
}