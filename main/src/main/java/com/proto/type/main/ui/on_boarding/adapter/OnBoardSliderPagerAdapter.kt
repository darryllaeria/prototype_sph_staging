package com.proto.type.main.ui.on_boarding.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.proto.type.main.R
import com.proto.type.main.ui.on_boarding.OnBoardSliderManager
import kotlinx.android.synthetic.main.item_onboard_slider.view.*


/**
 * @Detail Image slider adaptor
 */

class OnBoardSliderPagerAdapter(private var pageData: List<OnBoardSliderManager.SlideData>) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_onboard_slider, container, false)
        view.imSlider.setAnimation(pageData[position].icon)
        view.tvOnBoardTitle.setText(pageData[position].title)
        view.tvOnBoardSubTitle.setText(pageData[position].subTitle)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return pageData.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        container.removeView(view)
    }
}