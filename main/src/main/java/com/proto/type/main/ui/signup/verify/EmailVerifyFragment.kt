package com.proto.type.main.ui.signup.verify

import android.content.Intent
import android.content.Intent.CATEGORY_APP_EMAIL
import android.net.Uri
import com.proto.type.main.R
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import kotlinx.android.synthetic.main.fragment_verify_email.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmailVerifyFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = EmailVerifyFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_verify_email

    // MARK: - Private Constants
    private val viewModel: EmailVerifyViewModel by viewModel()

    // MARK: - Override Functions
    override fun initEvent() {
        super.initEvent()

        btOpenEmail.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(CATEGORY_APP_EMAIL)
            }
            startActivity(intent)
        }

        tvResend.setOnClickListener {
            showLoading()
            viewModel.resendEmail()
            hideLoading()
        }

        tvContinue.setOnClickListener {
            viewModel.checkForMarketDataUpdate()
            navigateTo(Uri.parse(Constants.Uri.URI_INBOX))
        }
    }
}