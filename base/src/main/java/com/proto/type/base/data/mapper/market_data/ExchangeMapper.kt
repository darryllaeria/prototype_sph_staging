package com.proto.type.base.data.mapper.market_data

import com.proto.type.base.data.database.entity.market_data.ExchangeEntity
import com.proto.type.base.data.model.market_data.ExchangeModel
import com.proto.type.base.data.model.market_data.ExchangeTimezone
import com.proto.type.base.data.model.market_data.ExchangeTradingDay
import com.proto.type.base.extension.isValidJsonArray
import com.proto.type.base.extension.isValidJsonObject
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

object ExchangeMapper {

    // MARK: - Public Functions
    fun toEntities(exchangeModels: List<ExchangeModel>): List<ExchangeEntity> = exchangeModels.map { toEntity(it) }

    fun toEntity(exchangeModel: ExchangeModel): ExchangeEntity {
        val exchangeEntity = ExchangeEntity()
        val gSon = Gson()
        exchangeEntity.description_value = exchangeModel.description_value
        exchangeEntity.timezone = gSon.toJson(exchangeModel.exchange_timezone)
        exchangeEntity.trading_days_of_week = gSon.toJson(exchangeModel.exchange_trading_days)
        exchangeEntity.holidays = exchangeModel.holidays
        exchangeEntity.id = exchangeModel.id
        exchangeEntity.name = exchangeModel.name
        exchangeEntity.trading_sections = exchangeModel.trading_sections
        exchangeEntity.website = exchangeModel.website
        return exchangeEntity
    }

    fun toModel(exchangeEntity: ExchangeEntity): ExchangeModel {
        val exchangeModel = ExchangeModel()
        exchangeModel.description_value = exchangeEntity.description_value
        if (exchangeEntity.timezone.isValidJsonObject())
            exchangeModel.exchange_timezone = ExchangeTimezone(JSONObject(exchangeEntity.timezone))
        if (exchangeEntity.trading_days_of_week.isValidJsonArray()) {
            val tradingDaysJSONArray = JSONArray(exchangeEntity.trading_days_of_week)
            exchangeModel.exchange_trading_days.clear()
            for (i in 0 until tradingDaysJSONArray.length()) {
                val jsonObject = tradingDaysJSONArray.optJSONObject(i)
                if (jsonObject != null)
                    exchangeModel.exchange_trading_days.add(ExchangeTradingDay(jsonObject))
            }
        }
        exchangeModel.holidays = exchangeEntity.holidays
        exchangeModel.id = exchangeEntity.id
        exchangeModel.name = exchangeEntity.name
        exchangeModel.trading_sections = exchangeEntity.trading_sections
        exchangeModel.website = exchangeEntity.website
        return exchangeModel
    }

    fun toModels(exchangeEntities: List<ExchangeEntity>): List<ExchangeModel> = exchangeEntities.map { toModel(it) }
}