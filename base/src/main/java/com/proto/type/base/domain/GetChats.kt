package com.proto.type.base.domain

import com.proto.type.base.DispatchGroup
import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.repository.chat.IChatRepository

class GetChats(private val getChatParticipants: GetChatParticipants,
               private val chatRepo: IChatRepository
): BaseUseCase<Any, List<ChatModel>>() {

    override suspend fun execute(param: Any, callback: (List<ChatModel>) -> Unit) {
        if (chatRepo.loadChats()) {
            val chatModels = chatRepo.getLocalChats()
            val dispatchGroup = DispatchGroup()
            dispatchGroup.enter()
            chatModels.forEach {
                getChatParticipants.invoke(it) {
                    dispatchGroup.leave()
                }
            }
            dispatchGroup.notify {
                callback.invoke(chatModels)
            }
        } else {
            callback.invoke(listOf())
        }
    }
}
