package com.proto.type.base.ui.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.appcompat.widget.AppCompatImageView
import com.proto.type.base.R
import com.proto.type.base.utils.AppLog
import kotlin.math.min

/**
 * @Details A circular shape image view
 * @Author Ranosys Technologies
 * @Date 11-Sep-2019
 */

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // MARK: - Companion Object
    companion object {
        private val TAG = CircularImageView::class.java.simpleName
        private const val DEFAULT_BORDER_WIDTH = 4f
        private const val DEFAULT_SHADOW_RADIUS = 8.0f
    }

    // MARK: - Enum Class
    /**
     * ShadowGravity enum class to set the gravity of the CircleView shadow
     */
    enum class ShadowGravity(val value: Int) {
        CENTER(1),
        TOP(2),
        BOTTOM(3),
        START(4),
        END(5)
    }

    // MARK: - Private Constants
    private val paint: Paint = Paint().apply { isAntiAlias = true }
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }
    private val paintBackground: Paint = Paint().apply { isAntiAlias = true }
    private var circleCenter = 0
    private var heightCircle: Int = 0

    // MARK: - Private Variables
    private var borderColor: Int = Color.BLACK
        set(value) {
            field = value
            update()
        }
    private var borderWidth: Float = 0f
        set(value) {
            field = value
            update()
        }
    private var circleColor: Int = Color.WHITE
        set(value) {
            field = value
            paintBackground.color = field
            invalidate()
        }
    private var civColorFilter: ColorFilter? = null // Color Filter
        set(value) {
            if (field != value) {
                field = value
                if (field != null) {
                    civDrawable = null // To force re-update shader
                    invalidate()
                }
            }
        }
    private var civDrawable: Drawable? = null // Object used for drawing
    private var civImage: Bitmap? = null // Object used for drawing
    private var shadowColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    private var shadowEnable = false
        set(value) {
            field = value
            if (field && shadowRadius == 0f)
                shadowRadius =
                    DEFAULT_SHADOW_RADIUS
            update()
        }
    private var shadowGravity = ShadowGravity.BOTTOM
        set(value) {
            field = value
            invalidate()
        }
    private var shadowRadius: Float = 0f
        set(value) {
            field = value
            shadowEnable = shadowRadius > 0f
        }

    // MARK: - Override Functions
    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        // Load the styled attributes and set their properties
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyleAttr, 0)

        // Init Background Color
        circleColor =
            attributes.getColor(R.styleable.CircularImageView_civ_circle_color, Color.WHITE)

        // Init Border
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_border, true)) {
            val defaultBorderSize =
                DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
            borderWidth = attributes.getDimension(
                R.styleable.CircularImageView_civ_border_width,
                defaultBorderSize
            )
            borderColor =
                attributes.getColor(
                    R.styleable.CircularImageView_civ_border_color,
                    Color.TRANSPARENT
                )
        }

        // Init Shadow
        shadowEnable = attributes.getBoolean(R.styleable.CircularImageView_civ_shadow, shadowEnable)
        if (shadowEnable) {
            shadowGravity = attributes.getInteger(
                R.styleable.CircularImageView_civ_shadow_gravity,
                shadowGravity.value
            ).toShadowGravity()
            shadowRadius = attributes.getFloat(
                R.styleable.CircularImageView_civ_shadow_radius,
                DEFAULT_SHADOW_RADIUS
            )
            shadowColor =
                attributes.getColor(R.styleable.CircularImageView_civ_shadow_color, shadowColor)
        }

        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        // Load the bitmap
        loadBitmap()

        // Check if civImage isn't null
        if (civImage == null) return

        val circleCenterWithBorder = circleCenter + borderWidth
        val margeWithShadowRadius = if (shadowEnable) shadowRadius * 2 else 0f

        // Draw Shadow
        if (shadowEnable) drawShadow()
        // Draw Border
        canvas.drawCircle(
            circleCenterWithBorder,
            circleCenterWithBorder,
            circleCenterWithBorder - margeWithShadowRadius,
            paintBorder
        )
        // Draw Circle background
        canvas.drawCircle(
            circleCenterWithBorder,
            circleCenterWithBorder,
            circleCenter - margeWithShadowRadius,
            paintBackground
        )
        // Draw CircularImageView
        canvas.drawCircle(
            circleCenterWithBorder,
            circleCenterWithBorder,
            circleCenter - margeWithShadowRadius,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measure(widthMeasureSpec)
        val height = measure(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update()
    }

    override fun getScaleType(): ScaleType =
        super.getScaleType().let { if (it == null || it != CENTER_INSIDE) CENTER_CROP else it }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        civColorFilter = colorFilter
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != CENTER_CROP && scaleType != CENTER_INSIDE) {
            throw IllegalArgumentException(
                String.format(
                    "ScaleType %s not supported. " + "Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.",
                    scaleType
                )
            )
        } else {
            super.setScaleType(scaleType)
        }
    }

    // MARK: - Private Functions
    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
        when (drawable) {
            null -> null
            is BitmapDrawable -> drawable.bitmap
            else -> try {
                // Create Bitmap object out of the drawable
                val bitmap = Bitmap.createBitmap(
                    if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 512,
                    if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 512,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            } catch (e: Exception) {
                AppLog.d(TAG, "Try to draw canvas failed with exception: $e")
                null
            }
        }

    private fun drawShadow() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, paintBorder)
        }
        var dx = 0.0f
        var dy = 0.0f
        when (shadowGravity) {
            ShadowGravity.CENTER -> {
                /*dx, dy = 0.0f*/
            }
            ShadowGravity.TOP -> dy = -shadowRadius / 2
            ShadowGravity.BOTTOM -> dy = shadowRadius / 2
            ShadowGravity.START -> dx = -shadowRadius / 2
            ShadowGravity.END -> dx = shadowRadius / 2
        }
        paintBorder.setShadowLayer(shadowRadius, dx, dy, shadowColor)
    }

    private fun manageElevation() {
        outlineProvider = if (!shadowEnable) object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setOval(0, 0, heightCircle, heightCircle)
            }
        } else {
            null
        }
    }

    private fun loadBitmap() {
        if (civDrawable == drawable) return
        civDrawable = drawable
        civImage = drawableToBitmap(civDrawable)
        updateShader()
    }

    private fun measure(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize // The parent has determined an exact size for the child.
            MeasureSpec.AT_MOST -> specSize // The child can be as large as it wants up to the specified size.
            else -> heightCircle // The parent has not imposed any constraint on the child.
        }
    }

    private fun update() {
        if (civImage != null)
            updateShader()
        val usableWidth = width - (paddingLeft + paddingRight)
        val usableHeight = height - (paddingTop + paddingBottom)
        heightCircle = min(usableWidth, usableHeight)
        circleCenter = (heightCircle - borderWidth * 2).toInt() / 2
        paintBorder.color = if (borderWidth == 0f) circleColor else borderColor
        manageElevation()
        invalidate()
    }

    private fun updateShader() {
        civImage?.also {
            // Create Shader
            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            // Center Image in Shader
            val scale: Float
            val dx: Float
            val dy: Float
            when (scaleType) {
                CENTER_CROP -> if (it.width * height > width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                CENTER_INSIDE -> if (it.width * height < width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                else -> {
                    scale = 0f
                    dx = 0f
                    dy = 0f
                }
            }
            shader.setLocalMatrix(Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            })

            // Set Shader in Paint
            paint.shader = shader

            // Apply colorFilter
            paint.colorFilter = civColorFilter
        }
    }

    // MARK: - Private Int Extension Function
    private fun Int.toShadowGravity(): ShadowGravity =
        when (this) {
            1 -> ShadowGravity.CENTER
            2 -> ShadowGravity.TOP
            3 -> ShadowGravity.BOTTOM
            4 -> ShadowGravity.START
            5 -> ShadowGravity.END
            else -> throw IllegalArgumentException("This value is not supported for ShadowGravity: $this")
        }
}