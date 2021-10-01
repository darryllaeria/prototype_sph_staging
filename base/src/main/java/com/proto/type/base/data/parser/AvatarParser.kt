package com.proto.type.base.data.parser

import com.proto.type.base.data.model.AvatarJsonKey
import com.proto.type.base.data.model.AvatarModel
import com.proto.type.base.utils.AppLog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class AvatarParser: JsonAdapter<AvatarModel?>(){

    // MARK: - Companion Object
    companion object {
        private val TAG: String = AvatarParser::class.java.simpleName
    }

    // MARK: - Override Function
    override fun fromJson(reader: JsonReader): AvatarModel? {
        try {
            if (reader.peek() == JsonReader.Token.NULL || reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
                if (reader.peek() == JsonReader.Token.STRING) {
                    reader.nextString()
                }
                return AvatarModel()
            }
            reader.beginObject()
            val avatar = AvatarModel()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    AvatarJsonKey.id -> avatar.id = reader.nextString()
                    AvatarJsonKey.filename -> avatar.file_name = reader.nextString()
                    AvatarJsonKey.type -> avatar.type = reader.nextString()
                    AvatarJsonKey.url, AvatarJsonKey.signed_url -> avatar.url = reader.nextString()
                }
            }
            reader.endObject()
            return avatar
        } catch (e: Exception) {
            AppLog.d(TAG, "Json format is invalid with exception: $e")
            return AvatarModel()
        }
    }

    override fun toJson(writer: JsonWriter, value: AvatarModel?) {
        writer.beginObject()
        writer.name(AvatarJsonKey.id).value(value?.id)
        writer.name(AvatarJsonKey.filename).value(value?.file_name)
        writer.name(AvatarJsonKey.type).value(value?.type)
        writer.name(AvatarJsonKey.url).value(value?.url)
        writer.endObject()
    }
}