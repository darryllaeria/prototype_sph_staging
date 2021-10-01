package com.proto.type.main.ui.login.forgot_password

import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.proto.type.main.R
import com.proto.type.base.Constants
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotPasswordFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_forgot_password
    private val viewModel: ForgotPasswordViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        etEmail.addTextChangedListener(emailWatcher)

        btReset.setOnClickListener {
            viewModel.requestPasswordReset(etEmail.text.toString())
            viewModel.isPasswordReset.observe(this, Observer {
                when (it) {
                    is UIState.LOADING -> {
                        tvError.hideView()
                        showLoading()
                    }
                    is UIState.FINISHED<*> -> {
                        if ((it.data as? Boolean) == true) {
                            findNavController().navigate(R.id.action_forgot_password_to_check_mail, bundleOf(
                                Constants.KEY_EMAIL to etEmail.text.toString()))
                        } else {
                            tvError.showView()
                        }
                    }
                    else -> {}
                }
            })
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isPasswordReset.removeObservers(this)
    }

    private val emailWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btReset.isEnabled = Validator.isValidateEmail(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}