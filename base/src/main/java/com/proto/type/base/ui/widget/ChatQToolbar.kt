package com.proto.type.base.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.base.R
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import kotlinx.android.synthetic.main.view_toolbar.view.*

class ChatQToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var isEditMode: Boolean = false
    private var btnResource: Int = 0
    private var tvRightResource: String? = null

    init {
        if (attrs != null) {
            var arr = context.obtainStyledAttributes(attrs, R.styleable.ChatQToolbar)
            try {
                btnResource = arr.getResourceId(R.styleable.ChatQToolbar_ctb_button_src, 0)
                tvRightResource = arr.getString(R.styleable.ChatQToolbar_ctb_text_right)
            } finally {
                arr?.recycle()
            }
        }
        View.inflate(context, R.layout.view_toolbar, this)
    }

    // MARK: - Override Functions
    override fun onFinishInflate() {
        super.onFinishInflate()
        if (btnResource != 0) {
            ivRightButton.setImageResource(btnResource)
        }

        if (tvRightResource != null) {
            tvRightText.text = tvRightResource
        }

        showMode()
    }

    // MARK: - Public Functions
    fun addOnBackClickListener(callback: () -> Unit) {
        showBackView()
        ivBack.setOnClickListener {
            callback.invoke()
        }
    }

    fun addOnBackProfileClickListener(callback: () -> Unit) {
        ivBack.setOnClickListener {
            enterEditMode(false)
            callback.invoke()
        }
    }

    fun addOnRightTextClickListener(callback: () -> Unit) {
        tvRightText.showView()
        tvRightText.setOnClickListener {
            callback.invoke()
        }
    }

    fun addOnSaveClickListener(callback: () -> Unit) {
        tvRightText.setOnClickListener {
            enterEditMode(false)
            callback.invoke()
        }
    }

    fun addOnToolbarButtonCallback(callback: () -> Unit) {
        ivRightButton.setOnClickListener {
            callback.invoke()
        }
    }

    fun enterEditMode(value: Boolean) {
        isEditMode = value
        showMode()
    }

    fun showBackView() {
        ivBack.showView()
    }

    // MARK: - Private Functions
    private fun showMode() {
        if (isEditMode) {
            ivBack.showView()
            tvRightText.showView()
            ivRightButton.hideView()
        } else {
            ivBack.hideView()
            tvRightText.hideView()
            ivRightButton.showView()
        }
    }
}