package com.proto.type.main.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.proto.type.main.R
import kotlinx.android.synthetic.main.view_marked_text.view.*

class MarkedText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var textDesc = ""

    init {
        if (attrs != null) {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.MarkedText)
            try {
                textDesc = arr.getString(R.styleable.MarkedText_mt_description)!!
            } finally {
                arr.recycle()
            }
        }
        View.inflate(context, R.layout.view_marked_text, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvDesc.text = textDesc
    }

    fun markDone(isDone: Boolean) {
        checkbox.isChecked = isDone
    }

    fun isMarked() = checkbox.isChecked
}