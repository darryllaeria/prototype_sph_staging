package com.proto.type.profile.ui.email.inbox

import android.content.Intent
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_check_inbox.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckInboxFragment: BaseFragment() {

    override var layoutId: Int = R.layout.fragment_check_inbox

    private val viewModel: CheckInboxViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btnOpenMail.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_APP_EMAIL)
            }
            startActivity(intent)
        }

        tvResend.setOnClickListener {
            viewModel.sendVerifyEmail()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.sendVerifyEmail()
    }
}