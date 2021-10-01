package com.proto.type.base.domain

import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository

class FindPrivateChat(private val userRepo: IUserRepository,
                      private val chatRepo: IChatRepository
): BaseUseCase<String, ChatModel>() {

    override suspend fun execute(friendId: String, callback: (ChatModel) -> Unit) {
        val userId = userRepo.getCurrentLocalUser().id
        callback.invoke(chatRepo.findPrivateChats(userId, friendId).first())
    }
}