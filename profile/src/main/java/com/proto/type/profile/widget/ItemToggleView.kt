package com.proto.type.profile.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getStringOrThrow
import com.proto.type.base.SuccessCallback
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.view_item_toggle.view.*

class ItemToggleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var key: String
    private var description: String? = null

    init {
        var arr = context.obtainStyledAttributes(attrs, R.styleable.ItemToggleView)
        try {
            key = arr.getStringOrThrow(R.styleable.ItemToggleView_itv_key)
            description = arr.getString(R.styleable.ItemToggleView_itv_desc)
        } finally {
            arr?.recycle()
        }

        View.inflate(context, R.layout.view_item_toggle, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        tvTitle.text = key
        if (description == null) {
            tvDesc.hideView()
        } else {
            tvDesc.apply {
                showView()
                text = description
            }
        }
    }

    fun subscribeOnCheckChanged(callback: SuccessCallback) {
        toggleValue.setOnClickListener {
            callback.invoke(toggleValue.isChecked)
        }
    }

    fun setChecked(isChecked: Boolean) {
        toggleValue.isChecked = isChecked
    }
}