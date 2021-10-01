package com.proto.type.base.repository.message

import com.proto.type.base.Constants
import com.proto.type.base.data.model.CreateMessageDataRequest
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.data.model.MessageMediaResponse
import com.proto.type.base.data.model.MessageResponse
import com.proto.type.base.data.model.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

interface IMessageRepository {

    // MARK: - Database Functions
    suspend fun deleteLocalMessageLinkItems(roomId: String)

    suspend fun deleteLocalMessageMediaItems(roomId: String)

    suspend fun deleteLocalMessages(roomId: String)

    suspend fun findLocalMessage(messageId: String): MessageModel?

    suspend fun findLocalMessageLinkItems(roomId: String): List<MessageLinkItemModel>

    suspend fun findLocalMessageMediaItems(roomId: String): List<MessageMediaItemModel>

    suspend fun findLocalMessages(roomId: String): List<MessageModel>

    suspend fun findLocalMessagesByFieldName(fieldName: String, keyword: String): List<MessageModel>

    suspend fun findLocalMessagesGreatThan(roomId: String, startTs: Double, pageSize: Int = Constants.Message.CHAT_PAGE_SIZE): List<MessageModel>

    suspend fun findLocalMessagesOlderThan(roomId: String, endTs: Double, pageSize: Int = Constants.Message.CHAT_PAGE_SIZE): List<MessageModel>

    fun saveMessageJson(jsonObject: JSONObject): Boolean

    fun saveMessageJsons(jsonArray: JSONArray): Boolean

    fun storeMessageLinkItems(messages: List<MessageLinkItemModel>)

    fun storeMessageMediaItems(messages: List<MessageMediaItemModel>)

    fun storeMessages(messages: List<MessageModel>)

    // MARK: - Service Functions
    suspend fun loadMessageLinkItems(roomId: String, pageSize: Int = Constants.Message.ITEM_PAGE_SIZE, endTimestamp: String? = null): Response<MessageLinkResponse>

    suspend fun loadMessageMediaItems(roomId: String, pageSize: Int = Constants.Message.ITEM_PAGE_SIZE, endTimestamp: String? = null): Response<MessageMediaResponse>

    suspend fun loadMessages(roomId: String, pageSize: Int? = 0, endTs: Double? = null): Pair<Boolean, Double>

    suspend fun sendMessage(id: String?, quotedMessage: MessageModel?, data: CreateMessageDataRequest, isEdited: Boolean, roomId: String?, extraData: Any?): Boolean

    suspend fun uploadImages(images: MutableList<String?>): List<Pair<String?, String?>>
}