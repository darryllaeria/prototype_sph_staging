package com.proto.type.base.manager

import com.proto.type.base.*
import com.proto.type.base.data.model.MessageJsonKey
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class ChatMessagesManager(private val messageRepo: IMessageRepository,
                          private val userRepo: IUserRepository): MQTTRecipient {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatMessagesManager::class.java.simpleName
    }

    // MARK: - Private Variable
    private var recipients: MutableList<ChatMessagesRecipient> = mutableListOf()

    // MARK: - Private Constant
//    private val messageMQTTClient = mqttManager.marketDataMQTTClient // previously messageMQTTClient

    // MARK: - Override Functions
    override fun didReceiveNewMessage(
        topic: String,
        payloadDictionary: JSONMutableMap,
        payloadString: String
    ) {
        AppLog.d(TAG, "topic: $topic, payload string: $payloadString, payload map: $payloadDictionary")
        val success = messageRepo.saveMessageJson(JSONObject(payloadDictionary))
        if (success) {
            (payloadDictionary[MessageJsonKey.id] as? String)?.let {
                runBlocking {
                    val message = messageRepo.findLocalMessage(it)
                    if (message != null)
                        sendMessageToRecipient(message)
                    else
                        AppLog.d(TAG, "Notify recipients failed due to unable to find saved Message from Realm.")
                }
            } ?: run {
                AppLog.d(TAG, "Notify recipients failed due to no message id: $payloadString")
            }
        } else {
            AppLog.d(TAG, "Notify recipients failed due to unable to save Message Json response: $payloadString")
        }
    }

    override fun didReceiveTimeout(topic: String) {
        // TODO("Handle when receive a timeout alert during subscription")
    }

    // MARK: - Public Functions
    fun addRecipient(recipient: ChatMessagesRecipient) {
        if (!recipients.contains(recipient))
            recipients.add(recipient)
    }

    fun removeAllRecipients() {
        recipients.clear()
    }

    fun removeRecipient(recipient: ChatMessagesRecipient) {
        recipients.remove(recipient)
        AppLog.d(TAG, "Current recipient counts: ${recipients.size}")
    }

    fun subscribe(userId: String? = null, userKey: String? = null) {
        var finalUserId = ""
        var finalUserKey: String? = ""
        if (userId != null && userKey != null) {
            finalUserId = userId
            finalUserKey = userKey
        } else {
            val currentUser = userRepo.getCurrentLocalUser()
            finalUserId = currentUser.id
            finalUserKey = currentUser.encryption_key
        }
        if (finalUserId.isNotEmpty()) {
//            messageMQTTClient.unsubscribe(MQTTUrls.messageRoomIncoming(finalUserId)) {
//                messageMQTTClient.subscribe(topic = MQTTUrls.messageRoomIncoming(finalUserId), subscriber = this, encryptionKey = finalUserKey)
//            }
        } else {
            AppLog.d(TAG, "Subscribe ChatMessages MQTT failed due to no user logged in yet")
        }
    }

    fun unsubscribe(userId: String? = null, callback: EmptyCallback? = null) {
        var finalUserId = userId ?: userRepo.getCurrentLocalUser().id
        if (finalUserId.isNotEmpty()) {
//            messageMQTTClient.unsubscribe(MQTTUrls.messageRoomIncoming(finalUserId)) {
//                callback?.invoke()
//            }
        } else {
            AppLog.d(TAG, "Unsubscribe ChatMessages MQTT failed due to no user logged in yet")
        }
    }

    // MARK: - Private Function
    private fun sendMessageToRecipient(message: MessageModel) {
        recipients.forEach { it.didReceiveNewMessage(message) }
        // TODO("Handle updating user online status here when receive his message in private chat")
    }
}