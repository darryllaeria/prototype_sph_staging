package com.proto.type.base.data.database.entity

import com.proto.type.base.data.database.dao.SendStatus
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object MessageEntityKey {
    const val data_type = "data_type"
    const val data_url = "data_url"
    const val data_value = "data_value"
    const val id = "id"
    const val is_edited = "is_edited"
    const val is_forwarded = "is_forwarded"
    const val mention_ids = "mention_ids"
    const val modified_ts = "modified_ts"
    const val quoted_message_id = "quoted_message_id"
    const val room_id = "room_id"
    const val sender_id = "sender_id"
    const val sender_type = "sender_type"
    const val sent_ts = "sent_ts"
}

// MARK: - Open Class
open class MessageEntity(
    var data_type: String = "",
    var data_url: String = "",
    var data_value: String = "",
    @PrimaryKey var id: String = "",
    var is_edited: Boolean = false,
    var is_forwarded: Boolean = false,
    var mention_ids: RealmList<String> = RealmList(),
    var modified_ts: Double = 0.0,
    var quoted_message_id: String = "",
    var room_id: String = "",
    var send_status: String = SendStatus.Success.name,
    var sender_id: String = "",
    var sender_type: String = "",
    var sent_ts: Double = 0.0
): RealmObject() {
    constructor() : this(id = "")
}