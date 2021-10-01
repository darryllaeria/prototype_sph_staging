package com.proto.type.base.data.database.dao.market_data

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.dao.BaseDao
import com.proto.type.base.data.database.dao.MessageDao
import com.proto.type.base.data.database.entity.ChatEntityKey
import com.proto.type.base.data.database.entity.market_data.AssetExchangeEntity
import com.proto.type.base.data.database.entity.market_data.AssetExchangeEntityKey
import com.proto.type.base.data.mapper.market_data.AssetExchangeMapper
import com.proto.type.base.data.model.ChatJsonKey
import com.proto.type.base.data.model.market_data.AssetExchangeJsonKey
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class AssetExchangeDao: BaseDao() {

    // MARK: - Private Constant
    private val assetDao: AssetDao by lazy {
        AssetDao()
    }

    // MARK: - Public Functions
    fun deleteAssetExchangesNotIn(ids: Array<String>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(AssetExchangeEntity::class.java)
                    .not()
                    .`in`(AssetExchangeEntityKey.id, ids)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun findAssetExchange(id: String): AssetExchangeModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(AssetExchangeEntity::class.java)
                .equalTo(AssetExchangeEntityKey.id, id)
                .findFirst()?.let { AssetExchangeMapper.toModel(it) }
        }
    }

    fun findMultipleAssetExchangesExcept(ids: Array<String>): List<AssetExchangeModel> {
        Realm.getDefaultInstance().use { realm ->
            return AssetExchangeMapper.toModels(
                realm.where(AssetExchangeEntity::class.java)
                    .not()
                    .`in`(AssetExchangeEntityKey.id, ids)
                    .findAll()
            )
        }
    }

    fun getAllAssetExchanges(): List<AssetExchangeModel> {
        Realm.getDefaultInstance().use { realm ->
            return AssetExchangeMapper.toModels(
                realm.where(AssetExchangeEntity::class.java)
                    .findAll()
            )
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(AssetExchangeJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(AssetExchangeEntity::class.java, rawJSONObject)
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

    fun updateJSONData(rawJSONObject: JSONObject, isLatestPrice: Boolean = true): String? {
        val id = (rawJSONObject.opt(AssetExchangeJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(AssetExchangeEntityKey.id to id)
        (rawJSONObject.opt(AssetExchangeJsonKey.close) as? Double)?.let { jsonMap[AssetExchangeEntityKey.close_price] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.earnings) as? Double)?.let { jsonMap[AssetExchangeEntityKey.earnings] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.high) as? Double)?.let { jsonMap[AssetExchangeEntityKey.high_price] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.price) as? Double)?.let { jsonMap[AssetExchangeEntityKey.latest_price] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.low) as? Double)?.let { jsonMap[AssetExchangeEntityKey.low_price] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.value) as? Double)?.let { jsonMap[AssetExchangeEntityKey.mktcap_value] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.open) as? Double)?.let { jsonMap[AssetExchangeEntityKey.open_price] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.peratio) as? Double)?.let { jsonMap[AssetExchangeEntityKey.peratio] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.rank) as? Int)?.let { jsonMap[AssetExchangeEntityKey.rank] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.size) as? Double)?.let { jsonMap[AssetExchangeEntityKey.size] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.total_volume_24h) as? Double)?.let { jsonMap[AssetExchangeEntityKey.total_volume_24h] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.total_volume_24h_to) as? Double)?.let { jsonMap[AssetExchangeEntityKey.total_volume_24h_to] = it }
        if (isLatestPrice)
            (rawJSONObject.opt(AssetExchangeJsonKey.utc_ts) as? Double)?.let { jsonMap[AssetExchangeEntityKey.utc_ts] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.volume) as? Double)?.let { jsonMap[AssetExchangeEntityKey.volume] = it }
        (rawJSONObject.opt(AssetExchangeJsonKey.yield) as? Double)?.let { jsonMap[AssetExchangeEntityKey.volume] = it }
        // TODO: Need to check supply key and store to base AssetModel
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(AssetExchangeEntity::class.java, JSONObject(jsonMap))
            }
        }
        return id
    }
}