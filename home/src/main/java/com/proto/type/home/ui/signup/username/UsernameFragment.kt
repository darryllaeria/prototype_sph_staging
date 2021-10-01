package com.proto.type.home.ui.signup.username

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.proto.type.home.R
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.extension.*
import com.proto.type.base.utils.Utils
import com.proto.type.base.utils.Validator
import kotlinx.android.synthetic.main.fragment_signup_username.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsernameFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = UsernameFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_signup_username

    // MARK: - Private Constants
    private val viewModel: UsernameViewModel by viewModel()

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        btContinue.isEnabled = Validator.isValidateUsername(etUsername.text.toString())
        tvChatIDDescription.text = String.format(getString(R.string.chatq_id_rule), Constants.AUTH_USERNAME_MIN, Constants.AUTH_USERNAME_MAX)
        Utils.setEditTextInputFilter(etUsername, Constants.AUTH_USERNAME_MAX)
    }

    override fun initEvent() {
        super.initEvent()
        btContinue.setOnClickListener {
            viewModel.storeUserName(etUsername.text.toString())
            navigateTo(R.id.action_signup_username_to_terms)
        }

        etUsername.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.toString().let {
                    if (it != null) {
                        if (it.length >= Constants.AUTH_USERNAME_MIN) {
                            if (Validator.isValidateUsername(s.toString())) {
                                viewModel.checkUserName(s.toString())
                            } else {
                                tvMessage.showError(R.string.txt_username_invalidated)
                                btContinue.isEnabled = false
                            }
                        } else {
                            btContinue.isEnabled = false
                            cpbLoader.invisibleView()
                            tvMessage.invisibleView()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.isChatQIdExist.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    cpbLoader.showView()
                    tvMessage.showInfo(R.string.txt_checking_username)
                }
                is UIState.FINISHED<*> -> {
                    cpbLoader.hideView()
                    val isExisted = it.data as? Boolean
                    if (isExisted != null && !isExisted) {
                        btContinue.isEnabled = true
                        tvMessage.showInfo(R.string.txt_username_validated)
                    } else {
                        btContinue.isEnabled = false
                        tvMessage.showError(R.string.txt_username_used)
                    }
                }
                is UIState.FAILED -> {
                    btContinue.isEnabled = false
                    cpbLoader.hideView()
                    tvMessage.showError(R.string.txt_username_invalidated)
                }
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isChatQIdExist.removeObservers(this)
    }
}