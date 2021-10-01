package com.proto.type.base.data.database.dao.market_data

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.dao.BaseDao
import com.proto.type.base.data.database.entity.market_data.ExchangeEntity
import com.proto.type.base.data.database.entity.market_data.ExchangeEntityKey
import com.proto.type.base.data.mapper.market_data.ExchangeMapper
import com.proto.type.base.data.model.market_data.ExchangeJsonKey
import com.proto.type.base.data.model.market_data.ExchangeModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class ExchangeDao: BaseDao() {

    // MARK: - Public Functions
    fun deleteExchangesNotIn(ids: Array<String>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(ExchangeEntity::class.java)
                    .not()
                    .`in`(ExchangeEntityKey.id, ids)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun findExchange(id: String): ExchangeModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(ExchangeEntity::class.java)
                .equalTo(ExchangeEntityKey.id, id)
                .findFirst()?.let { ExchangeMapper.toModel(it) }
        }
    }

    fun getAllExchanges(): List<ExchangeModel> {
        Realm.getDefaultInstance().use { realm ->
            return ExchangeMapper.toModels(
                realm.where(ExchangeEntity::class.java)
                    .findAll()
            )
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(ExchangeJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(ExchangeEntityKey.id to id)
        (rawJSONObject.opt(ExchangeJsonKey.description_value) as? String)?.let { jsonMap[ExchangeEntityKey.description_value] = it }
        (rawJSONObject.opt(ExchangeJsonKey.holidays) as? String)?.let { jsonMap[ExchangeEntityKey.holidays] = it }
        (rawJSONObject.opt(ExchangeJsonKey.name) as? String)?.let { jsonMap[ExchangeEntityKey.name] = it }
        (rawJSONObject.opt(ExchangeJsonKey.timezone) as? JSONObject)?.let { jsonMap[ExchangeEntityKey.timezone] = it.toString() }
        (rawJSONObject.opt(ExchangeJsonKey.trading_days_of_week) as? JSONArray)?.let { jsonMap[ExchangeEntityKey.trading_days_of_week] = it.toString() }
        (rawJSONObject.opt(ExchangeJsonKey.trading_sections) as? String)?.let { jsonMap[ExchangeEntityKey.trading_sections] = it }
        (rawJSONObject.opt(ExchangeJsonKey.website) as? String)?.let { jsonMap[ExchangeEntityKey.website] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(ExchangeEntity::class.java, rawJSONObject)
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