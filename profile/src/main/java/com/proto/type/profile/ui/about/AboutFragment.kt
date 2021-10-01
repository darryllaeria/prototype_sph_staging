package com.proto.type.profile.ui.about

import com.proto.type.base.BuildConfig
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_about

    override fun initView() {
        super.initView()

        ipvVersion.setValue(BuildConfig.VERSION_NAME)
    }

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }
    }
}