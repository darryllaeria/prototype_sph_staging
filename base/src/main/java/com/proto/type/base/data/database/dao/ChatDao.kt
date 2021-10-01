package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.entity.ChatEntity
import com.proto.type.base.data.database.entity.ChatEntityKey
import com.proto.type.base.data.database.entity.MessageEntityKey
import com.proto.type.base.data.mapper.ChatMapper
import com.proto.type.base.data.model.AvatarJsonKey
import com.proto.type.base.data.model.ChatJsonKey
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.extension.toListAny
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class ChatDao: BaseDao() {

    // MARK: - Private Constant
    private val messageDao: MessageDao by lazy {
         MessageDao()
    }

    // MARK: - Public Functions
    fun deleteChat(roomId: String) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(ChatEntity::class.java)
                    .equalTo(ChatEntityKey.id, roomId)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun findChat(id: String): ChatModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(ChatEntity::class.java)
                .equalTo(ChatEntityKey.id, id)
                .findFirst()?.let { ChatMapper.toModel(it) }
        }
    }

    fun findChats(fieldId: String, fieldValue: String): List<ChatModel> {
        Realm.getDefaultInstance().use { realm ->
            return ChatMapper.toModels(
                realm.where(ChatEntity::class.java)
                .equalTo(fieldId, fieldValue)
                .findAll()
            )
        }
    }

    fun getAllChats(): List<ChatModel> {
        Realm.getDefaultInstance().use { realm ->
            return ChatMapper.toModels(
                realm.where(ChatEntity::class.java)
                .findAll()
            ).sortedByDescending { it.last_message?.sent_ts ?: 0.0 }
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(ChatJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(ChatEntityKey.id to id)
        var adminIds = listOf<String>()
        (rawJSONObject.opt(ChatJsonKey.admins) as? JSONArray)?.let {
            jsonMap[ChatEntityKey.admin_ids] = it
            adminIds = it.toListAny().filterIsInstance<String>()
        }
        (rawJSONObject.opt(ChatJsonKey.avatar) as? JSONObject)?.let {
            if (!(it.opt(AvatarJsonKey.url) as? String).isNullOrEmpty()) jsonMap[ChatEntityKey.avatar] = it
        }
        (rawJSONObject.opt(ChatJsonKey.category) as? String)?.let { jsonMap[ChatEntityKey.category] = it }
        (rawJSONObject.opt(ChatJsonKey.created_by) as? String)?.let { jsonMap[ChatEntityKey.creator_id] = it }
        (rawJSONObject.opt(ChatJsonKey.is_pinned) as? Boolean)?.let { jsonMap[ChatEntityKey.is_pinned] = it }
        jsonMap[ChatEntityKey.is_removed] = false
        (rawJSONObject.opt(ChatJsonKey.last_message) as? JSONObject)?.let {
            it.put(MessageEntityKey.room_id, id)
            messageDao.saveJSONData(it)
        }
        (rawJSONObject.opt(ChatJsonKey.name) as? String)?.let { jsonMap[ChatEntityKey.name] = it }
        (rawJSONObject.opt(ChatJsonKey.participants) as? JSONArray)?.let {
            var participantIds = it.toListAny().filterIsInstance<String>().toMutableList()
            adminIds.forEach { adminId ->
                if (!participantIds.contains(adminId)) participantIds.add(adminId)
            }
            jsonMap[ChatEntityKey.participant_ids] = participantIds
        }
        (rawJSONObject.opt(ChatJsonKey.read_message_ts) as? Double)?.let { jsonMap[ChatEntityKey.read_message_ts] = it }
        (rawJSONObject.opt(ChatJsonKey.unread_bot_count) as? Int)?.let { jsonMap[ChatEntityKey.unread_bot_count] = it }
        (rawJSONObject.opt(ChatJsonKey.unread_count) as? Int)?.let { jsonMap[ChatEntityKey.unread_count ] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(ChatEntity::class.java, JSONObject(jsonMap))
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