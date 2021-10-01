package com.proto.type.profile.ui.phone.request

import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_request_change_number.*

class ChangeNumberFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_request_change_number

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        tvChangeNumber.setOnClickListener {
            navigateTo(R.id.action_request_to_change_phone)
        }
    }

}