package com.proto.type.profile.ui.phone.otp

import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_otp_verify.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpVerifyFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_otp_verify
    private val viewModel: OtpVerifyViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.requestOtp(arguments?.getString(Constants.KEY_PHONE)!!)
    }

}