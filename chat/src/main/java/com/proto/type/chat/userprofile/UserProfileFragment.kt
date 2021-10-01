package com.proto.type.chat.userprofile

import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.net.Uri
import android.view.View
import androidx.core.os.bundleOf
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.data.model.UserModel
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_user_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.proto.type.base.Constants.KEY_CHATQ_USER
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.ReportReasonType
import com.proto.type.base.data.model.ReportTargetType
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.ui.dialog.FullScreenPhotoDialog
import com.proto.type.chat.dialog.ReportActionDialog

class UserProfileFragment: BaseFragment() {

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_user_profile

    // MARK: - Private Constants
    private val userId: String by lazy { arguments?.getString(Constants.KEY_USER_ID) ?: "" }
    private val viewModel: UserProfileViewModel by viewModel()

    // MARK: - Override Functions
    override fun initEvent() {
        super.initEvent()

        // UserModel Actions Click events
        clSendMessageItem.setOnClickListener {
            viewModel.getChatRoom(arguments?.getString(Constants.KEY_USER_ID) ?: "")
        }

        clCallNowItem.setOnClickListener onClick@ {
            if (tvPhoneNumberValue.text.isEmpty()) return@onClick
            val callPhoneNumberIntent = Intent(ACTION_CALL)
            callPhoneNumberIntent.data = Uri.parse("tel:" + tvPhoneNumberValue.text)
            startActivity(callPhoneNumberIntent)
        }

        ivProfilePhoto.setOnClickListener {
            FullScreenPhotoDialog(viewModel.user.value, false).show(childFragmentManager, "fullscreen_photo")
        }

        // Options Click events
        clBlockUserItem.setOnClickListener {
            showLoading()
            viewModel.toggleBlockUser(true) {
                hideLoading()
                if (it) showInfo(getString(R.string.success)) else showError("Unable to block this user at the moment. Please try again later.")
            }
        }

        clUnblockUserItem.setOnClickListener {
            showLoading()
            viewModel.toggleBlockUser(false) {
                hideLoading()
                if (it) showInfo(getString(R.string.success)) else showError("Unable to unblock this user at the moment. Please try again later.")
            }
        }

        clReportUserItem.setOnClickListener {
            ReportActionDialog { action ->
                val reason = when (action) {
                    ReportActionDialog.ACTION_OTHER_HARASSMENT -> ReportReasonType.otherHarass
                    ReportActionDialog.ACTION_SEXUAL_HARASSMENT -> ReportReasonType.sexualHarass
                    ReportActionDialog.ACTION_SPAM_ADVERTISING -> ReportReasonType.spamOrAd
                    else -> ReportReasonType.other
                }
                val target = ReportTargetType.user
                navigateTo(
                    R.id.action_userProfileFragment_to_reportFragment,
                    bundleOf(
                        Constants.Report.KEY_REPORT_REASON to reason,
                        Constants.Report.KEY_REPORT_TARGET to target,
                        Constants.Report.KEY_REPORT_TARGET_ID to arguments?.getString(Constants.KEY_USER_ID)
                    )
                )
            }.show(childFragmentManager, "report_action")
        }

        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }
        }
    }

    override fun initLogic() {
        super.initLogic()

        // Observe models
        if (!viewModel.isCurrentUser(userId)) {
            viewModel.blockeeIds.observe(this, Observer {
                val userId = arguments?.getString(Constants.KEY_USER_ID) ?: ""
                clBlockUserItem.visibility = if (it.contains(userId)) View.GONE else View.VISIBLE
                clUnblockUserItem.visibility = if (it.contains(userId)) View.VISIBLE else View.GONE
            })
        }
        viewModel.chat.observe(this, Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    navigateTo(
                        R.id.action_userProfileFragment_to_inboxFragment,
                        bundleOf(Constants.KEY_OPEN_ROOM_ID to (it.data as? ChatModel)?.id)
                    )
                }
                is UIState.FAILED -> {
                    showInfo(getString(R.string.txt_unexpected_error))
                }
            }
        })
        viewModel.user.observe(this, Observer {
            updateUI(it)
        })

        // Load data
        if (!viewModel.isCurrentUser(userId)) {
            viewModel.loadBlockListFromRealm()
            viewModel.getBlockList()
        }
        if (userId.isNotEmpty()) {
            viewModel.loadUserProfileFromRealm(userId)
            viewModel.getUserProfile(userId)
        }
    }

    override fun initView() {
        super.initView()
        updateUI(null)
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.blockeeIds.removeObservers(this)
        viewModel.chat.removeObservers(this)
        viewModel.user.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun toggleProfileUI(isCurrentUser: Boolean) {
        llUserActionsSection.visibility = if (isCurrentUser) View.GONE else View.VISIBLE
        tvOptionsHeader.visibility = if (isCurrentUser) View.GONE else View.VISIBLE
        llOptionsSection.visibility = if (isCurrentUser) View.GONE else View.VISIBLE
    }

    private fun updateUI(user: UserModel?) {
        user?.let {
            ivProfilePhoto.loadAvatar(it)
            toggleProfileUI(viewModel.isCurrentUser(arguments?.getString(Constants.KEY_USER_ID) ?: ""))
            tvChatQIDValue.text = it.username
            tvEmailValue.text = it.email
            tvPhoneNumberValue.text = it.phone_number
            tvUserName.text = it.display_name
            tvUserStatus.text = it.bio
        } ?: run {
            tvUserName.text = KEY_CHATQ_USER
            toggleProfileUI(isCurrentUser = true)
        }
    }
}