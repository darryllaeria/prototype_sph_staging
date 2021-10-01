package com.proto.type.profile.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.ui.widget.ChatQRecyclerView
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.item_wallpaper.view.*

data class Wallpaper(val drawableResource: Int = 0,
                     val fileResource: String = "")

class WallpaperAdapter(private val callback: (Wallpaper) -> Unit): ChatQRecyclerView.ChatQAdapter<Wallpaper, WallpaperAdapter.WallpaperHolder>() {

    override var layoutItemId = R.layout.item_wallpaper
    private var selectedPos = 0

    override fun createHolder(view: View): WallpaperHolder {
        return WallpaperHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperHolder, position: Int) {
        holder.bindView(items[position], position)
    }

    inner class WallpaperHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindView(wallpaper: Wallpaper, position: Int) {
            val drawable = when {
                wallpaper.fileResource.isNotEmpty() -> {
                    // TODO("Load resource from file")
                }
                wallpaper.drawableResource != 0 -> {
                    wallpaper.drawableResource
                }
                else -> {
                    R.mipmap.ic_chat_background
                }
            }

            Glide.with(itemView.ivBackground)
                .load(drawable)
                .into(itemView.ivBackground)

            if (selectedPos == position) {
                itemView.ivChecked.apply {
                    showView()
                    bringToFront()
                }
            } else {
                itemView.ivChecked.apply {
                    hideView()
                }
            }

            itemView.ivBackground.setOnClickListener {
                val oldPos = selectedPos
                selectedPos = position
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPos)
                callback.invoke(wallpaper)
            }

        }

    }

}