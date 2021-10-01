package com.proto.type.profile.ui.contact

import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_view_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewContactFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_view_contact
    private val viewModel: ViewContactViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.loadContacts()
    }
}