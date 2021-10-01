package com.proto.type.main.ui.on_boarding

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.proto.type.main.R
import com.proto.type.main.ui.on_boarding.adapter.OnBoardSliderPagerAdapter
import com.proto.type.base.Constants
import com.proto.type.base.extension.dpToPx

/**
 * @Details class to manage the image slider and adaptor
 */

class OnBoardSliderManager(private val slider: ViewPager, private val indicators: LinearLayout) {

    val pageData = listOf(
        SlideData(
            R.raw.onboarding1,
            R.string.on_board_title_first,
            R.string.on_board_sub_title_first
        ),
        SlideData(
            R.raw.onboarding2,
            R.string.on_board_title_second,
            R.string.on_board_sub_title_second
        ),
        SlideData(
            R.raw.onboarding3,
            R.string.on_board_title_third,
            R.string.on_board_sub_title_third
        )
    )

    init {
        addDotsToSlider(0, pageData.size)
        val sliderPagerAdapter = OnBoardSliderPagerAdapter(pageData)
        slider.adapter = sliderPagerAdapter
        slider.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // empty overridden method
            }

            override fun onPageSelected(position: Int) {
                Handler().postDelayed({
                    addDotsToSlider(
                        position,
                        pageData.size
                    )
                }, Constants.ANIMATE_DOT)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // empty overridden method
            }
        })
    }

    private fun addDotsToSlider(currentPage: Int, size: Int) {
        if (size > 1) {
            val dots = arrayOfNulls<View>(size)
            val context = indicators.context
            val dotSize = context.dpToPx(8f)
            val params = ViewGroup.MarginLayoutParams(dotSize, dotSize).apply {
                marginStart = dotSize / 2
                marginEnd = dotSize / 2
            }

            indicators.removeAllViews()
            for (i in dots.indices) {
                dots[i] = View(context)
                dots[i]!!.layoutParams = params
                dots[i]!!.setBackgroundResource(R.drawable.bg_dot_selector)
                indicators.addView(dots[i])
            }

            if (dots.isNotEmpty()) {
                dots[currentPage]!!.isSelected = true
            }
        }
    }

    inner class SlideData(val icon: Int, val title: Int, val subTitle: Int)

}