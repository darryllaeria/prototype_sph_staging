package com.proto.type.profile.ui.chatQId

import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.extension.setupCursorEvents
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_chatq_id.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatQIdFragment: BaseFragment() {

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_chatq_id

    // MARK: - Private Constant
    private val viewModel: ChatQIdViewModel by viewModel()

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        etUsername.setText(arguments?.getString(Constants.KEY_USER_ID))
    }

    override fun initEvent() {
        super.initEvent()

        btDone.setOnClickListener {
            showLoading()
            viewModel.updateUserName(etUsername.text.toString())
        }

        etUsername.setupCursorEvents()

        ibRemove.setOnClickListener {
            etUsername.text.clear()
        }

        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.isUpdated.observe(this, Observer {
            hideLoading()
            if (it) navigateBack() else showError("Update failed!")
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isUpdated.removeObservers(this)
    }
}