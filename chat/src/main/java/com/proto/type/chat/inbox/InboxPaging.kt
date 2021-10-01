package com.proto.type.chat.inbox

import androidx.paging.PageKeyedDataSource
import com.proto.type.base.Constants
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.repository.chat.IChatRepository
import kotlinx.coroutines.CoroutineScope

class InboxPaging(private val scope: CoroutineScope,
                  private val chatRepo: IChatRepository
): PageKeyedDataSource<Int, ChatModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ChatModel>
    ) {
        callback.onResult(chatRepo.getLocalChats().filterNot {
            it.category?.contains(Constants.CHAT_CATEGORY_BOT_DASHBOARD)!!
                    && it.category?.contains(Constants.CHAT_CATEGORY_DASHBOARD)!!
        }, 0, 0)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ChatModel>) { }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ChatModel>) { }
}