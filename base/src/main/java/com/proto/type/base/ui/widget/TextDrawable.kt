package com.proto.type.base.ui.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.emoji.text.EmojiCompat
import com.proto.type.base.R

class TextDrawable(view: ImageView, private val mText: CharSequence) : Drawable() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mIntrinsicWidth: Int
    private val mIntrinsicHeight: Int
    private val bgBitmap: Bitmap?
    private val resource = view.context.resources

    private val mapper = hashMapOf("a_e" to 'a'..'e',
                                "f_j" to 'f'..'j',
                                "k_o" to 'k'..'o',
                                "p_t" to 'p'..'t',
                                "u_y" to 'u'..'y',
                                "z_z" to 'z'..'z')

    init {
        mPaint.color = DEFAULT_COLOR
        mPaint.textAlign = Paint.Align.CENTER

        bgPaint.isFilterBitmap = true
        bgPaint.isDither = true

        val textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE.toFloat(), resource.displayMetrics
        )
        var id = R.drawable.ic_avatar_z_z
        val packageName = resource.getResourcePackageName(id)
        mapper.forEach {
            if (it.value.contains(mText[0].toLowerCase())) {
                id = resource.getIdentifier("ic_avatar_${ it.key }", "drawable", packageName)
            }
        }

        bgBitmap = resource.getDrawable(id).toBitmap()

        mPaint.textSize = textSize
        mIntrinsicWidth = ((mPaint.measureText("M", 0, 1) + .5).toInt()) * 5
        mIntrinsicHeight = mPaint.getFontMetricsInt(null) * 3
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bgBitmap, bounds, bounds, bgPaint)
        canvas.drawText(
            EmojiCompat.get().process(mText).toString().toUpperCase(),
//            (mText as String).toUpperCase(),
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat() + (bounds.centerY().toFloat() * .20f),
            mPaint
        )
    }

    override fun getOpacity(): Int {
        return mPaint.alpha
    }

    override fun getIntrinsicWidth(): Int {
        return mIntrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mIntrinsicHeight
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(filter: ColorFilter) {
        mPaint.colorFilter = filter
    }

    companion object {
        private const val DEFAULT_COLOR = Color.WHITE
        private const val DEFAULT_TEXT_SIZE = 25
    }
}