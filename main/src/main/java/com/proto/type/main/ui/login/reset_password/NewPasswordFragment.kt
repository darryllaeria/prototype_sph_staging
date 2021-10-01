package com.proto.type.main.ui.login.reset_password

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.Constants
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_create_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPasswordFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_create_password
    private val viewModel: NewPasswordViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        etPassword.addTextChangedListener(textWatcher)
        etRePassword.addTextChangedListener(textWatcher)

        btConfirm.setOnClickListener {
            val code = arguments?.get("code") as String

            viewModel.isPasswordReset.observe(this, Observer<UIState> {
                when (it) {
                    is UIState.FINISHED<*> -> {
                        navigateTo(Uri.parse(Constants.Uri.URI_LOGIN))
                    }
                    is UIState.FAILED -> {
                        // TODO("Handle error")
                    }
                    else -> {}
                }
            })
            viewModel.confirmPasswordReset(code, etPassword.text.toString())
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isPasswordReset.removeObservers(this)
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            tvError.hideView()
            val password = etPassword.text.toString()
            val reenter = etRePassword.text.toString()

            val isValidatedPwd = Validator.validatePasswordLength(password)
            val isValidateReEnter = Validator.validatePasswordLength(reenter)

            btConfirm.isEnabled = isValidatedPwd && isValidateReEnter
            if (isValidatedPwd) {
                if (password != reenter) {
                    tvError.apply {
                        showView()
                        setText(R.string.txt_err_pwd_not_match)
                    }
                }
            } else {
                tvError.apply {
                    showView()
                    text = ""
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}