package com.proto.type.chat.sharedmedia

import LinksListingAdapter
import MediaListingAdapter
import PaginationGridScrollListener
import PaginationLinearScrollListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.proto.type.base.extension.dpToPx
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.page_item_link.view.*
import kotlinx.android.synthetic.main.page_item_media.view.*

class SharedMediaPagerAdapter(
    private val linksListingAdapter: LinksListingAdapter,
    private val mediaListingAdapter: MediaListingAdapter,
    private val pages: Array<String>,
    val onLoadMoreMediaItems: () -> Unit,
    val onLoadMoreLinkItems: () -> Unit): PagerAdapter() {

    // MARK: - Override Functions
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

    override fun getCount(): Int = pages.size

    override fun getPageTitle(position: Int): CharSequence = pages[position]

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (position == 0) {
            val view = LayoutInflater.from(container.context).inflate(R.layout.page_item_media, container, false)
            view.rvMedia.adapter = mediaListingAdapter
            val manager = GridLayoutManager(container.context, 3) // Show 3 media messages per row
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = if (mediaListingAdapter.isHeader(position)) 3 else 1
            }
            view.rvMedia.layoutManager = manager
            view.rvMedia.addItemDecoration(SpacingGridWithHeaderItemDecoration(container.context.dpToPx(3.5.toFloat()), false))
            view.rvMedia.addOnScrollListener(object : PaginationGridScrollListener(manager) {
                override fun isLastPage(): Boolean {
                    return mediaListingAdapter.isLastPage
                }

                override fun isLoading(): Boolean {
                    return mediaListingAdapter.isLoading
                }

                override fun loadMoreItems() {
                    onLoadMoreMediaItems.invoke()
                }
            })
            container.addView(view)
            return view
        } else {
            val view = LayoutInflater.from(container.context) .inflate(R.layout.page_item_link, container, false)
            view.rvLinks.adapter = linksListingAdapter
            (view.rvLinks.layoutManager as? LinearLayoutManager)?.let {
                view.rvLinks.addOnScrollListener(object : PaginationLinearScrollListener(it) {
                    override fun isLastPage(): Boolean {
                        return linksListingAdapter.isLastPage
                    }

                    override fun isLoading(): Boolean {
                        return linksListingAdapter.isLoading
                    }

                    override fun loadMoreItems() {
                        onLoadMoreLinkItems.invoke()
                    }
                })
            }
            container.addView(view)
            return view
        }
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj
}