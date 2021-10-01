package com.proto.type.chat.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.*
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class ChatSettingsViewModel(
    private val localRepo: ILocalRepository,
    private val messageRepo: IMessageRepository,
    private val chatRepo: IChatRepository,
    private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatSettingsViewModel::class.java.simpleName
    }

    // MARK: - Public Constant
    val CHAT_REALM_MESSAGE_ID = 0

    // MARK: - Private Constant
    private val myProfile = userRepo.getCurrentLocalUser()

    // MARK: - Public Variables
    var chat = MutableLiveData<UIState>()
    var messageMediaItems = MutableLiveData<MutableList<MessageMediaItemModel>>()
    var participants = MutableLiveData<MutableList<UserModel>>()
    var sendPrivateChat = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun addNewAdmin(admin: UserModel) {
        val chat = getCurrentChat()
        val newAdminIds = chat?.admin_ids?.toMutableList()
        newAdminIds?.add(admin.id)
        val finalAdminIds = newAdminIds ?: mutableListOf(admin.id)

        // Update participants list
        handleParticipantsWithAdminsValue(participants.value, finalAdminIds)

        // Update chat object
        handleChatWithAdminsValue(chat, finalAdminIds)
    }

    fun checkIsLastAdmin(): Boolean {
        val chat = getCurrentChat()
        return chat?.admin_ids?.size == 1 && isCurrentUserAdmin() && chat.participant_ids?.size ?: 0 > 1
    }

    fun clearChatHistory(roomId: String = "", callback: SuccessCallback) {
        if (roomId.isEmpty()) return callback.invoke(false)
        ioScope.launch {
            try {
                val success = chatRepo.clearChatHistory(roomId)
                uiScope.launch { callback.invoke(success) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Clear chat history failed with exception: $e")
                callback.invoke(false)
            }
        }
    }

    fun deleteChat(roomId: String) {
        ioScope.launch {
            try {
                chatRepo.deleteChat(roomId)
            } catch (e: Exception) {
                AppLog.d(TAG, "Delete chat failed with exception: $e")
            }
        }
    }

    fun deleteChatMessages(roomId: String) {
        ioScope.launch {
            try {
                messageRepo.deleteLocalMessages(roomId)
            } catch (e: Exception) {
                AppLog.d(TAG, "Delete chat messages failed with exception: $e")
            }
        }
    }

    fun getChatCategory(): String = getCurrentChat()?.category ?: ""

    fun getChatFromRealm(roomId: String) {
        if (roomId.isEmpty()) return chat.postValue(UIState.FAILED(CHAT_REALM_MESSAGE_ID))
        try {
            uiScope.launch {
                chat.postValue(UIState.FINISHED(chatRepo.findChat(roomId), CHAT_REALM_MESSAGE_ID)) // Don't alter loading logic when get chat from Realm
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Delete get chat from Realm failed with exception: $e")
            chat.postValue(UIState.FAILED(CHAT_REALM_MESSAGE_ID)) // Don't alter loading logic when get chat from Realm
        }
    }

    fun getChatName(): String = getCurrentChat()?.name ?: ""

    fun getChatRoom(userId: String) {
        ioScope.launch {
            try {
                val currentUserId = myProfile.id
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
                        sendPrivateChat.postValue(UIState.FINISHED(chatModel))
                    else {
                        AppLog.d(TAG, "Create new chat failed due to realm finding or server error")
                        sendPrivateChat.postValue(UIState.FAILED())
                    }
                } else {
                    sendPrivateChat.postValue(UIState.FINISHED(privateRoom.first()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get chat info failed with exception: $e")
                sendPrivateChat.postValue(UIState.FAILED())
            }
        }
    }

    fun getCurrentChat(): ChatModel? = (chat.value as? UIState.FINISHED<*>)?.data as? ChatModel

    fun getInitialMessageMediaItems(roomId: String, pageSize: Int) {
        if (roomId.isEmpty()) return
        ioScope.launch {
            try {
                val response = messageRepo.loadMessageMediaItems(roomId, pageSize)
                if (response.isSuccessful) {
                    messageMediaItems.postValue(response.body()?.items)
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get initial message media items failed with exception: $e")
            }
        }
    }

    fun getOtherUserInPrivateChat(): UserModel? {
        if (getCurrentChat()?.category != Constants.CHAT_CATEGORY_PRIVATE) return null
        return participants.value?.first { it.id != myProfile.id }
    }

    fun getParticipantsIds(): List<String> = participants.value?.map { it.id }?.filter { it != myProfile.id } ?: listOf()

    fun getShowDoodleBackgroundStatus(roomId: String): Boolean {
        return localRepo.getShowDoodleBackground(roomId)
    }

    fun isAdmin(user: UserModel): Boolean = (getCurrentChat()?.admin_ids ?: listOf()).contains(user.id)

    fun isCurrentUser(user: UserModel): Boolean = user.id == myProfile.id

    fun isCurrentUserAdmin(): Boolean = isAdmin(myProfile)

    fun leaveChat(roomId: String = "", callback: SuccessCallback) {
        ioScope.launch {
            try {
                val result = chatRepo.leaveChat(roomId, listOf(myProfile.id))
                callback.invoke(result)
            } catch (e: Exception) {
                AppLog.d(TAG, "Leave chat failed with exception: $e")
                callback.invoke(false)
            }
        }
    }

    fun loadParticipants(userIds: List<String>, adminIds: List<String>) {
        if (userIds.isEmpty()) return participants.postValue(mutableListOf())
        ioScope.launch {
            try {
                if (userRepo.loadUsers(userIds = userIds, shouldFill = true))
                    handleParticipantsWithAdminsValue(userRepo.findLocalUsers(userIds.toTypedArray()), adminIds)
                else {
                    AppLog.d(TAG, "Load participants failed due to server error")
                    participants.postValue(mutableListOf())
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Load participants failed with exception: $e")
                participants.postValue(mutableListOf())
            }
        }
    }

    fun promoteDemoteUser(roomId: String = "", userId: String = "", callback: SuccessCallback) {
        ioScope.launch {
            try {
                val success = chatRepo.promoteDemoteUser(roomId, userId, getCurrentChat()?.admin_ids?.contains(userId) ?: false)
                callback.invoke(success)
            } catch (e: Exception) {
                AppLog.d(TAG, "Promote demote user failed with exception: $e")
                callback.invoke(false)
            }
        }
    }

    fun removeChatMembers(roomId: String, userIds: List<String>, callback: SuccessCallback) {
        ioScope.launch {
            try {
                val success = chatRepo.removeParticipants(roomId, userIds)
                if (success) { removeCurrentParticipants(userIds) }
                uiScope.launch { callback.invoke(success) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Remove chat members failed with exception: $e")
                uiScope.launch { callback.invoke(false) }
            }
        }
    }

    fun removeCurrentAdmin(admin: UserModel) {
        val currentChat = getCurrentChat()
        val newAdminIds = currentChat?.admin_ids?.toMutableList()
        newAdminIds?.remove(admin.id)
        val finalAdminIds = newAdminIds ?: mutableListOf()

        // Update participants list
        handleParticipantsWithAdminsValue(participants.value, finalAdminIds)

        // Update chat object
        handleChatWithAdminsValue(currentChat, finalAdminIds)
    }

    fun setMuteNotification(muted: Boolean = false, roomId: String = "") {
        val currentChat = getCurrentChat()
        chat.postValue(UIState.LOADING())
        ioScope.launch {
            try {
                chatRepo.updateChatMuteNotification(roomId, muted)
                currentChat?.mute_notification = muted
                chat.postValue(UIState.FINISHED(currentChat))
                currentChat?.let { chatRepo.storeChat(it) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Set mute notification failed with exception: $e")
                chat.postValue(UIState.FINISHED(currentChat))
            }
        }
    }

    fun toggleShowDoodleBackground(roomId: String) {
        localRepo.setShowDoodleBackground(roomId, !getShowDoodleBackgroundStatus(roomId))
    }

    fun updateRoomAvatar(uri: Uri, roomId: String) {
        val currentChat = getCurrentChat()
        chat.postValue(UIState.LOADING())
        ioScope.launch {
            try {
//                val result = chatRepo.uploadChatAvatarToFirebase(
//                    uri,
//                    roomId,
//                    NewImagePurpose.GROUP_CHAT_AVATAR.generateUniqueImageId(
//                        myProfile.id,
//                        roomId
//                    ),
//                    FileDataType.JPEG
//                )
//                if (result.first != null && result.second != null) {
//                    if (chatRepo.updateChatAvatar(roomId, result.first!!, result.second!!)) {
//                        currentChat?.avatar = AvatarModel(id = result.first, url = result.second!!)
//                        chat.postValue(UIState.FINISHED(currentChat))
//                        currentChat?.let { chatRepo.storeChat(it) }
//                    } else {
//                        chat.postValue(UIState.FINISHED(currentChat))
//                    }
//                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Update chat avatar failed with exception: $e")
                chat.postValue(UIState.FINISHED(currentChat))
            }
        }
    }

    fun updateRoomName(roomId: String, roomName: String = "") {
        val currentChat = getCurrentChat()
        chat.postValue(UIState.LOADING())
        ioScope.launch {
            try {
                chatRepo.updateChatName(roomId, roomName)
                currentChat?.name = roomName
                chat.postValue(UIState.FINISHED(currentChat))
                currentChat?.let { chatRepo.storeChat(it) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Update chat name failed with exception: $e")
                chat.postValue(UIState.FINISHED(currentChat))
            }
        }
    }

    // MARK: - Private Functions
    private fun handleChatWithAdminsValue(chatRoom: ChatModel?, newAdminIds: List<String>, newParticipantIds: List<String>? = null) {
        chatRoom?.admin_ids = newAdminIds
        newParticipantIds?.let { chatRoom?.participant_ids = newParticipantIds }
        chat.postValue(UIState.FINISHED(chatRoom))
        uiScope.launch { chatRoom?.let { chatRepo.storeChat(it) } }
    }

    private fun handleParticipantsWithAdminsValue(participants: List<UserModel>?, adminIds: List<String>) {
        val sortedParticipants = participants?.filter { adminIds.contains(it.id) }?.sortedBy { it.display_name }?.toMutableList()
        sortedParticipants?.addAll(participants.filter { !adminIds.contains(it.id) }.sortedBy { it.display_name }.toMutableList())
        this.participants.postValue(sortedParticipants)
    }

    private fun removeCurrentParticipants(userIds: List<String>) {
        val chat = getCurrentChat()
        val newAdminIds = chat?.admin_ids?.filter { !userIds.contains(it) } ?: listOf()
        val newParticipants = participants.value?.filter { !userIds.contains(it.id) }?.toMutableList()

        // Update participants list
        handleParticipantsWithAdminsValue(newParticipants, newAdminIds)

        // Update chat object
        handleChatWithAdminsValue(chat, newAdminIds, newParticipants?.map { it.id })
    }
}