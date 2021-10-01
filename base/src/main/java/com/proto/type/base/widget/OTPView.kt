package com.proto.type.base.widget

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.proto.type.base.R
import com.proto.type.base.extension.showKeyboard
import com.proto.type.base.utils.AppLog

class OTPView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextWatcher,
    View.OnKeyListener, TextView.OnEditorActionListener {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = OTPView.javaClass.simpleName
    }

    // MARK: - Interface
    interface OTPListener {
        fun onOTPEntered(otpText: String)
        fun onDoneClicked(otpText: String)
    }

    // MARK: - Private Variables
    private var fieldCount = 6
    private var fieldsList: ArrayList<EditText>? = null
    private var mContext: Context? = null
    private var move = false
    private var otpListener: OTPListener? = null

    // MARK: - Private Constant
    private val currentFocusIndex: Int
        get() {
            var index = 1
            for (i in 0 until fieldCount)
                if (fieldsList!![i].isFocused) {
                    index = i
                    break
                }
            return index
        }

    // MARK: - Override Functions
    init {
        mContext = context
        var lp =
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        lp.gravity = CENTER
        this.orientation = HORIZONTAL
        this.layoutParams = lp
        fieldsList = ArrayList()

        // Adding OTP Fields
        for (i in 0 until fieldCount) {
            val field = EditText(mContext)
            field.textAlignment = View.TEXT_ALIGNMENT_CENTER
            field.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            field.typeface = ResourcesCompat.getFont(mContext!!, R.font.roboto_bold)
            field.setTextColor(ContextCompat.getColor(mContext!!, R.color.blue_link))
            field.inputType = InputType.TYPE_CLASS_NUMBER
            field.tag = "EditText:$i"
            field.isEnabled = false
            field.setBackgroundResource(R.drawable.otp_bg)
            field.setPadding(getPX(10f), getPX(10f), getPX(10f), getPX(10f))
            lp = LayoutParams(getPX(36f), ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(getPX(5f), getPX(5f), getPX(5f), getPX(5f))
            lp.weight = 0.16f
            field.layoutParams = lp
            field.isCursorVisible = false
            field.onFocusChangeListener = this
            field.addTextChangedListener(this)
            field.setOnEditorActionListener(this)
            field.setOnKeyListener(this)
            fieldsList!!.add(field)
            this.addView(field)
        }
        selectEditText(fieldsList!![0])
    }

    override fun afterTextChanged(s: Editable) {
        AppLog.d(TAG, "AfterTextChanged with info text: $s")
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

    override fun onEditorAction(p0: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            otpListener!!.onDoneClicked(getOTPText())
        }
        return false
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        AppLog.d(TAG, "OnFocusChange with view's tag: " + v.tag)
        (v as EditText).setSelection(v.text.length)
        if (hasFocus) {
            v.setBackgroundResource(R.drawable.otp_active_bg)
        } else {
            if (v.text.isEmpty())
                v.setBackgroundResource(R.drawable.otp_bg)
        }
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        AppLog.d(TAG, "onKey: $event keyCode: $keyCode")
        return false
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        try {
            Integer.parseInt(getOTPText())
            otpListener!!.onOTPEntered(getOTPText())
        } catch (e: Exception) {
            AppLog.d(TAG, "Can't parse int with exception $e")
        }
        var ed = fieldsList!![currentFocusIndex]
        if (s.isEmpty()) {
            if (move) {
                moveFocusBackwards()
                move = false
                return
            } else {
                ed.setText(" ")
                move = true
                ed.setSelection(ed.text.length)
                return
            }
        }
        move = false
        if (currentFocusIndex < fieldCount - 1) {
            if (s.length == 1) {
                if (s[0] != ' ')
                    moveFocusForward()
            } else if (s.length > 1) {
                val c = s.toString()[s.length - 1]
                ed = fieldsList!![currentFocusIndex]
                ed.setText(c.toString())
                ed.setSelection(ed.text.length)
            }
        } else if (s.length > 1) {
            AppLog.d(TAG, "Text Entered:$s")
            val c = s.toString()[s.length - 1]
            AppLog.d(TAG, "Set Text :$c")
            ed.setText(c.toString())
            ed.setSelection(ed.text.length)
        }
    }

    // MARK: - Public Functions
    private fun getOTPText(): String {
        val otpString = StringBuffer()
        for (editText in fieldsList!!) {
            otpString.append(editText.text)
        }
        return otpString.toString()
    }

    fun setOTPListener(otpListener: OTPListener) {
        this.otpListener = otpListener
    }

    fun setOTPText(otpText: String) {
        for (i in 0 until fieldCount)
            fieldsList!![i].setText(otpText[i].toString())
        selectEditText(fieldsList!![fieldsList!!.size - 1])
        if (this.otpListener != null)
            this.otpListener!!.onOTPEntered(getOTPText())
    }

    // MARK: - Private Functions
    private fun getPX(dip: Float): Int {
        val r = resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        ).toInt()
    }

    private fun moveFocusBackwards() {
        try {
            selectEditText(fieldsList!![currentFocusIndex - 1])
        } catch (e: Exception) {
            AppLog.d(TAG, "Can't move focus backward with exception $e")
        }
    }

    private fun moveFocusForward() {
        try {
            selectEditText(fieldsList!![currentFocusIndex + 1])
            fieldsList!![currentFocusIndex].setText(" ")
            val ed = fieldsList!![currentFocusIndex]
            ed.setSelection(ed.text.length)
            move = true
        } catch (e: Exception) {
            AppLog.d(TAG, "Can't move focus forward with exception $e")
        }
    }

    private fun selectEditText(editText: EditText) {
        editText.isEnabled = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        editText.showKeyboard()
        editText.isActivated = true
        editText.isPressed = true
        for (f in fieldsList!!)
            f.isEnabled = f.isFocused
    }
}