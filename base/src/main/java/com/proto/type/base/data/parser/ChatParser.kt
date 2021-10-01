package com.proto.type.base.data.parser

import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.ChatJsonKey
import com.proto.type.base.utils.AppLog
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class ChatParser: JsonAdapter<ChatModel?>() {

    companion object {
        private val TAG: String = ChatParser::class.java.simpleName
        private val ROOM_AVATAR_KEYS = JsonReader.Options.of(ChatJsonKey.id, ChatJsonKey.url)
    }

    override fun fromJson(reader: JsonReader): ChatModel {
        reader.isLenient = true

        try {
            if (reader.peek() == JsonReader.Token.NULL || reader.peek() != JsonReader.Token.BEGIN_OBJECT) {
                if (reader.peek() == JsonReader.Token.STRING) {
                    reader.nextString()
                }
                throw UnsupportedClassVersionError()
            }
            reader.beginObject()
            val chatModel = ChatModel()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    ChatJsonKey.id -> chatModel.id = reader.nextString()
                    ChatJsonKey.mute_notification -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.mute_notification = reader.nextBoolean()
                        } else {
                            chatModel.mute_notification = false
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.is_pinned -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.is_pinned = reader.nextBoolean()
                        } else {
                            chatModel.is_pinned = false
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.category -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.category = reader.nextString()
                        } else {
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.name -> {
                        if (reader.peek() == JsonReader.Token.NULL) {
                            reader.skipValue()
                        } else {
                            chatModel.name = reader.nextString()
                        }
                    }
                    ChatJsonKey.participants -> {
                        if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
                            val participants = ArrayList<String>()

                            reader.beginArray()
                            while (reader.hasNext()) {
                                participants.add(reader.nextString())
                            }
                            chatModel.participant_ids = participants
                            reader.endArray()
                        } else {
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.admins -> {
                        if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
                            val admins = ArrayList<String>()

                            reader.beginArray()
                            while (reader.hasNext()) {
                                admins.add(reader.nextString())
                            }
                            chatModel.admin_ids = admins
                            reader.endArray()
                        } else {
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.avatar -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject()
                                while (reader.hasNext()) {
                                    when (reader.selectName(ROOM_AVATAR_KEYS)) {
                                        0 -> {
                                            if (reader.peek() != JsonReader.Token.NULL) {
                                                chatModel.avatar?.id = reader.nextString()
                                            } else {
                                                reader.skipValue()
                                            }
                                        }
                                        1 -> {
                                            if (reader.peek() != JsonReader.Token.NULL) {
                                                chatModel.avatar?.url = reader.nextString()
                                            } else {
                                                reader.skipValue()
                                            }
                                        }
                                    }
                                }
                                reader.endObject()
                            } else if (reader.peek() == JsonReader.Token.STRING) {
                                chatModel.avatar?.url = reader.nextString()
                            }
                        } else {
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.read_message_ts -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.read_message_ts = reader.nextDouble()
                        } else {
                            chatModel.read_message_ts = 0.0
                            reader.skipValue()
                        }
                    }
                    ChatJsonKey.unread_count -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.unread_count = reader.nextInt()
                        }
                    }
                    ChatJsonKey.unread_bot_count -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            chatModel.unread_bot_count = reader.nextInt()
                        }
                    }
                    ChatJsonKey.last_message -> {
                        if (reader.peek() != JsonReader.Token.NULL) {
                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                reader.beginObject()
                                while (reader.hasNext()) {
                                    when (reader.nextName()) {
                                        ChatJsonKey.extra_data -> { reader.skipValue() }
                                        ChatJsonKey.data -> {
                                            reader.beginObject()
                                            while (reader.hasNext()) {
                                                when (reader.nextName()) {
                                                    ChatJsonKey.type -> {
                                                        chatModel.last_message?.data?.type = MessageDataType.valueOf(reader.nextString())
                                                    }
                                                    ChatJsonKey.value -> {
                                                        if (reader.peek() != JsonReader.Token.NULL) {
                                                            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                                                reader.beginObject()
                                                                while (reader.hasNext()) {
                                                                    when (reader.nextName()) {
                                                                        ChatJsonKey.id -> {
                                                                            chatModel.last_message?.data?.value = reader.nextString()
                                                                        }
                                                                        ChatJsonKey.url -> {
                                                                            chatModel.last_message?.data?.value_url = reader.nextString()
                                                                        }
                                                                    }
                                                                }
                                                                reader.endObject()
                                                            } else {
                                                                chatModel.last_message?.data?.value = reader.nextString()
                                                            }
                                                        } else {
                                                            reader.skipValue()
                                                        }
                                                    }
                                                }
                                            }
                                            reader.endObject()
                                        }
                                        ChatJsonKey.id -> { reader.skipValue() }
                                        ChatJsonKey.sender -> {
                                            if (reader.peek() != JsonReader.Token.NULL) {
                                                if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                                                    reader.beginObject()
                                                    while (reader.hasNext()) {
                                                        when (reader.nextName()) {
                                                            ChatJsonKey.id -> {
                                                                chatModel.last_message?.sender?.id = reader.nextString()
                                                            }
                                                            ChatJsonKey.type -> {
                                                                chatModel.last_message?.sender?.type = reader.nextString()
                                                            }
                                                        }
                                                    }
                                                    reader.endObject()
                                                }
                                            } else {
                                                reader.skipValue()
                                            }
                                        }
                                        ChatJsonKey.sent_ts -> {
                                            if (reader.peek() != JsonReader.Token.NULL) {
                                                chatModel.last_message?.sent_ts = reader.nextDouble()
                                            } else {
                                                reader.skipValue()
                                            }
                                        }
                                    }
                                }
                                reader.endObject()
                            } else {
                                reader.skipValue()
                            }
                        } else {
                            reader.skipValue()
                        }
                    }
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            return chatModel
        } catch (e: Exception) {
            AppLog.d(TAG, "Json format is invalid with exception: $e")
            reader.skipName()
            return ChatModel()
        }
    }

    override fun toJson(writer: JsonWriter, value: ChatModel?) {}
}