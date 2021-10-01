package com.proto.type.base.data.database.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object MessageLinkItemEntityKey {
    const val id = "id"
    const val link = "link"
    const val main_image_url = "main_image_url"
    const val message_id = "message_id"
    const val room_id = "room_id"
    const val sent_ts = "sent_ts"
    const val title = "title"
}

// MARK: - Open Class
open class MessageLinkItemEntity(
    @PrimaryKey var id: String = "",
    var link: String = "",
    var main_image_url: String = "",
    var message_id: String = "",
    var room_id: String = "",
    var sent_ts: Double = 0.0,
    var title: String = ""
): RealmObject() {
    constructor() : this(id = "")
}