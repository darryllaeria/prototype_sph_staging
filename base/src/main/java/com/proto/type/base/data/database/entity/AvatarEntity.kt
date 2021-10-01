package com.proto.type.base.data.database.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object AvatarEntityKey {
    const val id = "id"
    const val url = "url"
}

// MARK: - Open Class
open class AvatarEntity(
    var file_name: String = "",
    var id: String = "",
    var type: String = "",
    @PrimaryKey var url: String = "" // Need to use url as the primary key because for some old data there is no id but the url is still available. This is a data faulty but we still need to display that url regardless of the availability of the id.

): RealmObject() {
    constructor() : this(url = "")
}