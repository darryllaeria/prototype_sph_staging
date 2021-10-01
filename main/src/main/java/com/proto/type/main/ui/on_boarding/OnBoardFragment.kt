package com.proto.type.main.ui.on_boarding

import com.proto.type.main.R
import com.proto.type.base.base_component.BaseFragment
import kotlinx.android.synthetic.main.fragment_onboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * @details Onboarding fragment
 */

class OnBoardFragment : BaseFragment() {

    override var layoutId = R.layout.fragment_onboard
    private val viewModel: OnBoardViewModel by viewModel()

    override fun initView() {
        super.initView()
        OnBoardSliderManager(vpSlider, llIndicators)
    }

    override fun initEvent() {
        super.initEvent()
        btnRegister.setOnClickListener {
            viewModel.signUp()
            navigateTo(R.id.action_onboard_to_signup)
        }

        tvLogin.setOnClickListener {
            viewModel.signIn()
            navigateTo(R.id.action_onboard_to_signin)
        }
    }
}