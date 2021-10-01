package com.proto.type.profile.ui.notification

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.base_component.BaseViewModel

data class NotificationConfig(val isNotDisturb: Boolean,
                              val isShowPreview: Boolean,
                              val userSoundConfig: SoundConfig,
                              val groupSoundConfig: SoundConfig)

data class SoundConfig(val isShowNotification: Boolean,
                       val sound: Int)

class NotificationViewModel(private val localRepo: ILocalRepository): BaseViewModel() {

    var uiModel = MutableLiveData<NotificationConfig>()

    fun loadNotificationConfig() {
        val config = NotificationConfig(isNotDisturb = localRepo.getIsNotDisturb(),
            isShowPreview = localRepo.getIsShowPreview(),
            userSoundConfig = SoundConfig(isShowNotification = localRepo.isUserNotification(), sound = localRepo.getUserSound()),
            groupSoundConfig = SoundConfig(isShowNotification = localRepo.isGroupNotification(), sound = localRepo.getGroupSound()))

        uiModel.postValue(config)
    }

    fun setDisturb(isDisturb: Boolean) {
        localRepo.setDisturb(isDisturb)
    }

    fun setShowPreview(isShowPreview: Boolean) {
        localRepo.setShowPreview(isShowPreview)
    }

    fun setUserShowNotification(isShowNotification: Boolean) {
        localRepo.setUserShowNotification(isShowNotification)
    }

    fun setGroupShowNotification(isShowNotification: Boolean) {
        localRepo.setGroupShowNotification(isShowNotification)
    }
}