package com.proto.type.base.utils

import android.os.Handler
import android.view.View
import com.proto.type.base.Constants
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView

object ViewUtils {

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