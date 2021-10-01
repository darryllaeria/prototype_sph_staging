package com.proto.type.base.ui.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateInterpolator
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.proto.type.base.R
import com.proto.type.base.utils.AppLog
import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.abs

/**
 * @Details Swipe Layout
 * @Author Ranosys Technologies
 * @Date 14-Oct-2019
 */
class SwipeLayout : ViewGroup {

    private var dragHelper: ViewDragHelper? = null
    private var leftView: View? = null
    private var rightView: View? = null
    private var centerView: View? = null
    private var velocityThreshold: Float = 0.toFloat()
    private var touchSlop: Float = 0.toFloat()
    private var swipeListener: OnSwipeListener? = null
    private var weakAnimator: WeakReference<ObjectAnimator>? = null
    private val hackedParents = WeakHashMap<View, Boolean>()
    /**
     * Enable or disable swipe gesture from left side
     */
    private var isLeftSwipeEnabled = true
    /**
     * Enable or disable swipe gesture from right side
     */

    private var isRightSwipeEnabled = true

    private var touchState =
        TOUCH_STATE_WAIT
    private var touchX: Float = 0.toFloat()
    private var touchY: Float = 0.toFloat()

    /**
     * get horizontal offset from initial position
     */
    /**
     * set horizontal offset from initial position
     */
    private var offset: Int
        get() = if (centerView == null) 0 else centerView!!.left
        set(offset) {
            if (centerView != null) {
                offsetChildren(null, offset - centerView!!.left)
            }
        }

    /**
     * enable or disable swipe gesture handling
     */
    private var isSwipeEnabled: Boolean
        get() = isLeftSwipeEnabled || isRightSwipeEnabled
        set(enabled) {
            this.isLeftSwipeEnabled = enabled
            this.isRightSwipeEnabled = enabled
        }

    private val dragCallback = object : ViewDragHelper.Callback() {

        private var initLeft: Int = 0

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            initLeft = child.left
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return if (dx > 0) {
                clampMoveRight(child, left)
            } else {
                clampMoveLeft(child, left)
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return width
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            AppLog.d(TAG, "OnViewReleased with velocity: $xvel; threshold: $velocityThreshold")
            val dx = releasedChild.left - initLeft
            if (dx == 0) return
            val handled: Boolean
            if (dx > 0) {

                handled = if (xvel >= 0) onMoveRightReleased(
                    releasedChild,
                    dx,
                    xvel
                ) else onMoveLeftReleased(releasedChild, dx, xvel)

            } else {

                handled = if (xvel <= 0) onMoveLeftReleased(
                    releasedChild,
                    dx,
                    xvel
                ) else onMoveRightReleased(releasedChild, dx, xvel)
            }

            if (!handled) {
                startScrollAnimation(
                    releasedChild,
                    releasedChild.left - centerView!!.left,
                    false,
                    dx > 0
                )
            }
        }

        private fun leftViewClampReached(leftViewLP: LayoutParams): Boolean {
            if (leftView == null) return false

            return when (leftViewLP.clamp) {
                LayoutParams.CLAMP_PARENT -> leftView!!.right >= width

                LayoutParams.CLAMP_SELF -> leftView!!.right >= leftView!!.width

                else -> leftView!!.right >= leftViewLP.clamp
            }
        }

        private fun rightViewClampReached(lp: LayoutParams): Boolean {
            if (rightView == null) return false

            return when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> rightView!!.right <= width

                LayoutParams.CLAMP_SELF -> rightView!!.right <= width

                else -> rightView!!.left + lp.clamp <= width
            }
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            offsetChildren(changedView, dx)

            if (swipeListener == null) return

            var stickyBound: Int
            if (dx > 0) {
                //move to right
                if (leftView != null) {
                    stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView!!.right - stickyBound > 0 && leftView!!.right - stickyBound - dx <= 0)
                            swipeListener!!.onLeftStickyEdge(this@SwipeLayout, true)
                    }
                }

                if (rightView != null) {
                    stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView!!.left + stickyBound > width && rightView!!.left + stickyBound - dx <= width)
                            swipeListener!!.onRightStickyEdge(this@SwipeLayout, true)
                    }
                }
            } else if (dx < 0) {
                //move to left
                if (leftView != null) {
                    stickyBound = getStickyBound(leftView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (leftView!!.right - stickyBound <= 0 && leftView!!.right - stickyBound - dx > 0)
                            swipeListener!!.onLeftStickyEdge(this@SwipeLayout, false)
                    }
                }

                if (rightView != null) {
                    stickyBound = getStickyBound(rightView)
                    if (stickyBound != LayoutParams.STICKY_NONE) {
                        if (rightView!!.left + stickyBound <= width && rightView!!.left + stickyBound - dx > width)
                            swipeListener!!.onRightStickyEdge(this@SwipeLayout, false)
                    }
                }
            }
        }

        private fun getStickyBound(view: View?): Int {
            val lp = getLayoutParams(view!!)
            if (lp.sticky == LayoutParams.STICKY_NONE) return LayoutParams.STICKY_NONE

            return if (lp.sticky == LayoutParams.STICKY_SELF) view.width else lp.sticky
        }

        private fun clampMoveRight(child: View, left: Int): Int {
            if (leftView == null) {
                return if (child === centerView) left.coerceAtMost(0) else left.coerceAtMost(width)
            }

            val lp = getLayoutParams(leftView!!)
            return when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> Math.min(
                    left,
                    width + child.left - leftView!!.right
                )

                LayoutParams.CLAMP_SELF -> Math.min(left, child.left - leftView!!.left)

                else -> left.coerceAtMost(child.left - leftView!!.right + lp.clamp)
            }
        }

        private fun clampMoveLeft(child: View, left: Int): Int {
            if (rightView == null) {
                return if (child === centerView) left.coerceAtLeast(0) else left.coerceAtLeast(-child.width)
            }

            val lp = getLayoutParams(rightView!!)
            return when (lp.clamp) {
                LayoutParams.CLAMP_PARENT -> (child.left - rightView!!.left).coerceAtLeast(left)

                LayoutParams.CLAMP_SELF -> left.coerceAtLeast(width - rightView!!.left + child.left - rightView!!.width)

                else -> left.coerceAtLeast(width - rightView!!.left + child.left - lp.clamp)
            }
        }

        private fun onMoveRightReleased(child: View, dx: Int, xvel: Float): Boolean {

            if (xvel > velocityThreshold) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                val moveToOriginal = centerView!!.left < 0
                startScrollAnimation(child, clampMoveRight(child, left), !moveToOriginal, true)
                return true
            }

            if (leftView == null) {
                startScrollAnimation(child, child.left - centerView!!.left, false, true)
                return true
            }

            val lp = getLayoutParams(leftView!!)

            if (dx > 0 && xvel >= 0 && leftViewClampReached(lp)) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@SwipeLayout, true)
                }
                return true
            }

            if (dx > 0 && xvel >= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && leftView!!.right > lp.bringToClamp) {
                val left = if (centerView!!.left < 0) child.left - centerView!!.left else width
                startScrollAnimation(
                    child, clampMoveRight(child, left),
                    moveToClamp = true,
                    toRight = true
                )
                return true
            }

            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) leftView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity

                if (isBetween(-amplitude, amplitude, (centerView!!.left - stickyBound).toFloat())) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == leftView!!.width ||
                                lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - centerView!!.left + stickyBound,
                        toClamp,
                        true
                    )
                    return true
                }
            }
            return false
        }

        private fun onMoveLeftReleased(child: View, dx: Int, xvel: Float): Boolean {
            if (-xvel > velocityThreshold) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                val moveToOriginal = centerView!!.left > 0
                startScrollAnimation(child, clampMoveLeft(child, left), !moveToOriginal, false)
                return true
            }

            if (rightView == null) {
                startScrollAnimation(
                    child,
                    child.left - centerView!!.left,
                    moveToClamp = false,
                    toRight = false
                )
                return true
            }


            val lp = getLayoutParams(rightView!!)

            if (dx < 0 && xvel <= 0 && rightViewClampReached(lp)) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@SwipeLayout, false)
                }
                return true
            }

            if (dx < 0 && xvel <= 0 && lp.bringToClamp != LayoutParams.BRING_TO_CLAMP_NO && rightView!!.left + lp.bringToClamp < width) {
                val left = if (centerView!!.left > 0) child.left - centerView!!.left else -width
                startScrollAnimation(child, clampMoveLeft(child, left), true, false)
                return true
            }

            if (lp.sticky != LayoutParams.STICKY_NONE) {
                val stickyBound =
                    if (lp.sticky == LayoutParams.STICKY_SELF) rightView!!.width else lp.sticky
                val amplitude = stickyBound * lp.stickySensitivity

                if (isBetween(
                        -amplitude,
                        amplitude,
                        (centerView!!.right + stickyBound - width).toFloat()
                    )
                ) {
                    val toClamp =
                        lp.clamp == LayoutParams.CLAMP_SELF && stickyBound == rightView!!.width ||
                                lp.clamp == stickyBound ||
                                lp.clamp == LayoutParams.CLAMP_PARENT && stickyBound == width
                    startScrollAnimation(
                        child,
                        child.left - rightView!!.left + width - stickyBound,
                        toClamp,
                        false
                    )
                    return true
                }
            }

            return false
        }

        private fun isBetween(left: Float, right: Float, check: Float): Boolean {
            return check in left..right
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        dragHelper = ViewDragHelper.create(this, 1f, dragCallback)
        velocityThreshold = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            VELOCITY_THRESHOLD,
            resources.displayMetrics
        )
        touchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop.toFloat()

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
            if (a.hasValue(R.styleable.SwipeLayout_swipe_enabled)) {
                isLeftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true)
                isRightSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_swipe_enabled, true)
            }
            if (a.hasValue(R.styleable.SwipeLayout_left_swipe_enabled)) {
                isLeftSwipeEnabled = a.getBoolean(R.styleable.SwipeLayout_left_swipe_enabled, true)
            }
            if (a.hasValue(R.styleable.SwipeLayout_right_swipe_enabled)) {
                isRightSwipeEnabled =
                    a.getBoolean(R.styleable.SwipeLayout_right_swipe_enabled, true)
            }

            a.recycle()
        }
    }

    fun setOnSwipeListener(swipeListener: OnSwipeListener) {
        this.swipeListener = swipeListener
    }

    /**
     * resetData swipe-layout state to initial position
     */
    fun reset() {
        if (centerView == null) return

        finishAnimator()
        dragHelper!!.abort()

        offsetChildren(null, -centerView!!.left)
    }

    /**
     * resetData swipe-layout state to initial position with animation (200ms)
     */
    fun animateReset() {
        if (centerView != null) {
            runAnimation(centerView!!.left, 0)
        }
    }

    /**
     * Swipe with animation to left by right view's width
     *
     *
     * Ignores [SwipeLayout.isSwipeEnabled] and [SwipeLayout.isLeftSwipeEnabled]
     */
    fun animateSwipeLeft() {
        if (centerView != null && rightView != null) {
            val target = -rightView!!.width
            runAnimation(offset, target)
        }
    }

    /**
     * Swipe with animation to right by left view's width
     *
     *
     * Ignores [SwipeLayout.isSwipeEnabled] and [SwipeLayout.isRightSwipeEnabled]
     */
    fun animateSwipeRight() {
        if (centerView != null && leftView != null) {
            val target = leftView!!.width
            runAnimation(offset, target)
        }
    }

    private fun runAnimation(initialX: Int, targetX: Int) {
        finishAnimator()
        dragHelper!!.abort()

        val animator = ObjectAnimator()
        animator.target = this
        animator.propertyName = "offset"
        animator.interpolator = AccelerateInterpolator()
        animator.setIntValues(initialX, targetX)
        animator.duration = 200
        animator.start()
        this.weakAnimator = WeakReference(animator)
    }

    private fun finishAnimator() {
        if (weakAnimator != null) {

            val animator = this.weakAnimator!!.get()
            if (animator != null) {
                this.weakAnimator!!.clear()
                if (animator.isRunning) {
                    animator.end()
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        val count = childCount

        var maxHeight = 0

        // Find out how big everyone wants to be
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            measureChildren(widthMeasureSpec, heightMeasureSpec)
        } else {
            //find a child with biggest height
            for (i in 0 until count) {
                val child = getChildAt(i)
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                maxHeight = maxHeight.coerceAtLeast(child.measuredHeight)
            }

            if (maxHeight > 0) {
                heightMeasureSpec =
                    MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY)
                measureChildren(widthMeasureSpec, heightMeasureSpec)
            }
        }

        // Find rightmost and bottom-most child
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val childBottom: Int = child.measuredHeight

                maxHeight = maxHeight.coerceAtLeast(childBottom)
            }
        }

        maxHeight += paddingTop + paddingBottom
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight)

        setMeasuredDimension(
            View.resolveSize(suggestedMinimumWidth, widthMeasureSpec),
            View.resolveSize(maxHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren()
    }

    private fun layoutChildren() {
        val count = childCount

        val parentTop = paddingTop

        centerView = null
        leftView = null
        rightView = null
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue

            val lp = child.layoutParams as LayoutParams
            when (lp.gravity) {
                LayoutParams.CENTER -> centerView = child

                LayoutParams.LEFT -> leftView = child

                LayoutParams.RIGHT -> rightView = child
            }
        }

        if (centerView == null) throw RuntimeException("Center view must be added")

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams

                val width = child.measuredWidth
                val height = child.measuredHeight

                val childLeft: Int
                val childTop: Int = parentTop

                childLeft = when (lp.gravity) {
                    LayoutParams.LEFT -> centerView!!.left - width

                    LayoutParams.RIGHT -> centerView!!.right

                    LayoutParams.CENTER -> child.left
                    else -> child.left
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    private fun startScrollAnimation(
        view: View,
        targetX: Int,
        moveToClamp: Boolean,
        toRight: Boolean
    ) {
        if (dragHelper!!.settleCapturedViewAt(targetX, view.top)) {
            ViewCompat.postOnAnimation(view, SettleRunnable(view, moveToClamp, toRight))
        } else {
            if (moveToClamp && swipeListener != null) {
                swipeListener!!.onSwipeClampReached(this@SwipeLayout, toRight)
            }
        }
    }

    private fun getLayoutParams(view: View): LayoutParams {
        return view.layoutParams as LayoutParams
    }

    private fun offsetChildren(skip: View?, dx: Int) {
        if (dx == 0) return

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child === skip) continue

            child.offsetLeftAndRight(dx)
            invalidate(child.left, child.top, child.right, child.bottom)
        }
    }

    private fun hackParents() {
        var parent: ViewParent? = parent
        while (parent != null) {
            if (parent is NestedScrollingParent) {
                val view = parent as View?
                hackedParents[view!!] = view.isEnabled
            }
            parent = parent.parent
        }
    }

    private fun unHackParents() {
        for ((view, value) in hackedParents) {
            if (view != null) {
                view.isEnabled = value
            }
        }
        hackedParents.clear()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (isSwipeEnabled)
            internalOnInterceptTouchEvent(event)
        else
            super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val defaultResult = super.onTouchEvent(event)
        if (!isSwipeEnabled) {
            return defaultResult
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onTouchBegin(event)

            MotionEvent.ACTION_MOVE -> if (touchState == TOUCH_STATE_WAIT) {
                val dx = abs(event.x - touchX)
                val dy = abs(event.y - touchY)

                val isLeftToRight = event.x - touchX > 0

                if ((isLeftToRight && !isLeftSwipeEnabled || !isLeftToRight && !isRightSwipeEnabled) && offset == 0) {

                    return defaultResult
                }

                if (dx >= touchSlop || dy >= touchSlop) {
                    touchState =
                        if (dy == 0f || dx / dy > 1f) TOUCH_STATE_SWIPE else TOUCH_STATE_SKIP
                    if (touchState == TOUCH_STATE_SWIPE) {
                        requestDisallowInterceptTouchEvent(true)

                        hackParents()

                        if (swipeListener != null)
                            swipeListener!!.onBeginSwipe(this, event.x > touchX)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (touchState == TOUCH_STATE_SWIPE) {
                    unHackParents()
                    requestDisallowInterceptTouchEvent(false)
                }
                touchState =
                    TOUCH_STATE_WAIT
            }
        }

        if (event.actionMasked != MotionEvent.ACTION_MOVE || touchState == TOUCH_STATE_SWIPE) {
            dragHelper!!.processTouchEvent(event)
        }

        return true
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            MATCH_PARENT,
            WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(
            context,
            attrs
        )
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    private fun internalOnInterceptTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            onTouchBegin(event)
        }
        return dragHelper!!.shouldInterceptTouchEvent(event)
    }

    private fun onTouchBegin(event: MotionEvent) {
        touchState =
            TOUCH_STATE_WAIT
        touchX = event.x
        touchY = event.y
    }

    private inner class SettleRunnable internal constructor(
        private val view: View,
        private val moveToClamp: Boolean,
        private val moveToRight: Boolean
    ) : Runnable {

        override fun run() {
            if (dragHelper != null && dragHelper!!.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this)
            } else {
                AppLog.d(TAG, "On swipe clamp: $moveToClamp ; moveToRight: $moveToRight")
                if (moveToClamp && swipeListener != null) {
                    swipeListener!!.onSwipeClampReached(this@SwipeLayout, moveToRight)
                }
            }
        }
    }

    class LayoutParams : ViewGroup.LayoutParams {

        var gravity =
            CENTER
        var sticky: Int = 0
        var stickySensitivity =
            DEFAULT_STICKY_SENSITIVITY
        var clamp =
            CLAMP_SELF
        var bringToClamp =
            BRING_TO_CLAMP_NO

        @SuppressLint("CustomViewStyleable")
        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {

            val a = c.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)

            val n = a.indexCount
            for (i in 0 until n) {
                when (val attr = a.getIndex(i)) {
                    R.styleable.SwipeLayout_gravity -> gravity = a.getInt(attr,
                        CENTER
                    )
                    R.styleable.SwipeLayout_sticky -> sticky =
                        a.getLayoutDimension(attr,
                            STICKY_SELF
                        )
                    R.styleable.SwipeLayout_clamp -> clamp = a.getLayoutDimension(attr,
                        CLAMP_SELF
                    )
                    R.styleable.SwipeLayout_bring_to_clamp -> bringToClamp =
                        a.getLayoutDimension(attr,
                            BRING_TO_CLAMP_NO
                        )
                    R.styleable.SwipeLayout_sticky_sensitivity -> stickySensitivity =
                        a.getFloat(attr,
                            DEFAULT_STICKY_SENSITIVITY
                        )
                }
            }
            a.recycle()
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(width: Int, height: Int) : super(width, height)

        companion object {

            const val LEFT = -1
            const val RIGHT = 1
            const val CENTER = 0

            const val CLAMP_PARENT = -1
            const val CLAMP_SELF = -2
            const val BRING_TO_CLAMP_NO = -1

            const val STICKY_SELF = -1
            const val STICKY_NONE = -2
            private const val DEFAULT_STICKY_SENSITIVITY = 0.9f
        }
    }

    interface OnSwipeListener {
        fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean)

        fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean)

        fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean)

        fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean)
    }

    companion object {

        private val TAG = SwipeLayout::class.java.simpleName
        private const val VELOCITY_THRESHOLD = 1500f

        private const val TOUCH_STATE_WAIT = 0
        private const val TOUCH_STATE_SWIPE = 1
        private const val TOUCH_STATE_SKIP = 2
    }
}
