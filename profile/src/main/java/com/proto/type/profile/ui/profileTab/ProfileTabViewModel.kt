package com.proto.type.profile.ui.profileTab

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.AvatarModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.manager.ChatMessagesManager
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileTabViewModel(
    private val chatMessagesManager: ChatMessagesManager,
//    private val firebaseService: FirebaseService,
    private val userRepo: IUserRepository
): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ProfileTabViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var isVerifiedEmail = MutableLiveData<UIState>()
    var localUser = MutableLiveData<UIState>()


    // MARK: - Public Functions
    fun cleanupMQTTSubscriptions() {
        chatMessagesManager.unsubscribe(getLocalUser()?.id)
    }

    fun clearFirebaseToken() {
//        firebaseService.clearLocalToken()
    }

    fun getLocalUser(): UserModel? = (localUser.value as? UIState.FINISHED<*>)?.data as? UserModel

    fun loadProfile() {
        localUser.postValue(UIState.FINISHED(UserModel.generateLocalUser()))
        localUser.postValue(UIState.LOADING())
        ioScope.launch {
            isVerifiedEmail.postValue(UIState.FINISHED(withContext(Dispatchers.Default) {
                userRepo.isFirebaseEmailVerified()
            }))
            localUser.postValue(UIState.FINISHED(withContext(Dispatchers.Default) {
                userRepo.getCurrentLocalUser()
            }))
        }
    }

    fun logout(): LiveData<Boolean> = MutableLiveData(userRepo.logout())

    fun updateDisplayName(displayName: String) {
        ioScope.launch {
            try {
                val currentUser = userRepo.getCurrentLocalUser()
                if (userRepo.updateDisplayName(currentUser.id, displayName))
                    localUser.postValue(UIState.FINISHED(currentUser.copy(display_name = displayName)))
                else
                    AppLog.d(TAG, "Update display name failed due to server error")
            } catch (e: Exception) {
                AppLog.d(TAG, "Update display name failed with exception: $e")
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        val user = userRepo.getCurrentLocalUser()
        ioScope.launch {
            try {
                val url = userRepo.updateFirebaseAvatar(uri)
                if (url != null) {
                    val avatar = AvatarModel(file_name = user.display_name ?: "", url = url)
                    if (userRepo.updateChatQAvatar(user.id, avatar))
                        localUser.postValue(UIState.FINISHED(user.copy(avatar = avatar)))
                    else
                        AppLog.d(TAG, "Update avatar failed due to server error")
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Update avatar failed due to server error")
            }
        }
    }
}