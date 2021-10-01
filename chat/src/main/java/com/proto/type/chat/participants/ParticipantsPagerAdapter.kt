package com.proto.type.chat.participants

import AdminsListingAdapter
import ParticipantsListingAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.page_item_participants.view.*

class ParticipantsPagerAdapter(
    private val adminsListAdapter: AdminsListingAdapter,
    private val participantsListAdapter: ParticipantsListingAdapter,
    private val pages: Array<String>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.page_item_participants, container, false)
        view.recyclerViewParticipants.adapter = if (position == 0) participantsListAdapter else adminsListAdapter
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj

    override fun getCount(): Int = pages.size

    override fun getPageTitle(position: Int): CharSequence = pages[position]

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}