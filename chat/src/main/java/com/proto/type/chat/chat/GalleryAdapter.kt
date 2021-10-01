package com.proto.type.chat.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.item_image_camera.view.*
import kotlinx.android.synthetic.main.item_image_grid.view.*
import kotlinx.android.synthetic.main.item_image_grid.view.ivImage

class GalleryAdapter(
    private val mContext: Context,
    private val imageList: MutableList<String> = mutableListOf(),
    private val onStickCamera: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val selectedItems = mutableListOf<String>()
    val observableItems = MutableLiveData(selectedItems)

    fun setData(list: List<String>) {
        selectedItems.clear()
        imageList.clear()
        imageList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == TYPE_HEADER) HeaderViewHolder(view) else ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            holder.itemView.layoutCameraItem.setOnClickListener {
                onStickCamera.invoke()
            }
        } else {
            val item = imageList[position - 1]
            Glide.with(mContext)
                .load(item)
                .centerCrop()
                .placeholder(R.color.gray)
                .into(holder.itemView.ivImage)

            holder.itemView.layoutImageItem.setOnClickListener {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                    holder.itemView.ivImageSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_image_not_select))
                } else {
                    selectedItems.add(item)
                    holder.itemView.ivImageSelect.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_image_selected))
                }
                observableItems.postValue(selectedItems)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return imageList.size + 1
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val TYPE_HEADER = R.layout.item_image_camera
        private val TYPE_ITEM = R.layout.item_image_grid
    }
}