package com.proto.type.base.data.model

import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.database.dao.SendStatus
import com.squareup.moshi.Json

// MARK: - Object
object MessageJsonKey {
    const val data = "data"
    const val extra_data = "extra_data"
    const val id = "id"
    const val is_edited = "is_edited"
    const val is_forwarded = "is_forwarded"
    const val items = "items"
    const val link = "link"
    const val mentions = "mentions"
    const val modified_ts = "modified_ts"
    const val next_index = "next_index"
    const val num_record = "num_record"
    const val page_size = "page_size"
    const val quoted_message = "quoted_message"
    const val quoted_message_id = "quoted_message_id"
    const val room_id = "room_id"
    const val sender = "sender"
    const val sent_ts = "sent_ts"
    const val type = "type"
    const val url = "url"
    const val value = "value"
}

// MARK: - Model Data Classes
data class MessageData(
    @Json(name = MessageJsonKey.type) var type: MessageDataType? = MessageDataType.Text,
    @Json(name = MessageJsonKey.value) var value_any: Any? = null,
    @Transient var value_url: String = "",
    @Transient var value: String = ""
)

data class MessageModel(
    @Json(name = MessageJsonKey.data) var data: MessageData = MessageData(),
    @Json(name = MessageJsonKey.id) val id: String = "",
    @Json(name = MessageJsonKey.is_edited) var is_edited: Boolean = false,
    @Json(name = MessageJsonKey.is_forwarded) var is_forwarded: Boolean = false,
    @Json(name = MessageJsonKey.modified_ts) var modified_ts: Double = 0.0,
    @Json(name = MessageJsonKey.quoted_message) var quoted_message: MessageModel? = null,
    @Json(name = MessageJsonKey.room_id) var room_id: String = "",
    @Json(name = MessageJsonKey.sender) var sender: MessageSender = MessageSender(),
    @Json(name = MessageJsonKey.sent_ts) var sent_ts: Double = 0.0,
    @Transient var mention_ids: List<String> = listOf(),
    @Transient var quoted_message_id: String = "",
    @Transient var send_status: SendStatus = SendStatus.Success
)

data class MessagePagination(
    @Json(name = MessageJsonKey.next_index) var next_index: Int? = 0,
    @Json(name = MessageJsonKey.num_record) var num_record: Int? = 0,
    @Json(name = MessageJsonKey.page_size) var page_size: Int? = 0
)

data class MessageSender(
    @Json(name = MessageJsonKey.id) var id: String? = "",
    @Json(name = MessageJsonKey.type) var type: String? = ""
)

// MARK: - Request Data Classes
data class CreateMessageDataRequest(
    @Json(name = MessageJsonKey.type) val type: String? = "",
    @Json(name = MessageJsonKey.value) val valueAny: Any? = null
)

data class CreateMessageRequest(
    @Json(name = MessageJsonKey.id) val id: String? = null,
    @Json(name = MessageJsonKey.quoted_message_id) val quotedMessageId: String? = null,
    @Json(name = MessageJsonKey.quoted_message) val quotedMessage: MessageModel? = null,
    @Json(name = MessageJsonKey.data) val data: CreateMessageDataRequest? = null,
    @Json(name = MessageJsonKey.is_edited) val isEdited: Boolean = false,
    @Json(name = MessageJsonKey.room_id) val roomId: String? = "",
    @Json(name = MessageJsonKey.extra_data) val extraData: Any?
)

// MARK: - Response Data Class
data class MessageResponse(
    @Json(name = MessageJsonKey.items) var items: List<MessageModel>? = mutableListOf(),
    @Transient var pagination: MessagePagination = MessagePagination()
)