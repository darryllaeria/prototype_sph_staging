package com.proto.type.base.data.database.entity

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object ChatEntityKey {
    const val admin_ids = "admin_ids"
    const val avatar = "avatar"
    const val category = "category"
    const val creator_id = "creator_id"
    const val id = "id"
    const val is_pinned = "is_pinned"
    const val is_removed = "is_removed"
    const val name = "name"
    const val participant_ids = "participant_ids"
    const val read_message_ts = "read_message_ts"
    const val unread_bot_count = "unread_bot_count"
    const val unread_count = "unread_count"
}

// MARK: - Open Class
open class ChatEntity(
    var admin_ids: RealmList<String> = RealmList(),
    var avatar: AvatarEntity? = null,
    var category: String = "",
    var creator_id: String = "",
    @PrimaryKey var id: String = "",
    var is_pinned: Boolean = false,
    var is_removed: Boolean = false,
    var mute_notification: Boolean = false,
    var name: String = "",
    var participant_ids: RealmList<String> = RealmList(),
    var read_message_ts: Double = 0.0,
    var unread_bot_count: Int = 0,
    var unread_count: Int = 0
): RealmObject() {
    constructor() : this(id = "")
}