package com.proto.type.base.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


/**
 * @Details a custom image view with round corners
 * @Author Ranosys Technologies
 * @Date 25-Sep-2019
 */
class CustomRoundedImageView : AppCompatImageView {

    private val clipPath = Path()
    private val rect = RectF()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onDraw(canvas: Canvas) {
        rect.set(0f, 0f, this.width.toFloat(), this.height.toFloat())
        clipPath.addRoundRect(rect,
            radius,
            radius, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }

    companion object {
        var radius = 20.0f
    }
}