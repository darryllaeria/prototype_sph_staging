package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.entity.*
import com.proto.type.base.data.mapper.MessageLinkItemMapper
import com.proto.type.base.data.model.MessageItemJsonKey
import com.proto.type.base.data.model.MessageLinkItemModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class MessageLinkItemDao: BaseDao() {

    // MARK: - Public Functions
    fun getMessageLinkItems(roomId: String): List<MessageLinkItemModel> {
        return Realm.getDefaultInstance().use { realm ->
            MessageLinkItemMapper.toModels(
                realm.where(MessageLinkItemEntity::class.java)
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
        (rawJSONObject.opt(MessageItemJsonKey.sent_ts) as? Double)?.let { jsonMap[MessageLinkItemEntityKey.sent_ts] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(MessageLinkItemEntity::class.java, rawJSONObject)
            }
        }
        return messageId
    }
}