package com.proto.type.chat.chat

import android.app.Activity
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.proto.type.base.*
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.database.entity.UserEntity
import com.proto.type.base.data.model.*
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.domain.GetMessageParam
import com.proto.type.base.domain.GetMessages
import com.proto.type.base.extension.findMentionText
import com.proto.type.base.extension.getHighlightObjects
import com.proto.type.base.manager.ChatMessagesManager
import com.proto.type.base.manager.MarketDataManager
import com.proto.type.base.manager.TypingUsersManager
import com.proto.type.base.repository.asset_exchange.IAssetExchangeRepository
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.DateTimeUtils
import com.proto.type.base.utils.Utils
import com.proto.type.chat.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*

class ChatViewModel(
    val assetExchangeRepo: IAssetExchangeRepository,
    private val chatMessagesManager: ChatMessagesManager,
    private val chatRepo: IChatRepository,
    private val deviceRepo: IDeviceRepository,
    private val localRepo: ILocalRepository,
    val marketDataManager: MarketDataManager,
    private val messageRepo: IMessageRepository,
    private val messagesRetriever: GetMessages,
    val typingUsersManager: TypingUsersManager,
    private val userRepo: IUserRepository
): BaseViewModel(), MQTTRecipient, ChatMessagesRecipient, MarketDataRecipient {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatViewModel::class.java.simpleName
    }

    // MARK: - Public Constant
//    val messageMQTTClient = mqttManager.marketDataMQTTClient // previously messageMQTTClient

    // MARK: - Private Constant
    private val requestedIds = HashSet<String>()

    // MARK: - Public Variables
    var allSuggestions = MutableLiveData<List<TextSuggestion>>()
    var chat = MutableLiveData<UIState>()
    var currentUser = MutableLiveData<UserModel>()
    var displayMentions = MutableLiveData<List<UserModel>>()
    var displaySuggestions = MutableLiveData<List<TextSuggestion>>()
    var galleryImages = MutableLiveData<List<String>>()
    var isMarkRead = false
    var isTypingMessage = MutableLiveData<String>()
    var latestPrice = MutableLiveData<String>()
    var messages: LiveData<PagedList<MessageModel>> = MutableLiveData()
    var messageAction = MutableLiveData<MessageModel?>()
    var messageReplied: MessageModel? = null
    var participants = MutableLiveData<MutableList<UserModel>>()
    var selectedInstrument: AssetExchangeModel? = null

    // MARK: - Private Variable
    private var highlightedObjects = listOf<HighlightObject>()

    // MARK: - MQTTRecipient Functions
    override fun didReceiveNewMessage(
        topic: String,
        payloadDictionary: JSONMutableMap,
        payloadString: String
    ) {
        AppLog.d(TAG, "topic: $topic, payload string: $payloadString, payload map: $payloadDictionary")
        val userId = currentUser.value?.id ?: ""
//        if (topic.contains(MQTTUrls.messageRoomIsTyping(""))) {
//            val id = (payloadDictionary[UserJsonKey.id] as? String) ?: ""
//            val name = (payloadDictionary[UserJsonKey.display_name] as? String) ?: ""
//            if (id != userId && id.isNotEmpty()) typingUsersManager.addUser(id, name)
//        }
    }

    override fun didReceiveTimeout(topic: String) {
        // TODO("Handle when receive a timeout alert during subscription")
    }

    // MARK: - ChatMessagesRecipient Function
    override fun didReceiveNewMessage(message: MessageModel) {
        messages.value?.dataSource?.invalidate()
    }

    // MARK: - MarketDataRecipient Function
    override fun didReceiveNewMessage(assetExchange: AssetExchangeModel) {
        latestPrice.postValue(assetExchange.latestPriceDisplay())
    }

    // MARK: - Public Functions
    fun checkIfCurrentUserBlocked(): Pair<UserModel?, Boolean> {
        return getOtherUserInPrivateChat()?.let {
            Pair(it, currentUser.value?.blocker_ids?.contains(it.id) ?: false)
        } ?: run {
            Pair(null, false)
        }
    }

    fun cleanupChatMessagesManager() {
        chatMessagesManager.removeRecipient(this)
    }

    fun findMention(text: String) {
        if (text.isEmpty())
            displayMentions.postValue(listOf())
        else
            (participants.value as? List<UserModel>)?.let { userList ->
                text.findMentionText()?.let { mentionText ->
                    displayMentions.postValue(userList.filter {
                        it.display_name.contains(
                            mentionText.drop(1),
                            ignoreCase = true
                        )
                    })
                } ?: run {
                    displayMentions.postValue(listOf())
                }
            }
    }

    fun findDisplayName(senderId: String) =
        participants.value
            ?.firstOrNull { it.id == senderId }
            ?.display_name ?: senderId
    
    fun getAllImages(context: Activity) { // Get all images from Gallery
        galleryImages.postValue(Utils.getGalleryImages(context))
    }

    fun getBlockList() {
        ioScope.launch {
            try {
                if (userRepo.loadCurrentUserBlockingData())
                    loadCurrentUserFromRealm()
            } catch (e: Exception) {
                AppLog.d(TAG, "Get block list failed with exception $e")
            }
        }
    }

    fun getChatDetailFromRealm(roomId: String) {
        ioScope.launch {
            chat.postValue(UIState.FINISHED(chatRepo.findChat(roomId)))
        }
    }

    fun getChatDetailFromServer(roomId: String) {
        ioScope.launch {
            try {
                if (chatRepo.loadChat(roomId)) {
                    val chatModel = chatRepo.findChat(roomId)
                    if (chatModel != null) {
                        if (chatModel.category == Constants.CHAT_CATEGORY_PRIVATE) {
                            val localChat = chatRepo.findChat(roomId)
                            chatModel.avatar = AvatarModel(id = "", url = localChat?.avatar?.url.toString())
                            chatModel.name = localChat?.name ?: ""
                        }
                        chat.postValue(UIState.FINISHED(chatModel))
                    } else {
                        AppLog.d(TAG, "Error find local chat due to Realm")
                        chat.postValue(UIState.FINISHED(null))
                    }
                } else {
                    AppLog.d(TAG, "Error get chat detail due to server error")
                    chat.postValue(UIState.FINISHED(null))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Error get chat detail with exception: $e")
                chat.postValue(UIState.FINISHED(null))
            }
        }
    }

    fun getInitialMessages(roomId: String) {
        val config = PagedList.Config.Builder()
            .setPageSize(Constants.Message.CHAT_PAGE_SIZE)
            .setPrefetchDistance(Constants.Message.CHAT_PRE_DISTANCE_SIZE)
            .setInitialLoadSizeHint(Constants.Message.CHAT_PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()
        messages = LivePagedListBuilder(object: DataSource.Factory<Double, MessageModel>() {
            override fun create(): DataSource<Double, MessageModel> {
                return MessagePagingSource(ioScope, roomId, messageRepo)
            }
        }, config).setBoundaryCallback(object: PagedList.BoundaryCallback<MessageModel>() {
            private var isRequest = false

            override fun onZeroItemsLoaded() { }

            override fun onItemAtFrontLoaded(itemAtFront: MessageModel) {
                if (itemAtFront.data.type != MessageDataType.SystemRoomCreate)
                    loadMessages(itemAtFront.sent_ts, itemAtFront.id)
            }

            override fun onItemAtEndLoaded(itemAtEnd: MessageModel) { }

            private fun loadMessages(endTs: Double, id: String = "") {
                if (isRequest || (id.isNotEmpty() && !requestedIds.add(id))) return
                isRequest = true
                ioScope.launch {
                    messagesRetriever.invoke(GetMessageParam(roomId, endTs, Constants.Message.CHAT_PAGE_SIZE)) {
                        messages.value?.dataSource?.invalidate()
                        isRequest = false
                    }
                }
            }
        }).build()
    }

    fun getOtherUserInPrivateChat(): UserModel? {
        return ((chat.value as? UIState.FINISHED<*>)?.data as? ChatModel)?.let { chat ->
            if (chat.category != Constants.CHAT_CATEGORY_PRIVATE) return null
            return participants.value?.first { it.id != currentUser.value?.id }
        } ?: run {
            null
        }
    }

    fun getShowDoodleBackgroundStatus(roomId: String): Boolean {
        return localRepo.getShowDoodleBackground(roomId)
    }

    fun performHighlight(editText: EditText, context: Context) {
        if (allSuggestions.value != null && participants.value != null) {
            val data = editText.getHighlightObjects(
                listOf(
                    HighlightParameters(allSuggestions.value!!, ContextCompat.getColor(context, R.color.blue_link), ""),
                    HighlightParameters(participants.value!!, ContextCompat.getColor(context, R.color.blue_link), "@", true)
                ),
                ContextCompat.getColor(context, R.color.black),
                highlightedObjects
            )
            highlightedObjects = data.second

            // Apply the new spannableText to EditText
            editText.setText(data.first, TextView.BufferType.SPANNABLE)
            editText.setSelection(editText.length())
        } else {
            AppLog.d(TAG, "Highlight data is not available yet.")
        }
    }

    fun loadCurrentUserFromRealm() {
        currentUser.postValue(userRepo.getCurrentLocalUser())
    }

    fun loadParticipants(userIds: List<String>) {
        if (userIds.isEmpty()) return participants.postValue(mutableListOf())
        ioScope.launch {
            try {
                val success = userRepo.loadUsers(userIds = userIds, shouldFill = true)
                if (success)
                    participants.postValue(userRepo.findLocalUsers(userIds.toTypedArray())?.toMutableList() ?: mutableListOf())
                else {
                    AppLog.d(TAG, "Load participants failed due to server error")
                    participants.postValue(mutableListOf())
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Load participants failed with exception: $e")
                participants.postValue(mutableListOf())
            }
        }
    }

    fun markReadChat(chatId: String, timeStamp: Double? = null, callback: SuccessCallback) {
        val finalTs = timeStamp ?: DateTimeUtils.getCurrentTsForIos()
        if (finalTs >= ((chat.value as? UIState.FINISHED<*>)?.data as? ChatModel)?.read_message_ts ?: 0.0) {
            ioScope.launch {
                try {
                    // Do in the background, no need to notify user
                    uiScope.launch { callback.invoke(chatRepo.markReadChat(chatId, finalTs)) }
                } catch (e: Exception) {
                    AppLog.d(TAG, "Mark read chat failed with exception: $e")
                    uiScope.launch { callback.invoke(false) }
                }
            }
        }
    }

    fun sendImagesMessage(images: MutableList<String?>, roomId: String) {
        ioScope.launch {
            try {
                val data = messageRepo.uploadImages(images)
                data.forEach {
                    if (!it.second.isNullOrEmpty()) {
                        val imageData = CreateMessageDataRequest(MessageDataType.Image.name, mapOf("id" to it.first, "url" to it.second))
                        val success = messageRepo.sendMessage(
                            deviceRepo.getDeviceID(),
                            null,
                            imageData,
                            false,
                            roomId,
                            null
                        )
                        if (success)
                            messages.value?.dataSource?.invalidate()
                        else
                            AppLog.d(TAG, "Send images message failed due to server error.")
                    }
                }
                messageReplied = null
                messageAction.postValue(null) // Hide replied view
            } catch (e: Exception) {
                AppLog.d(TAG, "Send images message failed with exception: $e")
            }
        }
    }

    fun sendTextMessage(text: String, roomId: String) {
        if (text.isNotEmpty() || messageReplied != null) {
            ioScope.launch {
                try {
                    var replacedText = text
                    highlightedObjects.forEach {
                        if (it.type == UserEntity.TAG)
                            replacedText = replacedText.replaceFirst(it.value, it.id)
                    }
                    val messageData = CreateMessageDataRequest(MessageDataType.Text.name, replacedText.trim())
                    val extraData = mapOf("mentions" to highlightedObjects.map { it.id })
                    val success = messageRepo.sendMessage(
                        deviceRepo.getDeviceID(),
                        messageReplied,
                        messageData,
                        false,
                        roomId,
                        extraData
                    )
                    if (success)
                        messages.value?.dataSource?.invalidate()
                    else
                        AppLog.d(TAG, "Send text message failed due to server error.")
                    highlightedObjects = listOf()
                    messageReplied = null
                    messageAction.postValue(null) // Hide replied view
                } catch (e: Exception) {
                    AppLog.d(TAG, "Send text message failed with exception: $e")
                }
            }
        }
    }

    fun setupChatMessagesManager() {
        chatMessagesManager.addRecipient(this)
    }

    fun unblockUser(userId: String, callback: SuccessCallback) {
        currentUser.value?.let {
            ioScope.launch {
                try {
                    val success = userRepo.unblockUser(userId)
                    if (success) {
                        val newBlockeeIds = it.blockee_ids.toMutableList()
                        newBlockeeIds.remove(userId)
                        it.blockee_ids = newBlockeeIds
                        val storeSuccess = userRepo.saveUserJson(JSONObject(mapOf("id" to it.id, "blockee_ids" to newBlockeeIds)))
                        if (storeSuccess) { loadCurrentUserFromRealm() }
                        callback(storeSuccess)
                    } else {
                        callback.invoke(false)
                    }
                } catch (e: Exception) {
                    AppLog.d(TAG, "Unblock a user failed with exception: $e")
                    callback.invoke(false)
                }
            }
        }
    }
}