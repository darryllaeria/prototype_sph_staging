package com.proto.type.main.ui.signup.term

import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.proto.type.main.R
import com.proto.type.base.extension.showView
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import kotlinx.android.synthetic.main.fragment_term_of_service.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TermOfServiceFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_term_of_service
    private val viewModel: TermOfServiceViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()
        btAccept.isEnabled = cbTerms.isChecked && cbPrivacy.isChecked
        cbTerms.setOnCheckedChangeListener(checkboxCallback)
        cbPrivacy.setOnCheckedChangeListener(checkboxCallback)

        ivBack.setOnClickListener {
            navigateBack()
        }

        btAccept.setOnClickListener {
            viewModel.isUserCreated.observe(this, Observer {
                when (it) {
                    is UIState.LOADING -> {
                        showLoading()
                    }
                    is UIState.FINISHED<*> -> {
                        hideLoading()
                        if (it.data != null) {
                            viewModel.sendEmail()
                            navigateTo(R.id.action_term_service_to_verify)
                        } else {
                            showError(it.messageId)
                        }
                    }
                    is UIState.FAILED -> {
                        hideLoading()
                        showError(it.messageId)
                    }
                    else -> {}
                }
            })
            viewModel.createUser()
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isUserCreated.removeObservers(this)
    }

    private fun showError(message: Int) {
        tvError.showView()
        tvError.setText(message)
    }

    private val checkboxCallback = CompoundButton.OnCheckedChangeListener { _, _ ->
        btAccept.isEnabled = cbTerms.isChecked && cbPrivacy.isChecked
    }
}