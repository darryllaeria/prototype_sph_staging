package com.proto.type.home.ui.splash

import android.net.Uri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.proto.type.home.R
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_splash
    private val viewModel: SplashViewModel by viewModel()

    override fun initLogic() {
        super.initLogic()

        viewModel.isUserAuthorized.observe(this, Observer {
            if (it) {
                findNavController().navigate(Uri.parse(Constants.Uri.URI_INBOX))
            } else {
                findNavController().navigate(R.id.action_splash_to_onboard)
            }
        })
        viewModel.checkAuthorization()
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isUserAuthorized.removeObservers(this)
    }
}