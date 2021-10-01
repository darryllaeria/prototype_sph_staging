package com.proto.type.base.data.model

import com.squareup.moshi.Json

// MARK: - Object
object AvatarJsonKey {
    const val filename = "filename"
    const val id = "id"
    const val signed_url = "signed_url"
    const val type = "type"
    const val url = "url"
}

// MARK: - Model Data Class
data class AvatarModel(
    @Json(name = AvatarJsonKey.id) var id: String? = "",
    @Json(name = AvatarJsonKey.url) var url: String = "",
    @Transient var file_name: String = "",
    @Transient var type: String = ""
)
