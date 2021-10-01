package com.proto.type.profile.ui.email.inbox

import com.proto.type.base.Constants
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import kotlinx.coroutines.launch

class CheckInboxViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    fun sendVerifyEmail() {
        ioScope.launch {
            userRepo.sendFirebaseEmailVerification(
                Constants.Uri.URI_VERIFIED_EMAIL,
                "com.proto.typestaging"
            )
        }
    }
}