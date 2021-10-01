package com.proto.type.main.ui.signup.email

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_signup_email.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpEmailFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = SignUpEmailFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_signup_email

    // MARK: - Private Constants
    private val viewModel: SignUpEmailViewModel by viewModel()

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        btContinue.isEnabled = Validator.isValidateEmail(etEmail.text.toString())
    }

    override fun initEvent() {
        super.initEvent()
        btContinue.isEnabled = Validator.isValidateEmail(etEmail.text.toString())

        etEmail.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isValidated = Validator.isValidateEmail(s.toString())
                btContinue.isEnabled = isValidated
                if (isValidated) {
                    tvError.hideView()
                } else {
                    if (s.toString().isNotEmpty()) {
                        tvError.showView()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvError.hideView()
            }
        })

        ivBack.setOnClickListener {
            navigateBack()
        }

        btContinue.setOnClickListener {
            viewModel.checkUserEmail(etEmail.text.toString())
            viewModel.isUserExisted.observe(this, Observer {
                when (it) {
                    is UIState.LOADING -> {
                        showLoading()
                    }
                    is UIState.FINISHED<*> -> {
                        hideLoading()
                        try {
                            if (it.data == true) {
                                btContinue.isEnabled = false
                                showInfo(getString(R.string.email_error_used))
                            } else {
                                viewModel.storeEmail(etEmail.text.toString())
                                navigateTo(R.id.action_signup_email_to_password)
                            }
                        } catch (e: Exception) {
                            AppLog.d(TAG, "Check user email failed with exception: $e")
                            showInfo(getString(R.string.txt_unexpected_error))
                        }
                    }
                    is UIState.FAILED -> {
                        hideLoading()
                    }
                }
            })
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.loadSaveForm()
        viewModel.signUpForm.observe(this, Observer {
            etEmail.setText(it.email)
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.signUpForm.removeObservers(this)
        viewModel.isUserExisted.removeObservers(this)
    }
}