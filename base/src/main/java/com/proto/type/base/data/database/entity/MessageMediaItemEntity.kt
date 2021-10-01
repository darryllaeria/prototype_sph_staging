package com.proto.type.base.data.database.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object MessageMediaItemEntityKey {
    const val id = "id"
    const val media_id = "media_id"
    const val message_id = "message_id"
    const val room_id = "room_id"
    const val sent_ts = "sent_ts"
    const val url = "url"
}

// MARK: - Open Class
open class MessageMediaItemEntity(
    @PrimaryKey var id: String = "",
    var media_id: String = "",
    var message_id: String = "",
    var room_id: String = "",
    var sent_ts: Double = 0.0,
    var url: String = ""
): RealmObject() {
    constructor() : this(id = "")
}