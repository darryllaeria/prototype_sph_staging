package com.proto.type.base.utils

import android.os.Handler
import android.view.View
import com.proto.type.base.Constants
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView

/**
 * @Details utility class for views and their properties
 * @Author Ranosys Technologies
 * @Date 26-Aug-2019
 */

object ViewUtils {

//    fun changeIconDrawableToGray(context: Context, drawable: Drawable?) {
//        if (drawable != null) {
//            drawable.mutate()
//            drawable.setColorFilter(
//                ContextCompat.getColor(context, R.color.colorAccent),
//                PorterDuff.Mode.SRC_ATOP
//            )
//        }
//    }

    fun disableViewClickTemp(view: View) {
        view.isClickable = false
        Handler().postDelayed({
            view.isClickable = true
        }, Constants.App.CLICK_DISABLE_DURATION)
    }

    fun showViews(vararg views: View) {
        views.forEach {
            it.showView()
        }
    }

    fun hideViews(vararg views: View) {
        views.forEach {
            it.hideView()
        }
    }
}