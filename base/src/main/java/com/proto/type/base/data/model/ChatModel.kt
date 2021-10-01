package com.proto.type.base.data.model

import com.squareup.moshi.Json

// MARK: - Object
object ChatJsonKey {
    const val admins = "admins"
    const val avatar = "avatar"
    const val category = "category"
    const val clear_ts = "clear_ts"
    const val created_by = "created_by"
    const val data = "data"
    const val extra_data = "extra_data"
    const val id = "id"
    const val is_pinned = "is_pinned"
    const val last_message = "last_message"
    const val mute_notification = "mute_notification"
    const val name = "name"
    const val participants = "participants"
    const val read_message_ts = "read_message_ts"
    const val room_name = "room_name"
    const val sent_ts = "sent_ts"
    const val sender = "sender"
    const val type = "type"
    const val unread_bot_count = "unread_bot_count"
    const val unread_count = "unread_count"
    const val url = "url"
    const val user_id = "user_id"
    const val user_ids = "user_ids"
    const val value = "value"
}

// MARK: - Model Data Classes
data class ChatModel(
    @Json(name = ChatJsonKey.admins) var admin_ids: List<String> = listOf(),
    @Json(name = UserJsonKey.avatar) var avatar: AvatarModel? = null,
    @Json(name = ChatJsonKey.category) var category: String? = "",
    @Json(name = ChatJsonKey.created_by) var creator_id: String? = "",
    @Json(name = ChatJsonKey.id) var id: String = "",
    @Json(name = ChatJsonKey.is_pinned) var is_pinned: Boolean = false,
    @Json(name = ChatJsonKey.last_message) var last_message: MessageModel? = MessageModel(id = ""),
    @Json(name = ChatJsonKey.mute_notification) var mute_notification: Boolean = false,
    @Json(name = ChatJsonKey.name) var name: String = "",
    @Json(name = ChatJsonKey.participants) var participant_ids: List<String>? = listOf(),
    @Json(name = ChatJsonKey.read_message_ts) var read_message_ts: Double? = 0.0,
    @Json(name = ChatJsonKey.unread_bot_count) var unread_bot_count: Int = 0,
    @Json(name = ChatJsonKey.unread_count) var unread_count: Int = 0,
    @Transient var is_removed: Boolean = false,
    @Transient var private_participant_id: String = ""
)

// MARK: - Request Data Classes
data class ClearChatHistoryRequest(@Json(name = ChatJsonKey.clear_ts) val clearTimeStamp: Double = 0.0)

data class CreateChatRequest(@Json(name = ChatJsonKey.admins) val admins: List<String>,
                             @Json(name = ChatJsonKey.category) val category: String,
                             @Json(name = ChatJsonKey.id) val roomId: String,
                             @Json(name = ChatJsonKey.room_name) val chatName: String,
                             @Json(name =ChatJsonKey.user_ids) val participantsId: List<String>,
                             @Json(name = ChatJsonKey.avatar) val avatar: Map<String, String>?)

data class ChatLeaveRequest(@Json(name = ChatJsonKey.user_ids) val userIds: List<String>)

data class ChatManageUsersRequest(@Json(name = ChatJsonKey.user_ids) val userIds: List<String>)

data class ChatMarkReadRequest(@Json(name = ChatJsonKey.read_message_ts) val markReadTimeStamp: Double = 0.0)

data class ChatPromoteDemoteUserRequest(@Json(name = ChatJsonKey.user_id) val userId: String)

data class ChatUpdateAvatarRequest(@Json(name = ChatJsonKey.avatar) val avatar: Map<String, String>)

data class ChatUpdateIsPinnedRequest(@Json(name = ChatJsonKey.is_pinned) val isPinned: Boolean? = false)

data class ChatUpdateMuteRequest(@Json(name = ChatJsonKey.mute_notification) val muteNotification: Boolean? = false)

data class ChatUpdateNameRequest(@Json(name = ChatJsonKey.name) val roomName: String)