package com.proto.type.chat.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.item_mention_user.view.*

class MentionListingAdapter(
    private val mContext: Context,
    private var mentionList: List<UserModel> = listOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    val userSelectedObservable = MutableLiveData<UserModel>()

    fun setData(list: List<UserModel>) {
        mentionList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_mention_user,
                    parent,
                    false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = mentionList[position]
        holder.itemView.ivAvatar.loadAvatar(user)
        holder.itemView.tvUserName.text = user.display_name
        holder.itemView.tvBio.text = user.bio
        holder.itemView.setOnClickListener {
            userSelectedObservable.postValue(user)
        }
    }

    override fun getItemCount() = mentionList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}