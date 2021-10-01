package com.proto.type.chat.forward

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.page_item_chat_forward.view.*

class ForwardChatPagerAdapter(private val singleChatAdapter: ForwardChatAdapter,
                              private val groupChatAdapter: ForwardChatAdapter,
                              private val pages: Array<String>): PagerAdapter() {

    // MARK: - Override Functions
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    override fun getCount(): Int = pages.size

    override fun getPageTitle(position: Int): CharSequence = pages[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.page_item_chat_forward, container, false)
        view.rvChatList.adapter = if (position == 0) singleChatAdapter else groupChatAdapter
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj
}