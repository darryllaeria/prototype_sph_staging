package com.proto.type.base.data.parser

import com.proto.type.base.Constants
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.model.MessageData
import com.proto.type.base.data.model.MessageJsonKey
import com.proto.type.base.utils.AppLog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class MessageDataParser: JsonAdapter<MessageData?>() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = MessageDataParser::class.java.simpleName
        private val TOP_KEYS = JsonReader.Options.of(MessageJsonKey.type, MessageJsonKey.value)
        private val VALUE_KEYS = JsonReader.Options.of(MessageJsonKey.id, MessageJsonKey.url)
    }

    override fun fromJson(reader: JsonReader): MessageData? {
        try {
            if (reader.peek() == JsonReader.Token.NULL || reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
                if (reader.peek() == JsonReader.Token.STRING) {
                    reader.nextString()
                }
                return MessageData()
            }
            reader.beginObject()
            var optionType: String? = ""
            val messageData = MessageData()
            while (reader.hasNext()) {
                when (reader.selectName(TOP_KEYS)) {
                    0 -> {
                        optionType = reader.nextString()
                        messageData.type = MessageDataType.valueOf(optionType)
                    }
                    1 -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            when (optionType) {
                                MessageDataType.Text.name,
                                MessageDataType.SystemUserAddBot.name,
                                MessageDataType.SystemRoomCreate.name -> {
                                    messageData.value = reader.nextString()
                                }
                                MessageDataType.Image.name -> {
                                    when {
                                        reader.peek() == JsonReader.Token.BEGIN_OBJECT -> {
                                            reader.beginObject()
                                            while (reader.hasNext()) {
                                                when (reader.selectName(VALUE_KEYS)) {
                                                    0 -> messageData.value = reader.nextString()
                                                    1 -> messageData.value_url = reader.nextString()
                                                }
                                            }
                                            reader.endObject()
                                        }
                                        reader.peek() == JsonReader.Token.STRING -> {
                                            messageData.value_url = reader.nextString()
                                        }
                                        else -> {
                                            reader.skipValue()
                                        }
                                    }
                                }
                                MessageDataType.Chart.name -> {
                                    reader.beginObject()
                                    while (reader.hasNext()) {
                                        when (reader.selectName(VALUE_KEYS)) {
                                            0 -> messageData.value = reader.nextString()
                                            1 -> messageData.value_url = reader.nextString()
                                        }
                                    }
                                    reader.endObject()
                                }
                            }
                        } else {
                            messageData.value = ""
                            reader.skipValue()
                        }
                    }
                }
            }
            reader.endObject()
            return messageData
        } catch (e: Exception) {
            AppLog.d(TAG, "Json format is invalid with exception: $e")
            return MessageData()
        }
    }

    override fun toJson(writer: JsonWriter, value: MessageData?) {
        writer.beginObject()
        val messageType = value?.type ?: MessageDataType.Text
        writer.name(MessageJsonKey.type).value(messageType.name)
        writer.name(if (messageType.isMedia) MessageJsonKey.id else MessageJsonKey.value).value(value?.value)
        writer.name(MessageJsonKey.url).value(value?.value_url)
        writer.endObject()
    }
}