package com.proto.type.chat.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.getAllLocalContacts
import com.proto.type.base.extension.getDeviceID
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchChatViewModel(private val context: Context,
                          private val userRepo: IUserRepository,
                          private val messageRepo: IMessageRepository,
                          private val chatRepo: IChatRepository
): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = SearchChatViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var chat = MutableLiveData<UIState>()
    var groupChats = MutableLiveData<UIState>()
    val hashMapMessages = MutableLiveData<UIState>()
    var privateParticipant = MutableLiveData<UserModel>()
    var seeMoreContacts = MutableLiveData(false)
    var seeMoreGroups = MutableLiveData(false)
    var seeMoreMessages = MutableLiveData(false)
    var users = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun getAllSearch() {
        getAllUsers()
        getRoomGroupByKeyword("")
        getMessageByKeyword("")
    }

    fun getAllSearchByKeyword(keyword: String) {
        searchUserList(keyword)
        getRoomGroupByKeyword(keyword)
        getMessageByKeyword(keyword)
    }

    fun getChatRoom(userId: String) {
        ioScope.launch {
            try {
                val currentUserId = userRepo.getCurrentLocalUser().id
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
                AppLog.d(TAG, "Get chat room info failed with exception: $e")
                chat.postValue(UIState.FAILED())
            }
        }
    }

    fun getMessageByKeyword(keyword: String) {
        uiScope.launch {
            try {
                val messageList = messageRepo.findLocalMessagesByFieldName("dataValue", keyword)
                val roomIds = arrayListOf<String>()
                val roomIdsHash = mutableListOf<HashMap<String, Any>>()
                messageList.forEach {
                    it.room_id?.let { it1 -> roomIds.add(it1) }
                }.also {
                    for (roomId in HashSet<String>(roomIds)) {
                        val roomMap = HashMap<String, Any>()
                        val chatRoom = chatRepo.findChat(roomId)
                        roomMap[Constants.KEY_ROOM_ID] = roomId
                        roomMap[Constants.SEARCH_ROOM_SIZE_KEY] = messageList.filter { it.room_id == roomId }.size
                        roomMap[Constants.KEY_ROOM_AVATAR] = chatRoom?.avatar?.url.toString()
                        roomMap[Constants.KEY_ROOM_NAME] = chatRoom?.name.toString()
                        roomIdsHash.add(roomMap)
                    }
                    hashMapMessages.postValue(UIState.FINISHED(roomIdsHash))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get messages failed with exception: $e")
                hashMapMessages.postValue(UIState.FAILED())
            }
        }
    }

    fun getAllUsers() {
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
                AppLog.d(TAG, "Get all users failed with exception: $e")
                users.postValue(UIState.FINISHED(userRepo.getInContactLocalUsers()))
            }
        }
    }

    fun getRoomGroupByKeyword(keyword: String) {
        uiScope.launch {
            try {
                if (chatRepo.loadChats()) {
                    groupChats.postValue(
                        UIState.FINISHED(
                            chatRepo.getLocalChats().sortedByDescending { it.last_message?.sent_ts }.filter {
                                it.category == Constants.CHAT_CATEGORY_GROUP && (it.name ?: "").contains(keyword, true)
                            }.toMutableList()
                        )
                    )
                } else {
                    AppLog.d(TAG, "Get chats groups failed due to server error.")
                    groupChats.postValue(UIState.FAILED())
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get chats groups failed with exception: $e")
                groupChats.postValue(UIState.FAILED())
            }
        }
    }

    fun searchUserList(name: String?) {
        uiScope.launch {
            try {
                if (name.isNullOrEmpty()) {
                    users.postValue(users.value)
                } else {
                    val currentUsers = (users.value as? UIState.FINISHED<MutableList<UserModel>>)?.data
                    users.postValue(UIState.FINISHED(currentUsers?.filter { it.displayingName().contains(name, true) }?.toMutableList()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Search users failed with exception: $e")
                users.postValue(UIState.FAILED())
            }
        }
    }

    fun setPrivateParticipant(participant: UserModel) {
        privateParticipant.postValue(participant)
    }

    fun setSeeMoreContacts(flag: Boolean) {
        seeMoreContacts.postValue(flag)
    }

    fun setSeeMoreGroups(flag: Boolean) {
        seeMoreGroups.postValue(flag)
    }

    fun setSeeMoreMessages(flag: Boolean) {
        seeMoreMessages.postValue(flag)
    }
}