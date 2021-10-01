package com.proto.type.chat.creategroup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val userRepo: IUserRepository,
    private val chatRepo: IChatRepository
): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = CreateGroupViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var users = MutableLiveData<UIState>()
    var newChat = MutableLiveData<UIState>()

    // MARK: - Private Constant
    private val myProfile = userRepo.getCurrentLocalUser()

    // MARK: - Public Functions
    fun createChatRoom(avatarUri: Uri?, roomName: String, participantIds: ArrayList<String>?) {
        newChat.postValue(UIState.LOADING())
        ioScope.launch {
//            val roomId = NewImagePurpose.GROUP_CHAT_AVATAR.generateUniqueImageId(myProfile.id, Constants.Message.ROOM_NEW)
            val roomId = ""
            participantIds?.add(0, myProfile.id)
            try {
                if (avatarUri != null) {
//                    chatRepo.uploadChatAvatarToFirebase(avatarUri, roomId, roomId, FileDataType.JPEG).apply {
//                        if (this.first != null && this.second != null) {
//                            val avatar = HashMap<String, String>()
//                            avatar["id"] = this.first!!
//                            avatar["url"] = this.second!!
//                            val chatId = chatRepo.createNewChat(myProfile.id, Constants.CHAT_CATEGORY_GROUP, this.first!!, roomName, participantIds?.toList()!!, avatar)
//                            val chatModel = chatRepo.findChat(chatId)
//                            if (chatModel != null)
//                                newChat.postValue(UIState.FINISHED(data = chatModel))
//                            else {
//                                AppLog.d(TAG, "Create new chat failed due to realm finding or server error")
//                                newChat.postValue(UIState.FAILED())
//                            }
//                        }
//                    }
                } else {
                    val chatId = chatRepo.createNewChat(myProfile.id, Constants.CHAT_CATEGORY_GROUP, roomId, roomName, participantIds?.toList()!!, null)
                    val chatModel = chatRepo.findChat(chatId)
                    if (chatModel != null)
                        newChat.postValue(UIState.FINISHED(chatModel))
                    else {
                        AppLog.d(TAG, "Create new chat failed due to realm finding or server error")
                        newChat.postValue(UIState.FAILED())
                    }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Create chat room failed with exception: $e")
                newChat.postValue(UIState.FAILED())
            }
        }
    }

    fun getUserList(isSelfChat: Boolean, participantIds: ArrayList<String>?) {
        if (isSelfChat) {
            users.postValue(UIState.FINISHED(mutableListOf(myProfile)))
        } else {
            ioScope.launch {
                try {
                    val participantList = userRepo.findLocalUsers(participantIds?.toTypedArray() ?: arrayOf())?.toMutableList()
                    participantList?.add(0, myProfile)
                    users.postValue(UIState.FINISHED(participantList))
                } catch (e: Exception) {
                    AppLog.d(TAG, "Get users list failed with exception: $e")
                    users.postValue(UIState.FAILED())
                }
            }
        }
    }

    fun setRemoveUser(index: Int) {
        when (val users = users.value) {
            is UIState.FINISHED<*> -> {
                (users.data as? MutableList<UserModel>)?.let { it.removeAt(index) }
            }
        }
    }
}
