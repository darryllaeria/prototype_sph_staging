package com.proto.type.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.base.R
import kotlinx.android.synthetic.main.view_indexed_text.view.*

class IndexTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var text: String? = null

    init {
        if (attrs != null) {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.IndexTextView)
            try {
                text = arr.getString(R.styleable.IndexTextView_itv_text)
            } finally {
                arr?.recycle()
            }
        }
        View.inflate(context, R.layout.view_indexed_text, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (text != null) {
            tvText.text = text
        }
    }

    fun setText(value: String) {
        tvText.text = value
    }
}