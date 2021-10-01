package com.proto.type.profile.ui.delete

import android.net.Uri
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.profile.R
import com.proto.type.profile.dialog.DeleteConfirmDialog
import kotlinx.android.synthetic.main.fragment_delete_account.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeleteAccountFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_delete_account
    private val viewModel: DeleteAccountViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btDelete.setOnClickListener {
            viewModel.checkEnterPhone("${ccpCountry.selectedCountryCodeWithPlus}${etPhone.text}")
            viewModel.deletingUser.observe(this, Observer {
                when (it) {
                    is UIState.FINISHED<*> -> {
                        (it.data as? UserModel)?.let { user -> showConfirmDialog(user)}

                    }
                    is UIState.FAILED -> {
                        // TODO("Handle when enter phone number is wrong")
                        showInfo(getString(it.messageId))
                    }
                    else -> {}
                }
            })
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.getCountryCode()
        viewModel.countryCode.observe(this, Observer {
            ccpCountry.setCountryForNameCode(it)
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isDeleted.removeObservers(this)
        viewModel.deletingUser.removeObservers(this)
        viewModel.countryCode.removeObservers(this)
    }

    private fun showConfirmDialog(user: UserModel) {
        DeleteConfirmDialog(user) {
            showLoading()
            viewModel.deleteAccount()
            viewModel.isDeleted.observe(this@DeleteAccountFragment, Observer {
                hideLoading()
                when (it) {
                    is UIState.FINISHED<*> -> {
                        if (it.data as? Boolean == true) {
                            navigateTo(Uri.parse(Constants.Uri.URI_ONBOARD))
                        } else {
                            showInfo(getString(it.messageId))
                        }
                    }
                    is UIState.FAILED -> {
                        showInfo(getString(it.messageId))
                    }
                }
            })
        }.show(childFragmentManager, "delete")
    }
}