package com.proto.type.profile.ui.privacy

import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_privacy_and_security.*

class PrivacyAndSecurityFragment: BaseFragment() {
    override var layoutId = R.layout.fragment_privacy_and_security

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        ipvBlockedUser.setOnClickListener {
            navigateTo(R.id.action_privacy_to_blocked_contact)
        }
    }
}