package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.entity.*
import com.proto.type.base.data.mapper.MessageMediaItemMapper
import com.proto.type.base.data.model.MessageItemJsonKey
import com.proto.type.base.data.model.MessageJsonKey
import com.proto.type.base.data.model.MessageMediaItemModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class MessageMediaItemDao: BaseDao() {

    // MARK: - Companion Object
    companion object {
        private val TAG = MessageMediaItemDao::class.java.simpleName
    }

    // MARK: - Public Functions
    fun deleteMessageMediaItems(roomId: String) {
        Realm.getDefaultInstance().use {
            it.where(MessageMediaItemEntity::class.java)
                .equalTo(MessageEntityKey.room_id, roomId)
                .findAll()
                .deleteAllFromRealm()
        }
    }

    fun getMessageMediaItems(roomId: String): List<MessageMediaItemModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageMediaItemMapper.toModels(
                realm.where(MessageMediaItemEntity::class.java)
                    .equalTo(MessageEntityKey.room_id, roomId)
                    .findAll()
            )
        }
    }

    fun saveJSONDatas(rawJSONArray: JSONArray, roomId: String): List<String> {
        val ids = mutableListOf<String>()
        for (i in 0 until rawJSONArray.length()) {
            rawJSONArray.optJSONObject(i)?.let { jsonObject ->
                jsonObject.put(MessageItemJsonKey.room_id, roomId)
                saveJSONData(jsonObject)?.let { ids.add(it) }
            }
        }
        return ids
    }

    // MARK: - Private Function
    private fun saveJSONData(rawJSONObject: JSONObject): String? {
        val messageId = (rawJSONObject.opt(MessageItemJsonKey.message_id) as? String) ?: ""
        val roomId = (rawJSONObject.opt(MessageItemJsonKey.room_id) as? String) ?: ""
        if (messageId.isEmpty() || roomId.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(
            MessageMediaItemEntityKey.id to "$messageId.$roomId",
            MessageMediaItemEntityKey.message_id to messageId,
            MessageMediaItemEntityKey.room_id to roomId
        )
        (rawJSONObject.opt(MessageItemJsonKey.data) as? JSONObject)?.let {
            (it.opt(MessageItemJsonKey.id) as? String)?.let { value -> jsonMap[MessageMediaItemEntityKey.media_id] = value }
            (it.opt(MessageJsonKey.url) as? String)?.let { value -> jsonMap[MessageMediaItemEntityKey.url] = value }
        }
        (rawJSONObject.opt(MessageItemJsonKey.sent_ts) as? Double)?.let { jsonMap[MessageMediaItemEntityKey.sent_ts] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(MessageMediaItemEntity::class.java, rawJSONObject)
            }
        }
        return messageId
    }
}