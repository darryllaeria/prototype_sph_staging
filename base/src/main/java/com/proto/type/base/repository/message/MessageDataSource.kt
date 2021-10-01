package com.proto.type.base.repository.message

import androidx.paging.PageKeyedDataSource
import com.proto.type.base.Constants
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MessageDataSource(private val roomId: String, private val messageRepo: IMessageRepository): PageKeyedDataSource<Double, MessageModel>() {
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())
    private var sentTs = 0.0

    override fun loadInitial(
        params: LoadInitialParams<Double>,
        callback: LoadInitialCallback<Double, MessageModel>
    ) {
        ioScope.launch {
            try {
                val (success, oldestMessageTs) = messageRepo.loadMessages(roomId, Constants.Message.CHAT_PAGE_SIZE, DateTimeUtils.getCurrentTsForIos())
                if (success) {
                    sentTs = oldestMessageTs
                    callback.onResult(messageRepo.findLocalMessages(roomId), null, sentTs)
                }
            } catch (exp: Exception) {
                val messageModels = messageRepo.findLocalMessages(roomId)
                sentTs = messageModels.lastOrNull()?.sent_ts ?: 0.0
                callback.onResult(messageModels, null, sentTs)
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<Double>,
        callback: LoadCallback<Double, MessageModel>
    ) {
        ioScope.launch {
            try {
                val (success, oldestMessageTs) = messageRepo.loadMessages(roomId, Constants.Message.CHAT_PAGE_SIZE, sentTs)
                if (success) {
                    sentTs = oldestMessageTs
                    if (sentTs != 0.0)
                        callback.onResult(messageRepo.findLocalMessages(roomId), sentTs)
                }
            } catch (exp: Exception) {
                val messageModels = messageRepo.findLocalMessages(roomId)
                sentTs = messageModels?.lastOrNull()?.sent_ts ?: 0.0
                if (sentTs != 0.0) {
                    callback.onResult(messageModels, sentTs)
                }
            }
        }
    }

    override fun loadBefore(
        params: LoadParams<Double>,
        callback: LoadCallback<Double, MessageModel>
    ) {
        //To change body of created functions use File | Settings | File Templates.
        // TODO("not implemented")
    }
}