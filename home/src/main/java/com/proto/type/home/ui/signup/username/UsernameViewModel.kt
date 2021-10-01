package com.proto.type.home.ui.signup.username

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.manager.PrefsManager
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class UsernameViewModel(//private val firebaseService: FirebaseService,
                        private val localRepo: ILocalRepository,
                        private val userRepo: IUserRepository,
                        private val prefsManager: PrefsManager): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG = UsernameViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var isChatQIdExist = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun checkUserName(userName: String) {
        isChatQIdExist.postValue(UIState.LOADING())
        ioScope.launch {
            try {
                isChatQIdExist.postValue(UIState.FINISHED(userRepo.checkChatQUsernameExist(userName)))
            } catch (e: Exception) {
                AppLog.d(TAG, "Check username failed with exception: $e")
//                val result = firebaseService.refreshToken()
//                result.apply {
//                    prefsManager.putString(Constants.KEY_AUTH_TOKEN, result?.token!!)
//                    prefsManager.putLong(Constants.KEY_TOKEN_EXPIRATION, result.expirationTimestamp)
//                }
                isChatQIdExist.postValue(UIState.FAILED())
            }
        }
    }

    fun storeUserName(userName: String) {
        localRepo.storeUsername(username = userName)
    }
}