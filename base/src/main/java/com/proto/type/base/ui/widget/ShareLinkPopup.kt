package com.proto.type.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.base.Constants
import com.proto.type.base.R
import com.proto.type.base.extension.hideWithAnimation
import kotlinx.android.synthetic.main.popup_share_link.view.*

class ShareLinkPopup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    // MARK: - Init Function
    init {
        View.inflate(context, R.layout.popup_share_link, this)
        setOnClickListener {
            hideWithAnimation(Constants.App.VIEW_ANIMATION_DURATION)
        }
    }

    // MARK: - Public Functions
    fun setOnCopyLinkClickListener(callback: () -> Unit) {
        tvCopyLink.setOnClickListener { callback.invoke() }
        hideWithAnimation(Constants.App.VIEW_ANIMATION_DURATION)
    }

    fun setOnShareToChatClickListener(callback: () -> Unit) {
        tvShareToChat.setOnClickListener { callback.invoke() }
        hideWithAnimation(Constants.App.VIEW_ANIMATION_DURATION)
    }
}