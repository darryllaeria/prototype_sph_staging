package com.proto.type.home.ui.signup.term

import androidx.lifecycle.MutableLiveData
import com.proto.type.home.R
import com.proto.type.base.Constants
import com.proto.type.base.data.model.UserRegisterRequest
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class TermOfServiceViewModel(private val localRepo: ILocalRepository,
                             private val userRepo: IUserRepository,
                             private val deviceRepo: IDeviceRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG = TermOfServiceViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var isUserCreated = MutableLiveData<UIState>()

    // MARK: - Public Function
    fun createUser() {
        isUserCreated.postValue(UIState.LOADING())
        val form = localRepo.getSignupForm()
//        val firebaseUser = userRepo.getFirebaseUser()
        val request = UserRegisterRequest(
            form.phoneNumber,
            "",
//            firebaseUser?.photoUrl.toString(),
            form.lastName,
            "${form.firstName} ${form.lastName}".trim(),
            form.email,
            if (form.userName.isNotBlank()) form.userName else null
        )
        ioScope.launch {
            try {
                val success = userRepo.registerNewChatQUser(request)
                if (!success) {
                    AppLog.d(TAG, "Create user failed due to server.")
                    uiScope.launch { isUserCreated.postValue(UIState.FAILED(R.string.txt_err_create_user)) }
                } else {
                    deviceRepo.registerDevice()
                    uiScope.launch {
                        localRepo.authorized()
                        isUserCreated.postValue(UIState.FINISHED(true))
                    }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Create user failed with exception: $e")
                uiScope.launch { isUserCreated.postValue(UIState.FAILED(R.string.txt_err_create_user)) }
            }
        }
    }

    fun sendEmail() {
        ioScope.launch {
            userRepo.sendFirebaseEmailVerification(
                Constants.Uri.URI_VERIFIED_EMAIL,
                "com.proto.typestaging"
            )
        }
    }
}