package com.proto.type.profile.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.view_profile_menu.view.*

class ItemProfileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var icon = 0
    private var title: String = ""
    private var hint: String? = ""
    private var value: String? = ""
    private var isShowArrow = false
    private var isShowDivider = false

    init {
        if (attrs != null) {
            var arr: TypedArray? = null
            try {
                arr = context.obtainStyledAttributes(attrs, R.styleable.ItemProfileView)
                icon = arr.getResourceId(R.styleable.ItemProfileView_ipv_icon, 0)
                title = arr.getString(R.styleable.ItemProfileView_ipv_title)!!
                hint = arr.getString(R.styleable.ItemProfileView_ipv_hint)
                value = arr.getString(R.styleable.ItemProfileView_ipv_value)
                isShowArrow = arr.getBoolean(R.styleable.ItemProfileView_ipv_show_arrow, false)
                isShowDivider = arr.getBoolean(R.styleable.ItemProfileView_ipv_show_divider, false)
            } finally {
                arr?.recycle()
            }
        }
        View.inflate(context, R.layout.view_profile_menu, this)

    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (isShowDivider) {
            divider.showView()
        } else {
            divider.hideView()
        }

        if (icon != 0) {
            ivMenuIcon.apply {
                showView()
                setImageResource(icon)
            }
        } else {
            ivMenuIcon.hideView()
        }
        tvMenuTitle.text = title

        if (value != null && value!!.isNotEmpty()) {
            tvMenuValue.text = value
        } else {
            if (hint != null && hint!!.isNotEmpty()) {
                tvMenuValue.hint = hint
            } else {
                tvMenuValue.hideView()
            }
        }

        if (isShowArrow) {
            ivArrow.showView()
        } else {
            ivArrow.hideView()
        }
    }

    fun addOnTouchListener(callback: () -> Unit?) {
        setOnClickListener {
            callback.invoke()
        }
    }

    fun showNotification(icon: Int = -1) {
        ivNotification.showView()
        if (icon != -1) {
            ivNotification.setImageResource(icon)
        }
    }

    fun setValue(value: String?) {
        tvMenuValue.showView()
        tvMenuValue.text = value?: ""
    }

    fun getValue(): String {
        return tvMenuValue.text.toString()
    }

    fun hideNotification() {
        ivNotification.hideView()
    }
}