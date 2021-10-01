package com.proto.type.base.data.database.dao.market_data

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.dao.BaseDao
import com.proto.type.base.data.database.entity.market_data.ChartEntity
import com.proto.type.base.data.database.entity.market_data.ChartEntityKey
import com.proto.type.base.data.mapper.market_data.ChartMapper
import com.proto.type.base.data.model.market_data.ChartJsonKey
import com.proto.type.base.data.model.market_data.ChartModel
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

class ChartDao: BaseDao() {

    // MARK: - Public Functions
    fun findChart(id: String): ChartModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(ChartEntity::class.java)
                .equalTo(ChartEntityKey.id, id)
                .findFirst()?.let { ChartMapper.toModel(it) }
        }
    }

    fun findCharts(symbolId: String): List<ChartModel> {
        Realm.getDefaultInstance().use { realm ->
            return ChartMapper.toModels(
                realm.where(ChartEntity::class.java)
                    .equalTo(ChartEntityKey.symbol_id, symbolId)
                    .findAll()
            )
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(ChartJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(ChartEntityKey.id to id)
        (rawJSONObject.opt(ChartJsonKey.data) as? JSONArray)?.let { jsonMap[ChartEntityKey.data_points] = it.toString() }
        (rawJSONObject.opt(ChartJsonKey.duration) as? String)?.let { jsonMap[ChartEntityKey.duration] = it }
        (rawJSONObject.opt(ChartJsonKey.symbol_id) as? String)?.let { jsonMap[ChartEntityKey.symbol_id] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(ChartEntity::class.java, rawJSONObject)
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