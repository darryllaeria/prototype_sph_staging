package com.proto.type.base.domain

import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.repository.message.IMessageRepository

data class GetMessageParam(val roomId: String,
                           val endTs: Double,
                           val pageSize: Int)

class GetMessages(private val messageRepo: IMessageRepository): BaseUseCase<GetMessageParam, List<MessageModel>>() {

    override suspend fun execute(param: GetMessageParam, callback: (List<MessageModel>) -> Unit) {
        val (success, lastMessageTs) = messageRepo.loadMessages(param.roomId, param.pageSize, param.endTs)
        if (success) {
            callback.invoke(messageRepo.findLocalMessages(param.roomId))
        } else {
            callback.invoke(emptyList())
        }
    }
}