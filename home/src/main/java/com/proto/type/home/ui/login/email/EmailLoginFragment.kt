package com.proto.type.home.ui.login.email

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import com.proto.type.home.R
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showError
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_login_email.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmailLoginFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_login_email

    private val viewModel: EmailViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        etEmail.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        ivBack.setOnClickListener {
            navigateBack()
        }

        tvRecoverPassword.setOnClickListener {
            navigateTo(R.id.action_login_email_to_forgot_password)
        }

        ivPassword.setOnClickListener {
            ivPassword.isSelected = !ivPassword.isSelected
            etPassword.transformationMethod = if (ivPassword.isSelected) null else PasswordTransformationMethod()
            etPassword.setSelection(etPassword.text.length)
        }

        btContinue.setOnClickListener {
            viewModel.user.observe(this, Observer {
                when (it) {
                    is UIState.LOADING -> {
                        showLoading()
                        tvError.hideView()
                    }
                    is UIState.FINISHED<*> -> {
                        hideLoading()
                        navigateTo((Uri.parse(Constants.Uri.URI_INBOX)))
                    }
                    is UIState.FAILED -> {
                        hideLoading()
                        tvError.showError(it.messageId)
                    }
                    else -> {}
                }
            })
            viewModel.signIn(etEmail.text.toString(), etPassword.text.toString())
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.user.removeObservers(this)
    }

    private val watcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btContinue.isEnabled = Validator.isValidateEmail(etEmail.text.toString()) && etPassword.text.toString().isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
}