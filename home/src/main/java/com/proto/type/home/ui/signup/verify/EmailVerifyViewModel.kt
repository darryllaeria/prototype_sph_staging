package com.proto.type.home.ui.signup.verify

import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.repository.user.IUserRepository
import kotlinx.coroutines.launch

class EmailVerifyViewModel(
//    private val firebaseService: FirebaseService,
    private val userRepo: IUserRepository
): BaseViewModel() {

    // MARK: - Public Functions
    fun checkForMarketDataUpdate() {
//        firebaseService.checkForMarketDataUpdate()
    }

    fun resendEmail() {
        ioScope.launch {
            userRepo.sendFirebaseEmailVerification(
                Constants.Uri.URI_VERIFIED_EMAIL,
                "com.proto.typestaging"
            )
        }
    }
}