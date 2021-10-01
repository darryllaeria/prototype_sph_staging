package com.proto.type.profile.widget

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.proto.type.base.Constants
import com.proto.type.base.SuccessCallback
import com.proto.type.base.extension.hideKeyboard
import com.proto.type.base.extension.showView
import com.proto.type.base.utils.ViewUtils
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.view_profile_name.view.*

class ProfileNameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var txtValue: String? = ""
    private var isEditMode: Boolean = false

    init {
        var arr: TypedArray? = null
        try {
            arr = context.obtainStyledAttributes(attrs, R.styleable.ProfileNameView)
            txtValue = arr.getString(R.styleable.ProfileNameView_pnv_value)
        } finally {
            arr?.recycle()
        }
        View.inflate(context, R.layout.view_profile_name, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvValue.text = txtValue

        tvEdit.setOnClickListener {
            if (!isEditMode) {
                toggleEdit()
            }
        }
        ivRemove.setOnClickListener {
            etValue.setText("")
        }
        etValue.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setCount()
            }

        })
    }

    fun subscribeModeChange(callback: SuccessCallback) {
        tvEdit.setOnClickListener {
            isEditMode = !isEditMode
            callback.invoke(isEditMode)
            if (isEditMode) {
                toggleEdit()
            } else {
                toggleDone()
            }
        }
    }

    fun toggleEdit() {
        etValue.setText(tvValue.text.toString())
        ViewUtils.hideViews(tvValue, tvEdit)
        ViewUtils.showViews(etValue, tvCount, ivRemove)
        setCount()
    }

    fun toggleDone() {
        ViewUtils.showViews(tvValue, tvEdit)
        ViewUtils.hideViews(etValue, tvCount, ivRemove)
        etValue.hideKeyboard()
    }

    private fun setCount() {
        tvCount.text = "${etValue.text?.length}/${Constants.MAX_COUNT}"
    }

    fun setValue(value: String?) {
        tvValue.showView()
        tvValue.text = value?: ""
    }

    fun getValue(): String {
        return etValue.text.toString()
    }
}