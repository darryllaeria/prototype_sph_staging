package com.proto.type.chat.forward

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants.CHAT_CATEGORY_GROUP
import com.proto.type.base.Constants.CHAT_CATEGORY_PRIVATE
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch
import java.lang.Exception

class ForwardChatViewModel(
    private val messageRepo: IMessageRepository,
    private val chatRepo: IChatRepository
): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ForwardChatViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var groupChats = MutableLiveData<List<ChatModel>>()
    var message = MutableLiveData<MessageModel?>()
    var originGroupChats: List<ChatModel> = listOf()
    var originSingleChats: List<ChatModel> = listOf()
    var singleChats = MutableLiveData<List<ChatModel>>()

    // MARK: - Public Functions
    fun filterSearchText(text: String = "") {
        if (text.isEmpty()) {
            singleChats.postValue(originSingleChats)
            groupChats.postValue(originGroupChats)
        } else {
            singleChats.postValue(originSingleChats.filter {
                it.name.contains(text, ignoreCase = true)
            })
            groupChats.postValue(originGroupChats.filter {
                it.name.contains(text, ignoreCase = true)
            })
        }
    }

    fun getChatsFromRealm() {
        uiScope.launch {
            try {
                val allChats = chatRepo.getLocalChats()
                originSingleChats = allChats.filter { it.category == CHAT_CATEGORY_PRIVATE }
                originGroupChats = allChats.filter { it.category == CHAT_CATEGORY_GROUP }
                filterSearchText()
            } catch (e: Exception) {
                AppLog.d(ForwardChatViewModel.TAG, "Find chat failed with exception $e")
            }
        }
    }

    fun getMessageFromRealm(messageId: String) {
        if (messageId.isEmpty()) return message.postValue(null)
        uiScope.launch {
            try {
                message.postValue(messageRepo.findLocalMessage(messageId))
            } catch (e: Exception) {
                AppLog.d(ForwardChatViewModel.TAG, "Find chat failed with exception $e")
                message.postValue(null)
            }
        }
    }
}