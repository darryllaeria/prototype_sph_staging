package com.proto.type.base.data.mapper.market_data

import com.proto.type.base.ChartDataFilterType
import com.proto.type.base.data.database.entity.market_data.ChartEntity
import com.proto.type.base.data.model.market_data.ChartModel
import com.proto.type.base.data.model.market_data.DataPoint
import com.proto.type.base.extension.isValidJsonArray
import com.proto.type.base.utils.DateTimeUtils
import com.google.gson.Gson
import org.json.JSONArray

object ChartMapper {

    // MARK: - Public Functions
    fun toEntities(chartModels: List<ChartModel>): List<ChartEntity> = chartModels.map { toEntity(it) }

    fun toEntity(chartModel: ChartModel): ChartEntity {
        val chartEntity = ChartEntity()
        val dataPointsSource = chartModel.data_points.map { mutableListOf(DateTimeUtils.getCurrentTsForIos(it.date.time), it.value) }
        chartEntity.data_points = Gson().toJson(dataPointsSource)
        chartEntity.id = chartModel.id
        chartEntity.symbol_id = chartModel.symbol_id
        chartEntity.duration = chartModel.duration.value
        return chartEntity
    }

    fun toModel(chartEntity: ChartEntity): ChartModel {
        val chartModel = ChartModel()
        if (chartEntity.data_points.isValidJsonArray()) {
            val dataPointsJSONArray = JSONArray(chartEntity.data_points)
            chartModel.data_points.clear()
            for (i in 0 until dataPointsJSONArray.length()) {
                (dataPointsJSONArray.opt(i) as? MutableList<Double>)?.let { pointData ->
                    if (pointData.size == 2)
                        chartModel.data_points.add(DataPoint(pointData))
                }
            }
        }
        chartModel.id = chartEntity.id
        chartModel.symbol_id = chartEntity.symbol_id
        chartModel.duration = ChartDataFilterType.values().firstOrNull { it.value == chartEntity.duration } ?: ChartDataFilterType.OneDay
        return chartModel
    }

    fun toModels(chartEntities: List<ChartEntity>): List<ChartModel> = chartEntities.map { toModel(it) }
}