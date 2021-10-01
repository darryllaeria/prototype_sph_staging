package com.proto.type.contact.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.contact.R
import kotlinx.android.synthetic.main.view_image_text.view.*

class ImageTextMenuView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var image: Drawable? = null
    private var description: String? = null

    init {
        if (attrs != null) {
            val arr = context.obtainStyledAttributes(attrs, R.styleable.ImageTextMenuView)
            try {
                image = arr.getDrawable(R.styleable.ImageTextMenuView_itmv_image)
                description = arr.getString(R.styleable.ImageTextMenuView_itmv_text)
            } finally {
                arr.recycle()
            }
        }

        View.inflate(context, R.layout.view_image_text, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        ivImage.setImageDrawable(image)
        tvDesc.text = description
    }

    fun addOnItemClickCallback(callback: () -> Unit) {
        ivImage.setOnClickListener {
            callback.invoke()
        }
        tvDesc.setOnClickListener {
            callback.invoke()
        }
    }
}