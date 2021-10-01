package com.proto.type.base.data.database.dao.market_data

import com.proto.type.base.data.database.dao.BaseDao
import com.proto.type.base.data.database.entity.market_data.AssetEntity
import com.proto.type.base.data.database.entity.market_data.AssetEntityKey
import com.proto.type.base.data.mapper.market_data.AssetMapper
import com.proto.type.base.data.model.market_data.AssetJsonKey
import com.proto.type.base.data.model.market_data.AssetModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class AssetDao: BaseDao() {

    // MARK: - Public Functions
    fun deleteAssetsNotIn(ids: Array<String>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(AssetEntity::class.java)
                    .not()
                    .`in`(AssetEntityKey.id, ids)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun findAsset(id: String): AssetModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(AssetEntity::class.java)
                .equalTo(AssetEntityKey.id, id)
                .findFirst()?.let { AssetMapper.toModel(it) }
        }
    }

    fun getAllAssets(): List<AssetModel> {
        Realm.getDefaultInstance().use { realm ->
            return AssetMapper.toModels(
                realm.where(AssetEntity::class.java)
                    .findAll()
            )
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(AssetJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(AssetEntity::class.java, rawJSONObject)
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