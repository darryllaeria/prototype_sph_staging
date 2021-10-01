package com.proto.type.profile.ui.email.request

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import kotlinx.coroutines.launch

class EmailVerifyViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    var uiModel = MutableLiveData<UIState>()

    fun sendVerifyRequest() {
        ioScope.launch {
            userRepo.sendFirebaseEmailVerification(
                Constants.Uri.URI_VERIFIED_EMAIL,
                "com.proto.typestaging"
            )
        }
    }
}