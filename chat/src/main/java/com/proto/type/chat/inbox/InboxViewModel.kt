package com.proto.type.chat.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.proto.type.base.Constants
import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InboxViewModel(
    private val messageRepo: IMessageRepository,
    private val chatRepo: IChatRepository,
    private val userRepo: IUserRepository
) : BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = InboxViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var chats: LiveData<PagedList<ChatModel>> = MutableLiveData()
    var isFirstTime = true
    var loadingChatsState = MutableLiveData<UIState>(UIState.INIT)

    // MARK: - Private Constant
    private val myProfile by lazy { userRepo.getCurrentLocalUser() }

    // MARK: - Public Functions
    fun checkIsLastAdmin(chat: ChatModel): Boolean = chat.category != Constants.CHAT_CATEGORY_PRIVATE && chat.admin_ids.size == 1 && isCurrentUserAdmin(chat) && chat.participant_ids?.size ?: 0 > 1

    fun getChatDetails(roomId: String, callback: (ChatModel?) -> Unit) {
        if (roomId.isEmpty()) return callback.invoke(null)
        ioScope.launch {
            try {
                if (chatRepo.loadChat(roomId)) {
                    val chatModel = chatRepo.findChat(roomId)
                    if (chatModel != null)
                        callback.invoke(chatModel)
                    else {
                        AppLog.d(TAG, "Loading chat info failed due to server error")
                        callback.invoke(null)
                    }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Getting chat room info failed with exception: $e")
                callback.invoke(null)
            }
        }
    }

    fun getChatList() {
        loadingChatsState.postValue(UIState.LOADING())
        chats = LivePagedListBuilder(object: DataSource.Factory<Int, ChatModel>() {
            override fun create(): DataSource<Int, ChatModel> {
                return InboxPaging(uiScope, chatRepo)
            }
        }, Constants.Message.PAGE_SIZE).setBoundaryCallback(object: PagedList.BoundaryCallback<ChatModel>() {
            override fun onZeroItemsLoaded() {
                if (isFirstTime) {
                    loadRooms()
                    isFirstTime = false
                } else {
                    loadingChatsState.postValue(UIState.FINISHED(emptyList<ChatModel>()))
                }
            }

            override fun onItemAtFrontLoaded(itemAtFront: ChatModel) {
                loadingChatsState.postValue(UIState.DONE)
            }

            override fun onItemAtEndLoaded(itemAtEnd: ChatModel) {
                loadingChatsState.postValue(UIState.DONE)
            }
        }).build()
    }

    fun getOtherUserInPrivateChat(chat: ChatModel): UserModel? {
        if (chat.category != Constants.CHAT_CATEGORY_PRIVATE) return null
        return try {
            val participants = userRepo.findLocalUsers(chat.participant_ids?.toTypedArray() ?: arrayOf())
            participants?.first { it.id != myProfile.id }
        } catch (e: Exception) {
            AppLog.d(TAG, "Get other user in private chat failed with exception: $e")
            null
        }
    }

    fun getParticipants(userIds: List<String>, callback: SuccessCallback) {
        if (userIds.isEmpty()) return callback.invoke(false)
            ioScope.launch {
                try {
                    callback.invoke(userRepo.loadUsers(userIds = userIds, shouldFill = true))
                } catch (e: Exception) {
                    AppLog.d(TAG, "Get participants failed with exception: $e")
                    callback.invoke(false)
                }
            }
    }

    fun leaveChat(roomId: String = "") {
        ioScope.launch {
            try {
                val isLeaveSuccess = chatRepo.leaveChat(roomId, listOf(myProfile.id))
                if (isLeaveSuccess) {
                    withContext(Dispatchers.Default) {
                        messageRepo.deleteLocalMessages(roomId)
                        chatRepo.deleteChat(roomId)
                    }
                    chats.value?.dataSource?.invalidate()
                } else {
                    AppLog.d(TAG, "Leave chat from service failed due to server.")
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Failed to leave chat with exception: $e")
                // TODO("Handle exception")
            }
        }
    }

    fun updateRoomMute(muteNotification: Boolean = false, roomId: String = "") {
        ioScope.launch {
            try {
                val isUpdateMuteSuccessful = chatRepo.updateChatMuteNotification(roomId, muteNotification)
                if (isUpdateMuteSuccessful) {
                    chats.value?.dataSource?.invalidate()
                } else {
                    AppLog.d(TAG, "Update mute status failed due to server.")
                    // TODO("Update mute failed")
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Update mute status failed with exception: $e")
            }
        }
    }

    fun updateRoomPin(isPinned: Boolean = false, roomId: String = "") {
        ioScope.launch {
            try {
                val isUpdatePinSuccessful = chatRepo.updateChatIsPinned(roomId, isPinned)
                if (isUpdatePinSuccessful) {
                    chats.value?.dataSource?.invalidate()
                } else {
                    AppLog.d(TAG, "Update pin status failed due to server.")
                    // TODO("Update pin failed")
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Update pin status failed with exception: $e")
            }
        }
    }

    // MARK: - Private Functions
    private fun isAdmin(chat: ChatModel, user: UserModel): Boolean = chat.admin_ids.contains(user.id)

    private fun isCurrentUserAdmin(chat: ChatModel): Boolean = isAdmin(chat, myProfile)

    private fun loadRooms() {
        ioScope.launch {
            try {
                if (chatRepo.loadChats()) {
                    val chatModels = chatRepo.getLocalChats()
                    val friendIds = mutableListOf<String>()
                    chatModels.filter { it.category == Constants.CHAT_CATEGORY_PRIVATE }
                        .forEach { chatModel ->
                            chatModel.participant_ids?.filter { !it.contains(myProfile.id) }?.let {
                                it.firstOrNull()?.let { participantId ->
                                    chatModel.private_participant_id = participantId
                                    if (participantId != null) {
                                        friendIds.add(participantId)
                                    }
                                }
                            }
                        }
                    if (userRepo.loadUsers(userIds = friendIds.toList(), shouldFill = true)) {
                        val users = userRepo.findLocalUsers(friendIds.toTypedArray())
                        chatModels.filter {
                            it.category == Constants.CHAT_CATEGORY_PRIVATE
                        }.forEach { room ->
                            val privateChatUser = users?.firstOrNull { it.id == room.private_participant_id }
                            room.name = privateChatUser?.displayingName() ?: ""
                            room.avatar?.url = privateChatUser?.avatar?.url ?: ""
                        }
                        chatRepo.storeChats(chatModels)
                        loadingChatsState.postValue(UIState.DONE)
                        chats.value?.dataSource?.invalidate()
                    } else {
                        AppLog.d(TAG, "Load rooms failed due to server error on users api.")
                        chatRepo.storeChats(chatModels)
                        loadingChatsState.postValue(UIState.DONE)
                        chats.value?.dataSource?.invalidate()
                    }
                } else {
                    AppLog.d(TAG, "Load rooms failed due to server error on chats api.")
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Load rooms failed with exception $e")
            }
        }
    }
}