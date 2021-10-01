package com.proto.type.profile.ui.phone.otp

import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import kotlinx.coroutines.launch

class OtpVerifyViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    fun requestOtp(number: String) {
        userRepo.verifyFirebasePhoneNumber(number) {
            if (it) {
                ioScope.launch {
                    userRepo.updateLocalUserPhone(number)
                }
            } else {
                // TODO("Handle verify phone number failed")
            }
        }

    }

}