package com.proto.type.base.domain

import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog

class GetChatParticipants(private val chatRepo: IChatRepository,
                          private val userRepo: IUserRepository): BaseUseCase<ChatModel, List<UserModel>>() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = GetChatParticipants::class.java.simpleName
    }

    // MARK: - Override Function
    override suspend fun execute(param: ChatModel, callback: (List<UserModel>) -> Unit)  {
        val user = userRepo.getCurrentLocalUser()
        try {
            if (param.category == Constants.CHAT_CATEGORY_PRIVATE) {
                param.participant_ids?.filter { !it.contains(user.id) }?.let { ids ->
                    userRepo.loadUsers(userIds = ids, shouldFill = true)
                    callback.invoke(userRepo.findLocalUsers(ids.toTypedArray()) ?: emptyList())
                } ?: run {
                    callback.invoke(emptyList())
                }
            } else {
                if (chatRepo.loadChat(param.id)) {
                    chatRepo.findChat(param.id)?.participant_ids?.filter { !it.contains(user.id) }?.let { ids ->
                        userRepo.loadUsers(userIds = ids, shouldFill = true)
                        callback.invoke(userRepo.findLocalUsers(ids.toTypedArray()) ?: emptyList())
                    }
                } else {
                    AppLog.d(TAG, "Failed to get participants due to server error")
                    callback.invoke(emptyList())
                }
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Failed to get participants with exception: $e")
            callback.invoke(emptyList())
        }
    }
}