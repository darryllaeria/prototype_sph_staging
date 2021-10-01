package com.proto.type.profile.ui.email.request

import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_request_email_verify.*

class EmailVerifyFragment: BaseFragment() {

    companion object {
        const val KEY_EMAIL = "email"
    }

    override var layoutId: Int = R.layout.fragment_request_email_verify
    private lateinit var email: String

    override fun initView() {
        super.initView()

        email = arguments?.getString(KEY_EMAIL)!!
        etVerifyEmail.setText(email)
    }

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btVerify.setOnClickListener {
            navigateTo(R.id.action_verify_email_to_inbox)
        }
    }
}