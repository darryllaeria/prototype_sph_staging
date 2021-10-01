package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.entity.ChatEntity
import com.proto.type.base.data.database.entity.ChatEntityKey
import com.proto.type.base.data.mapper.ChatMapper
import com.proto.type.base.data.model.AvatarJsonKey
import com.proto.type.base.data.model.ChatJsonKey
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.extension.toListAny
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class MobileDao: BaseDao() {

    // MARK: - Public Functions
//    fun deleteByQuarter(quarter: String) {
//        Realm.getDefaultInstance().use { realm ->
//            realm.executeTransaction {
//                it.where(ChatEntity::class.java)
//                    .equalTo(ChatEntityKey.id, quarter)
//                    .findAll()
//                    .deleteAllFromRealm()
//            }
//        }
//    }
//
//    fun findQuarter(quarter: String): ChatModel? {
//        Realm.getDefaultInstance().use { realm ->
//            return realm.where(ChatEntity::class.java)
//                .equalTo(ChatEntityKey.id, quarter)
//                .findFirst()?.let { ChatMapper.toModel(it) }
//        }
//    }
//
//    fun findChats(quarter: String): List<ChatModel> {
//        Realm.getDefaultInstance().use { realm ->
//            return ChatMapper.toModels(
//                realm.where(ChatEntity::class.java)
//                    .equalTo(fieldId, fieldValue)
//                    .findAll()
//            )
//        }
//    }
//
//    fun getAllChats(): List<ChatModel> {
//        Realm.getDefaultInstance().use { realm ->
//            return ChatMapper.toModels(
//                realm.where(ChatEntity::class.java)
//                    .findAll()
//            ).sortedByDescending { it.last_message?.sent_ts ?: 0.0 }
//        }
//    }
//
//    fun saveJSONData(rawJSONObject: JSONObject): String? {
//        return null
//    }
//
//    fun saveJSONDatas(rawJSONArray: JSONArray): List<String> {
//        val ids = mutableListOf<String>()
//        for (i in 0 until rawJSONArray.length()) {
//            rawJSONArray.optJSONObject(i)?.let { jsonObject ->
//                saveJSONData(jsonObject)?.let { ids.add(it) }
//            }
//        }
//        return ids
//    }
}