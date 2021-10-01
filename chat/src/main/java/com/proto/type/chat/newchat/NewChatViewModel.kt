package com.proto.type.chat.newchat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.extension.getAllLocalContacts
import com.proto.type.base.extension.getDeviceID
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewChatViewModel(private val context: Context,
                       private val userRepo: IUserRepository,
                       private val chatRepo: IChatRepository
): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = NewChatViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var chat = MutableLiveData<UIState>()
    var privateParticipant = MutableLiveData<UserModel>()
    var users = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun getAllContacts() {
        users.postValue(UIState.LOADING())
        ioScope.launch {
            delay(500)
            val contacts = context.getAllLocalContacts()
            try {
                userRepo.syncContacts(contacts)
                val success = userRepo.loadContacts(context.getDeviceID())
                if (success) {
                    users.postValue(UIState.FINISHED(userRepo.getLocalUsers().sortedBy { it.displayingName() }))
                } else {
                    AppLog.d(TAG, "Get all users failed due to server error.")
                    users.postValue(UIState.FINISHED(userRepo.getInContactLocalUsers()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get all contacts failed with exception: $e")
                users.postValue(UIState.FINISHED(userRepo.getInContactLocalUsers()))
            }
        }
    }

    fun getChat(userId: String) {
        ioScope.launch {
            try {
                val currentUserId = userRepo.getCurrentLocalUser().id
                val privateChat = chatRepo.findPrivateChats(currentUserId, userId)
                if (privateChat.isNullOrEmpty()) {
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
                    chat.postValue(UIState.FINISHED(privateChat.first()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get chat info failed with exception: $e")
                chat.postValue(UIState.FAILED())
            }
        }
    }

    fun searchUserList(name: String?) {
        uiScope.launch {
            try {
                if (name.isNullOrEmpty()) {
                    users.postValue(users.value)
                } else {
                    val finalUsers = ((users.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()
                    users.postValue(UIState.FINISHED(finalUsers?.filter { it.displayingName().contains(name, true) }?.toMutableList()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Search users list failed with exception: $e")
                users.postValue(UIState.FAILED())
            }
        }
    }

    fun setPrivateParticipant(participant: UserModel) {
        privateParticipant.postValue(participant)
    }
}
