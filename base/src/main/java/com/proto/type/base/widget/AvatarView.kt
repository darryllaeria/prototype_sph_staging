package com.proto.type.base.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.proto.type.base.R
import kotlinx.android.synthetic.main.view_avatar.view.*

class AvatarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val DEF_NAME = "CU"
    }

    var background: Int = R.drawable.ic_avatar_unknown
    var name: String = DEF_NAME

    init {
        View.inflate(context, R.layout.view_avatar, this)
        if (attrs != null) {
            var arr: TypedArray? = null
            try {
                arr = context.obtainStyledAttributes(attrs, R.styleable.AvatarView)
                background = arr.getResourceId(R.styleable.AvatarView_av_background, R.drawable.ic_avatar_unknown)
                name = arr.getString(R.styleable.AvatarView_av_text)
            } finally {
                arr?.recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Glide.with(context)
            .load(background)
            .circleCrop()
            .fitCenter()
            .into(ivAvatar)

        tvName.text = name
    }
}