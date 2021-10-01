package com.proto.type.main.ui.signup.password

import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_create_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePasswordFragment: BaseFragment() {

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_create_password

    // MARK: - Private Constants
    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            tvError.hideView()
            val password = etPassword.text.toString()
            val reenter = etRePassword.text.toString()

            markLength.markDone(Validator.validatePasswordLength(password))
            markUpper.markDone(Validator.validateCasePassword(password))
            markSpecial.markDone(Validator.validateSpecialCharacter(password))

            val isEqual = password == reenter

            btConfirm.isEnabled = isEqual && markLength.isMarked() && markUpper.isMarked() && markSpecial.isMarked()

            if (password != reenter) {
                showError(R.string.txt_err_pwd_not_match)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    }
    private val viewModel: CreatePasswordViewModel by viewModel()

    // MARK: - Companion Object
    companion object {
        private val TAG: String = CreatePasswordFragment::class.java.simpleName
    }

    // MARK: - Override Functions
    override fun initEvent() {
        super.initEvent()

        btConfirm.isEnabled = Validator.validatePasswordLength(etPassword.text.toString()) && Validator.validatePasswordLength(etRePassword.text.toString())

        ivBack.setOnClickListener {
            navigateBack()
        }

        etPassword.addTextChangedListener(textWatcher)
        etRePassword.addTextChangedListener(textWatcher)
        ivPassword.setOnClickListener {
            ivPassword.isSelected = !ivPassword.isSelected
            if (ivPassword.isSelected) {
                etPassword.transformationMethod = null
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod()
            }
        }

        ivRePassword.setOnClickListener {
            ivRePassword.isSelected = !ivRePassword.isSelected
            if (ivRePassword.isSelected) {
                etRePassword.transformationMethod = null
            } else {
                etRePassword.transformationMethod = PasswordTransformationMethod()
            }
        }

        btConfirm.setOnClickListener {
            viewModel.storePassword(etPassword.text.toString())
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.isUserCreated.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading()
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    if (it.data != null) {
                        navigateTo(R.id.action_password_to_mobile)
                    } else {
                        // TODO("Handle error when create firebase user")
                        AppLog.d(TAG, "Error creating firebase user")
                    }
                }
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isUserCreated.removeObservers(this)
    }

    // MARK: - Private Function
    private fun showError(message: Int) {
        tvError.showView()
        tvError.setText(message)
    }
}