package com.proto.type.base.adapter

import android.telephony.PhoneNumberUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.R
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.ui.widget.ChatQRecyclerView
import kotlinx.android.synthetic.main.item_contact.view.*
import java.util.*

class UserAdapter(private val callback: ((UserModel) -> Unit)? = null): ChatQRecyclerView.ChatQAdapter<UserModel, UserAdapter.UserHolder>() {

    fun setData(list: MutableList<UserModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override var layoutItemId = R.layout.item_contact

    override fun createHolder(view: View) = UserHolder(view)

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindView(items[position])
    }

    inner class UserHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(user: UserModel) {
            itemView.setOnClickListener { callback?.invoke(user) }
            itemView.civAvatar.loadAvatar(user)
            itemView.tvFriendName.text = user.displayingName()
            itemView.tvPhoneNumber.text = PhoneNumberUtils.formatNumber(user.phone_number, Locale.getDefault().country)
        }
    }
}