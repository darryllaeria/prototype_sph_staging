package com.proto.type.base.adapter

import android.view.View
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.Constants
import com.proto.type.base.R
import com.proto.type.base.data.model.ContactModel
import com.proto.type.base.extension.getInitials
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.base.utils.AppLog
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(private val callback: ((ContactModel) -> Unit)? = null): ChatQRecyclerView.ChatQAdapter<ContactModel, ContactAdapter.ContactHolder>() {

    override var layoutItemId = R.layout.item_contact

    override fun createHolder(view: View) = ContactHolder(view)

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.bindView(items[position])
    }

    inner class ContactHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindView(contact: ContactModel) {
            try {
                itemView.tvContactAbbr.text = contact.local_name?.getInitials(Constants.AVATAR_INITIALS_DEFAULT)
            } catch (e: Exception) {
                AppLog.d("Contact name failed to process with exception: $e")
                itemView.tvContactAbbr.text = Constants.KEY_CHATQ_USER.getInitials(Constants.AVATAR_INITIALS_DEFAULT)
            }
            itemView.tvFriendName.text = contact.local_name?.let { EmojiCompat.get().process(it) }
            itemView.tvPhoneNumber.text = contact.phone_number
            itemView.setOnClickListener { callback?.invoke(contact) }
        }
    }
}