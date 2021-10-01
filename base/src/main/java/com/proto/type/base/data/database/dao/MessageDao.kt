package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.R
import com.proto.type.base.data.database.entity.*
import com.proto.type.base.data.mapper.MessageMapper
import com.proto.type.base.data.model.MessageJsonKey
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.utils.AppLog
import io.realm.Realm
import io.realm.Sort
import org.json.JSONArray
import org.json.JSONObject

enum class MessageDataType(val isMedia: Boolean = false, val isSystem: Boolean = false, val iconResId: Int? = null) {
    Chart(isMedia = true, iconResId = R.drawable.ic_chart),
    Image(isMedia = true, iconResId = R.drawable.ic_image),
    SystemRoomCreate(isSystem = true),
    SystemRoomAvatarUpdate(isSystem = true),
    SystemRoomNameUpdate(isSystem = true),
    SystemUserAddBot(isSystem = true),
    SystemUserDemote(isSystem = true),
    SystemUserLeave(isSystem = true),
    SystemUserJoin(isSystem = true),
    SystemUserRemove(isSystem = true),
    SystemUserRemoveBot(isSystem = true),
    SystemUserPromote(isSystem = true),
    Text,
    Video(isMedia = true, iconResId = R.drawable.ic_video);

    // MARK: - Companion Object
    companion object {
        fun contain(type: String): Boolean = values().map { it.name }.contains(type)
    }
}

enum class SendStatus {
    Fail,
    FirstSend,
    Sending,
    Success
}

class MessageDao: BaseDao() {

    // MARK: - Public Functions
    fun deleteMessages(roomId: String) {
        Realm.getDefaultInstance().use {
            it.where(MessageEntity::class.java)
                .equalTo(MessageEntityKey.room_id, roomId)
                .findAll()
                .deleteAllFromRealm()
        }
    }

    fun findLatestMessage(roomId: String): MessageModel? {
        return Realm.getDefaultInstance().use { realm ->
                realm.where(MessageEntity::class.java)
                    .equalTo(MessageEntityKey.room_id, roomId)
                    .sort(MessageEntityKey.sent_ts, Sort.DESCENDING)
                    .findFirst()?.let { MessageMapper.toModel(it) }
        }
    }

    fun findMessage(messageId: String): MessageModel? {
        return Realm.getDefaultInstance().use { realm ->
            realm.where(MessageEntity::class.java)
                .equalTo(MessageEntityKey.id, messageId)
                .sort(MessageEntityKey.sent_ts, Sort.DESCENDING)
                .findFirst()?.let { MessageMapper.toModel(it) }
        }
    }

    fun findMessages(roomId: String): List<MessageModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageMapper.toModels(
                realm.where(MessageEntity::class.java)
                    .equalTo(MessageEntityKey.room_id, roomId)
                    .sort(MessageEntityKey.sent_ts, Sort.ASCENDING)
                    .findAll()
            )
        }
    }

    fun findMessagesByFieldName(fieldName: String, keyword: String): List<MessageModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageMapper.toModels(
                realm.where(MessageEntity::class.java)
                    .contains(fieldName, keyword)
                    .sort(MessageEntityKey.sent_ts, Sort.ASCENDING)
                    .findAll()
            )
        }
    }

    fun findNewMessages(roomId: String, fromTime: Double, pageSize: Int): List<MessageModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageMapper.toModels(
                realm.where(MessageEntity::class.java)
                    .equalTo(MessageEntityKey.room_id, roomId)
                    .greaterThan(MessageEntityKey.sent_ts, fromTime)
                    .sort(MessageEntityKey.sent_ts, Sort.ASCENDING)
                    .limit(pageSize.toLong())
                    .findAll()
            )
        }
    }

    fun findOlderMessages(roomId: String, endTime: Double, pageSize: Int): List<MessageModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageMapper.toModels(
                realm.where(MessageEntity::class.java)
                    .equalTo(MessageEntityKey.room_id, roomId)
                    .lessThan(MessageEntityKey.sent_ts, endTime)
                    .sort(MessageEntityKey.sent_ts, Sort.ASCENDING)
                    .limit(pageSize.toLong())
                    .findAll()
            )
        }
    }

    fun findQuoteMessage(id: String): MessageModel? {
        return Realm.getDefaultInstance().use { realm ->
            realm.where(MessageEntity::class.java)
                .equalTo(MessageEntityKey.id, id)
                .sort(MessageEntityKey.sent_ts, Sort.DESCENDING)
                .findFirst()?.let { MessageMapper.toModel(it) }
        }
    }
    
    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(MessageJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(MessageEntityKey.id to id)
        (rawJSONObject.opt(MessageJsonKey.data) as? JSONObject)?.let {
            val dataTypeString = it.opt(MessageJsonKey.type) as? String ?: ""
            if (dataTypeString.isNotEmpty()) jsonMap[MessageEntityKey.data_type] = dataTypeString
            val dataType = if (MessageDataType.contain(dataTypeString)) MessageDataType.valueOf(dataTypeString) else MessageDataType.Text
            if (dataType.isMedia) {
                ((it.opt(MessageJsonKey.value) as? JSONObject)?.opt(MessageJsonKey.url) as? String)?.let { url ->
                    jsonMap[MessageEntityKey.data_url] = url
                }
                ((it.opt(MessageJsonKey.value) as? JSONObject)?.opt(MessageJsonKey.id) as? String)?.let { id ->
                    jsonMap[MessageEntityKey.data_value] = id
                }
            } else {
                (it.opt(MessageJsonKey.value) as? String)?.let { value -> jsonMap[MessageEntityKey.data_value] = value }
            }
        }
        (rawJSONObject.opt(MessageJsonKey.extra_data) as? JSONObject)?.let {
            (it.opt(MessageJsonKey.is_forwarded) as? Boolean)?.let { isForwarded -> jsonMap[MessageEntityKey.is_forwarded] = isForwarded }
            (rawJSONObject.opt(MessageJsonKey.mentions) as? JSONArray)?.let { mentions -> jsonMap[MessageEntityKey.mention_ids] = mentions }
        }
        (rawJSONObject.opt(MessageJsonKey.is_edited) as? Boolean)?.let { jsonMap[MessageEntityKey.is_edited] = it }
        (rawJSONObject.opt(MessageJsonKey.is_forwarded) as? Boolean)?.let { isForwarded -> jsonMap[MessageEntityKey.is_forwarded] = isForwarded }
        (rawJSONObject.opt(MessageJsonKey.modified_ts) as? Double)?.let { jsonMap[MessageEntityKey.modified_ts] = it }
        (rawJSONObject.opt(MessageJsonKey.quoted_message) as? JSONObject)?.let {
            val quotedMessageId = (it.opt(MessageJsonKey.id) as? String) ?: ""
            if (quotedMessageId.isNotEmpty()) jsonMap[MessageEntityKey.quoted_message_id] = quotedMessageId
            saveJSONData(it)
        }
        (rawJSONObject.opt(MessageJsonKey.room_id) as? String)?.let { jsonMap[MessageEntityKey.room_id] = it }
        (rawJSONObject.opt(MessageJsonKey.sender) as? JSONObject)?.let {
            jsonMap[MessageEntityKey.sender_id] = (it.opt(MessageJsonKey.id) as? String ?: (it.opt(MessageJsonKey.id) as? Int ?: -1).toString())
            (it.opt(MessageJsonKey.type) as? String)?.let { type -> jsonMap[MessageEntityKey.sender_type] = type }
        }
        (rawJSONObject.opt(MessageJsonKey.sent_ts) as? Double)?.let { jsonMap[MessageEntityKey.sent_ts] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(MessageEntity::class.java, JSONObject(jsonMap))
            }
        }
        return id
    }

    fun saveJSONDatas(rawJSONArray: JSONArray): List<String> {
        val ids = mutableListOf<String>()
        for (i in 0 until rawJSONArray.length()) {
            rawJSONArray.optJSONObject(i)?.let { jsonObject ->
                saveJSONData(jsonObject)?.let { ids.add(it) }
            }
        }
        return ids
    }
}