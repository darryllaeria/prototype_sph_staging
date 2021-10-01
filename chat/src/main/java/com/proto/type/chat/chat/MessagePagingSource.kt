package com.proto.type.chat.chat

import androidx.paging.ItemKeyedDataSource
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MessagePagingSource(private val scope: CoroutineScope,
                          private val roomId: String,
                          private val messageRepo: IMessageRepository): ItemKeyedDataSource<Double, MessageModel>() {
    override fun loadInitial(params: LoadInitialParams<Double>, callback: LoadInitialCallback<MessageModel>) {
        scope.launch {
            val messages = messageRepo.findLocalMessagesOlderThan(roomId, DateTimeUtils.getCurrentTsForIos())
            callback.onResult(messages)
        }
    }

    override fun loadAfter(params: LoadParams<Double>, callback: LoadCallback<MessageModel>) {
        scope.launch {
            val messages = messageRepo.findLocalMessagesGreatThan(roomId, params.key)
            callback.onResult(messages)
        }
    }

    override fun loadBefore(params: LoadParams<Double>, callback: LoadCallback<MessageModel>) {
        scope.launch {
            val messages = messageRepo.findLocalMessagesOlderThan(roomId, params.key)
            callback.onResult(messages)
        }
    }

    override fun getKey(item: MessageModel) = item.sent_ts
}