package com.proto.type.chat.participants

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch
import java.lang.Exception

class ParticipantsViewModel(private val chatRepo: IChatRepository,
                            private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ParticipantsViewModel::class.java.simpleName
    }

    // MARK: - Private Constant
    private val myProfile = userRepo.getCurrentLocalUser()

    // MARK: - Public Variables
    var adminsList = MutableLiveData<MutableList<UserModel>>()
    var chat = MutableLiveData<ChatModel?>()
    var participantsList = MutableLiveData<MutableList<UserModel>>()

    // MARK: - Public Functions
    fun addNewAdmin(admin: UserModel) {
        val finalAdmins = adminsList.value?.toMutableList()
        finalAdmins?.add(admin)
        val finalAdminIds = finalAdmins?.map { it.id } ?: listOf()

        // Update participants list
        handleParticipantsWithAdminsValue(participantsList.value, finalAdmins, finalAdminIds)

        // Update chat object
        handleChatWithAdminsValue(chat.value, finalAdminIds)
    }

    fun getChatFromRealm(roomId: String) {
        try {
            uiScope.launch {
                chat.postValue(chatRepo.findChat(roomId))
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Find chat failed with exception $e")
            chat.postValue(null)
        }
    }

    fun getParticipantsAndAdminsFromRealm(userIds: Array<String>, adminIds: List<String>) {
        if (userIds.isEmpty()) return postDefaultValue()
        try {
            uiScope.launch {
                val participants = userRepo.findLocalUsers(userIds)
                val admins = participants?.filter { adminIds.contains(it.id) }
                handleParticipantsWithAdminsValue(participants, admins, admins?.map { it.id } ?: listOf())
            }
        } catch (e: Exception) {
            postDefaultValue()
            AppLog.d(TAG, "Get users list failed with exception $e")
        }
    }

    fun isAdmin(user: UserModel): Boolean = (chat.value?.admin_ids ?: listOf()).contains(user.id)

    fun isCurrentUser(user: UserModel): Boolean = user.id == myProfile.id

    fun isCurrentUserAdmin(): Boolean = isAdmin(myProfile)

    fun promoteDemoteUser(roomId: String = "", userId: String = "", callback: SuccessCallback) {
        ioScope.launch {
            try {
                val success = chatRepo.promoteDemoteUser(roomId, userId, adminsList.value?.map { it.id }?.contains(userId) ?: false)
                callback.invoke(success)
            } catch (e: Exception) {
                AppLog.d(TAG, "Promote demote user failed with exception $e")
                callback.invoke(false)
            }
        }
    }

    fun removeCurrentAdmin(admin: UserModel) {
        val finalAdmins = adminsList.value?.toMutableList()
        finalAdmins?.remove(admin)
        val finalAdminIds = finalAdmins?.map { it.id } ?: listOf()

        // Update participants list
        handleParticipantsWithAdminsValue(participantsList.value, finalAdmins, finalAdminIds)

        // Update chat object
        handleChatWithAdminsValue(chat.value, finalAdminIds)
    }

    fun removeChatMembers(roomId: String, userIds: List<String>, callback: SuccessCallback) {
        ioScope.launch {
            try {
                val success = chatRepo.removeParticipants(roomId, userIds)
                if (success) { removeCurrentParticipants(userIds) }
                uiScope.launch { callback.invoke(success) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Remove chat members failed with exception $e")
                uiScope.launch { callback.invoke(false) }
            }
        }
    }

    // MARK: - Private Functions
    private fun handleChatWithAdminsValue(chatRoom: ChatModel?, newAdminIds: List<String>, newParticipantIds: List<String>? = null) {
        chatRoom?.admin_ids = newAdminIds
        newParticipantIds?.let { chatRoom?.participant_ids = newParticipantIds }
        chat.postValue(chatRoom)
        uiScope.launch { chatRoom?.let { chatRepo.storeChat(it) } }
    }

    private fun handleParticipantsWithAdminsValue(participants: List<UserModel>?, admins: List<UserModel>?, adminIds: List<String>) {
        val sortedAdmins = admins?.sortedBy { it.display_name }?.toMutableList()
        adminsList.postValue(sortedAdmins)
        val sortedParticipants = sortedAdmins?.toMutableList()
        sortedParticipants?.addAll(participants?.filter { !adminIds.contains(it.id) }?.sortedBy { it.display_name }?.toMutableList() ?: mutableListOf())
        participantsList.postValue(sortedParticipants)
    }

    private fun postDefaultValue() {
        adminsList.postValue(mutableListOf())
        participantsList.postValue(mutableListOf())
    }

    private fun removeCurrentParticipants(userIds: List<String>) {
        val finalAdmins = adminsList.value?.filter { !userIds.contains(it.id) } ?: listOf()
        val finalAdminIds = finalAdmins.map { it.id }
        val newParticipants = participantsList.value?.filter { !userIds.contains(it.id) }

        // Update participants list
        handleParticipantsWithAdminsValue(newParticipants, finalAdmins, finalAdminIds)

        // Update chat object
        handleChatWithAdminsValue(chat.value, finalAdminIds, newParticipants?.map { it.id })
    }
}