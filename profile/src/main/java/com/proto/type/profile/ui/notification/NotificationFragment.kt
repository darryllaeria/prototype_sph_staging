package com.proto.type.profile.ui.notification

import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import com.proto.type.profile.dialog.MuteDialog
import kotlinx.android.synthetic.main.fragment_notification.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_notification
    private val viewModel: NotificationViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        itvDisturb.subscribeOnCheckChanged {
            if (it) {
                MuteDialog()
                    .show(childFragmentManager, "muted")
            }
        }

        ipvSound.setOnClickListener {
            navigateTo(R.id.action_notification_to_sound, bundleOf("soundType" to "private"))
        }

        ipvGrSound.setOnClickListener {
            navigateTo(R.id.action_notification_to_sound, bundleOf("soundType" to "group"))
        }

        itvDisturb.subscribeOnCheckChanged {
            viewModel.setDisturb(it)
        }

        itvPreviews.subscribeOnCheckChanged {
            viewModel.setShowPreview(it)
        }

        itvNotification.subscribeOnCheckChanged {
            viewModel.setUserShowNotification(it)
        }

        itvGrNotification.subscribeOnCheckChanged {
            viewModel.setGroupShowNotification(it)
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.uiModel.observe(this, Observer{
            itvDisturb.setChecked(it.isNotDisturb)
            itvPreviews.setChecked(it.isShowPreview)
            itvNotification.setChecked(it.userSoundConfig.isShowNotification)
            itvGrNotification.setChecked(it.groupSoundConfig.isShowNotification)
        })
        viewModel.loadNotificationConfig()
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.uiModel.removeObservers(this)
    }
}