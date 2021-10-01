package com.proto.type.main.ui.login.email

import androidx.lifecycle.MutableLiveData
import com.proto.type.main.R
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.manager.ChatMessagesManager
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class EmailViewModel(
    private val chatMessagesManager: ChatMessagesManager,
    private val deviceRepo: IDeviceRepository,
//    private val firebaseService: FirebaseService,
    private val localRepo: ILocalRepository,
    private val userRepo: IUserRepository
) : BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG = EmailViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var user = MutableLiveData<UIState>()

    // MARK: - Public Function
    fun signIn(email: String, password: String) {
        user.postValue(UIState.LOADING())
        ioScope.launch {
            try {
                val userId = userRepo.login(email, password)
                if (userId.isEmpty()) {
                    user.postValue(UIState.FAILED(R.string.txt_err_login_failed))
                } else {
                    val success = userRepo.loadUser(userId, filter = null)
                    if (success) {
                        val loadedUser = userRepo.findLocalUser(userId)
                        if (loadedUser != null) {
                            loadedUser.is_default_user = true
                            userRepo.saveUserModel(loadedUser)
                            runBlocking {
                                deviceRepo.registerDevice()
                                localRepo.authorized()
                                val userKey = userRepo.loadCurrentUserEncryptionKey()
                                chatMessagesManager.subscribe(userId, userKey)
//                                firebaseService.checkForMarketDataUpdate()
                            }
                            user.postValue(UIState.FINISHED(loadedUser))
                        } else {
                            AppLog.d(TAG, "Sign in failed due to no local user object")
                            user.postValue(UIState.FAILED(R.string.txt_err_login_failed))
                        }
                    } else {
                        AppLog.d(TAG, "Sign in failed due to server error")
                        user.postValue(UIState.FAILED(R.string.txt_err_login_failed))
                    }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Sign in failed with exception: $e")
                user.postValue(UIState.FAILED(R.string.txt_err_login_failed))
            }
        }
    }
}