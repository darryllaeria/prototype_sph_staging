package com.proto.type.base.repository.message

import com.proto.type.base.data.database.dao.MessageDao
import com.proto.type.base.data.database.dao.MessageLinkItemDao
import com.proto.type.base.data.database.dao.MessageMediaItemDao
import com.proto.type.base.data.mapper.MessageLinkItemMapper
import com.proto.type.base.data.mapper.MessageMapper
import com.proto.type.base.data.mapper.MessageMediaItemMapper
import com.proto.type.base.data.model.*
import com.proto.type.base.data.remote.MessageService
import com.proto.type.base.extension.sortDoubleValueUsing
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

class MessageRepositoryImpl(
//    private val firebaseService: FirebaseService,
    private val messageDao: MessageDao,
    private val messageLinkItemDao: MessageLinkItemDao,
    private val messageMediaItemDao: MessageMediaItemDao,
    private val messageService: MessageService
): IMessageRepository {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = MessageRepositoryImpl::class.java.simpleName
    }

    // MARK: - Database Functions
    /**
     * @Detail Delete message link (inside chat) data from Realm
     */
    override suspend fun deleteLocalMessageLinkItems(roomId: String) {
        messageMediaItemDao.deleteMessageMediaItems(roomId)
    }

    /**
     * @Detail Delete message media (inside chat) data from Realm
     */
    override suspend fun deleteLocalMessageMediaItems(roomId: String) {
        messageMediaItemDao.deleteMessageMediaItems(roomId)
    }

    /**
     * @Detail Delete message (inside chat) data from Realm
     */
    override suspend fun deleteLocalMessages(roomId: String) {
        messageDao.deleteMessages(roomId)
    }

    /**
     * @Detail Get message data from Realm with provided messageId
     */
    override suspend fun findLocalMessage(messageId: String): MessageModel? {
        return runBlocking {
            messageDao.findMessage(messageId)
        }
    }

    /**
     * @Detail Get message link (chat settings) data from Realm
     */
    override suspend fun findLocalMessageLinkItems(roomId: String): List<MessageLinkItemModel> {
        return runBlocking {
            messageLinkItemDao.getMessageLinkItems(roomId)
        }
    }

    /**
     * @Detail Get message media (chat settings) data from Realm
     */
    override suspend fun findLocalMessageMediaItems(roomId: String): List<MessageMediaItemModel> {
        return runBlocking {
            messageMediaItemDao.getMessageMediaItems(roomId)
        }
    }

    /**
     * @Detail Get message (inside chat) data from Realm
     */
    override suspend fun findLocalMessages(roomId: String): List<MessageModel> {
        return runBlocking {
            messageDao.findMessages(roomId)
        }
    }

    /**
     * @Detail Get message (inside chat) data by field name from Realm
     */
    override suspend fun findLocalMessagesByFieldName(fieldName: String, keyword: String): List<MessageModel> {
        return runBlocking {
            messageDao.findMessagesByFieldName(fieldName, keyword)
        }
    }

    override suspend fun findLocalMessagesGreatThan(roomId: String, startTs: Double, pageSize: Int): List<MessageModel> {
        return runBlocking {
            messageDao.findNewMessages(roomId, startTs, pageSize)
        }
    }

    override suspend fun findLocalMessagesOlderThan(roomId: String, endTs: Double, pageSize: Int): List<MessageModel> {
        return runBlocking {
            messageDao.findOlderMessages(roomId, endTs, pageSize)
        }
    }

    override fun saveMessageJson(jsonObject: JSONObject): Boolean {
        return try {
            runBlocking {
                messageDao.saveJSONData(jsonObject) != null
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    override fun saveMessageJsons(jsonArray: JSONArray): Boolean {
        return try {
            runBlocking {
                val ids = messageDao.saveJSONDatas(jsonArray)
                val dataSize = jsonArray.length()
                dataSize != 0 && ids.size == dataSize
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    /**
     * @Detail Store message link list from API to Realm
     */
    override fun storeMessageLinkItems(messages: List<MessageLinkItemModel>) {
        messageDao.insertOrUpdateEntities(MessageLinkItemMapper.toEntities(messages))
    }

    /**
     * @Detail Store message media list from API to Realm
     */
    override fun storeMessageMediaItems(messages: List<MessageMediaItemModel>) {
        messageDao.insertOrUpdateEntities(MessageMediaItemMapper.toEntities(messages))
    }

    /**
     * @Detail Store messages list from API to Realm
     */
    override fun storeMessages(messages: List<MessageModel>) {
        messageDao.insertOrUpdateEntities(MessageMapper.toEntities(messages))
    }

    // MARK: - Service Functions
    /**
     * @Detail Get message link items (inside chat) data from API
     */
    override suspend fun loadMessageLinkItems(roomId: String, pageSize: Int, endTimestamp: String?): Response<MessageLinkResponse> {
        return messageService.getLinkItems(
            roomId,
            pageSize,
            endTime = endTimestamp
        )
    }

    /**
     * @Detail Get message media items (inside chat) data from API
     */
    override suspend fun loadMessageMediaItems(roomId: String, pageSize: Int, endTimestamp: String?): Response<MessageMediaResponse> {
        return messageService.getMediaItems(
            roomId,
            pageSize,
            endTime = endTimestamp
        )
    }

    /**
     * @Detail Get message (inside chat) data from API
     */
    override suspend fun loadMessages(roomId: String, pageSize: Int?, endTs: Double?): Pair<Boolean, Double> {
        val jsonString = messageService.getMessages(roomId, pageSize, endTs).string().trim()
        val jsonData = JSONObject(jsonString)
        val jsonMessages = jsonData.optJSONArray(MessageJsonKey.items)
        var sortedMessages = jsonMessages.sortDoubleValueUsing(MessageJsonKey.sent_ts)
        val oldestMessageTs = (sortedMessages.firstOrNull()?.optDouble(MessageJsonKey.sent_ts) ?: 0.0)
        return Pair(saveMessageJsons(jsonMessages), oldestMessageTs)  // Save messages info to Realm
    }

    /**
     * @Detail Send text message to backend
     */
    override suspend fun sendMessage(id: String?, quotedMessage: MessageModel?, data: CreateMessageDataRequest, isEdited: Boolean, roomId: String?, extraData: Any?): Boolean {
        val request = CreateMessageRequest(
            id = id,
            quotedMessageId = quotedMessage?.id,
            quotedMessage = quotedMessage,
            data = data,
            isEdited = isEdited,
            roomId = roomId,
            extraData = extraData
        )
        val jsonString = messageService.sendMessage(request).string().trim()
        return saveMessageJson(JSONObject(jsonString))
    }

    override suspend fun uploadImages(images: MutableList<String?>): List<Pair<String?, String?>> {
//        return images.map { Uri.fromFile(File(it)) }.map { firebaseService.uploadFile(it, it.lastPathSegment, FileDataType.JPG) }
        return emptyList()
    }
}