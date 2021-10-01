package com.proto.type.chat.userprofile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.database.entity.UserEntityKey
import com.proto.type.base.data.model.UserJsonKey
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.getDeviceID
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception

class UserProfileViewModel(private val context: Context,
                           private val chatRepo: IChatRepository,
                           private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = UserProfileViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    val chat = MutableLiveData<UIState>()
    val blockeeIds = MutableLiveData<List<String>>()
    val user = MutableLiveData<UserModel?>()

    // MARK: - Private Variable
    private var currentUser = userRepo.getCurrentLocalUser()

    // MARK: - Public Functions
    fun getChatRoom(userId: String) {
        ioScope.launch {
            try {
                val currentUserId = currentUser.id
                val privateRoom = chatRepo.findPrivateChats(currentUserId, userId)

                if (privateRoom.isNullOrEmpty()) {
                    val chatId = chatRepo.createNewChat(
                        currentUserId,
                        Constants.CHAT_CATEGORY_PRIVATE,
                        "$currentUserId.$userId",
                        "",
                        listOf(currentUserId, userId),
                        null
                    )
                    val chatModel = chatRepo.findChat(chatId)
                    if (chatModel != null)
                        chat.postValue(UIState.FINISHED(chatModel))
                    else {
                        AppLog.d(TAG, "Create new chat failed due to realm finding or server error")
                        chat.postValue(UIState.FAILED())
                    }
                } else {
                    chat.postValue(UIState.FINISHED(privateRoom.first()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get chat info failed with exception: $e")
                chat.postValue(UIState.FAILED())
            }
        }
    }

    fun isCurrentUser(userId: String): Boolean = user.value?.is_default_user ?: currentUser.id == userId

    fun loadUserProfileFromRealm(userId: String) {
        try {
            user.postValue(userRepo.findLocalUser(UserEntityKey.id, userId))
        } catch (e: Exception) {
            AppLog.d(TAG, "Load user profile from Realm failed with exception: $e")
            user.postValue(null)
        }
    }

    fun getUserProfile(userId: String) {
        ioScope.launch {
            try {
                val success = userRepo.loadUser(userId, filter = mutableMapOf(UserJsonKey.device_id to context.getDeviceID()))
                if (success)
                    loadUserProfileFromRealm(userId)
            } catch (e: Exception) {
                AppLog.d(TAG, "Get user profile failed with exception: $e")
            }
        }
    }

    fun getBlockList() {
        ioScope.launch {
            try {
                if (userRepo.loadCurrentUserBlockingData())
                    loadBlockListFromRealm()
            } catch (e: Exception) {
                AppLog.d(TAG, "Get block list failed with exception: $e")
            }
        }
    }

    fun loadBlockListFromRealm() {
        currentUser = userRepo.getCurrentLocalUser()
        blockeeIds.postValue(currentUser.blockee_ids)
    }

    fun toggleBlockUser(isBlocking: Boolean, callback: SuccessCallback) {
        user.value?.id?.let { userId ->
            ioScope.launch {
                try {
                    val success = if (isBlocking) userRepo.blockUser(userId) else userRepo.unblockUser(userId)
                    if (success) {
                        val newBlockeeIds = currentUser.blockee_ids.toMutableList()
                        if (isBlocking) (if (!newBlockeeIds.contains(userId)) newBlockeeIds.add(userId)) else newBlockeeIds.remove(userId)
                        currentUser.blockee_ids = newBlockeeIds
                        val saveSuccess = userRepo.saveUserJson(JSONObject(mapOf(UserJsonKey.id to currentUser.id, UserJsonKey.blockee_ids to newBlockeeIds)))
                        if (saveSuccess) { loadBlockListFromRealm() }
                        callback(saveSuccess)
                    } else {
                        callback.invoke(false)
                    }
                } catch (e: Exception) {
                    AppLog.d(TAG, "Block a user failed with exception: $e")
                    callback.invoke(false)
                }
            }
        }
    }
}