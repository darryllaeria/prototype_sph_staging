package com.proto.type.base.data.mapper.market_data

import com.proto.type.base.data.database.dao.market_data.AssetDao
import com.proto.type.base.data.database.dao.market_data.ChartDao
import com.proto.type.base.data.database.dao.market_data.ExchangeDao
import com.proto.type.base.data.database.entity.market_data.AssetExchangeEntity
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.extension.convertToList
import com.proto.type.base.extension.convertToRealmList

object AssetExchangeMapper {

    // MARK: - Private Constants
    private val assetDao: AssetDao by lazy {
        AssetDao()
    }
    private val chartDao: ChartDao by lazy {
        ChartDao()
    }
    private val exchangeDao: ExchangeDao by lazy {
        ExchangeDao()
    }

    // MARK: - Public Functions
    fun toEntities(assetExchangeModels: List<AssetExchangeModel>): List<AssetExchangeEntity> = assetExchangeModels.map { toEntity(it) }

    fun toEntity(assetExchangeModel: AssetExchangeModel): AssetExchangeEntity {
        val assetExchangeEntity = AssetExchangeEntity()
        assetExchangeEntity.aggregate_id = assetExchangeModel.aggregate_id
        assetExchangeEntity.chart_id = assetExchangeModel.chart_id
        assetExchangeEntity.close_price = assetExchangeModel.close_price
        assetExchangeEntity.earnings = assetExchangeModel.earnings
        assetExchangeEntity.exchange_id = assetExchangeModel.exchange_id
        assetExchangeEntity.high_price = assetExchangeModel.high_price
        assetExchangeEntity.high52W_price = assetExchangeModel.high52W_price
        assetExchangeEntity.id = assetExchangeModel.id
        assetExchangeEntity.key_financials = assetExchangeModel.key_financials
        assetExchangeEntity.latest_price = assetExchangeModel.latest_price
        assetExchangeEntity.low_price = assetExchangeModel.low_price
        assetExchangeEntity.low52W_price = assetExchangeModel.low52W_price
        assetExchangeEntity.mktcap = assetExchangeModel.mktcap
        assetExchangeEntity.mktcap_value = assetExchangeModel.mktcap_value
        assetExchangeEntity.key_financials = assetExchangeModel.key_financials
        assetExchangeEntity.latest_price = assetExchangeModel.latest_price
        assetExchangeEntity.name = assetExchangeModel.name
        assetExchangeEntity.open_price = assetExchangeModel.open_price
        assetExchangeEntity.peratio = assetExchangeModel.peratio
        assetExchangeEntity.providers = assetExchangeEntity.providers.convertToRealmList()
        assetExchangeEntity.rank = assetExchangeModel.rank
        assetExchangeEntity.size = assetExchangeModel.size
        assetExchangeEntity.total_volume_24h = assetExchangeModel.total_volume_24h
        assetExchangeEntity.total_volume_24h_to = assetExchangeModel.total_volume_24h_to
        assetExchangeEntity.utc_ts = assetExchangeModel.utc_ts
        assetExchangeEntity.volume = assetExchangeModel.volume
        assetExchangeEntity.yield = assetExchangeModel.yield
        return assetExchangeEntity
    }

    fun toModel(assetExchangeEntity: AssetExchangeEntity): AssetExchangeModel {
        val assetExchangeModel = AssetExchangeModel()
        assetExchangeModel.aggregate_id = assetExchangeEntity.aggregate_id
        assetExchangeModel.base_asset = assetDao.findAsset(assetExchangeEntity.base_asset_id)
        assetExchangeModel.chart_id = assetExchangeEntity.chart_id
        assetExchangeModel.charts = chartDao.findCharts(assetExchangeEntity.id).toMutableList()
        assetExchangeModel.close_price = assetExchangeEntity.close_price
        assetExchangeModel.earnings = assetExchangeEntity.earnings
        assetExchangeModel.exchange_id = assetExchangeEntity.exchange_id
        assetExchangeModel.exchangeModel = exchangeDao.findExchange(assetExchangeEntity.exchange_id)
        assetExchangeModel.high_price = assetExchangeEntity.high_price
        assetExchangeModel.high52W_price = assetExchangeEntity.high52W_price
        assetExchangeModel.id = assetExchangeEntity.id
        assetExchangeModel.key_financials = assetExchangeEntity.key_financials
        assetExchangeModel.latest_price = assetExchangeEntity.latest_price
        assetExchangeModel.low_price = assetExchangeEntity.low_price
        assetExchangeModel.low52W_price = assetExchangeEntity.low52W_price
        assetExchangeModel.mktcap = assetExchangeEntity.mktcap
        assetExchangeModel.mktcap_value = assetExchangeEntity.mktcap_value
        assetExchangeModel.key_financials = assetExchangeEntity.key_financials
        assetExchangeModel.latest_price = assetExchangeEntity.latest_price
        assetExchangeModel.name = assetExchangeEntity.name
        assetExchangeModel.open_price = assetExchangeEntity.open_price
        assetExchangeModel.peratio = assetExchangeEntity.peratio
        assetExchangeModel.providers = assetExchangeEntity.providers.convertToList().toMutableList()
        assetExchangeModel.quote_asset = assetDao.findAsset(assetExchangeEntity.quote_asset_id)
        assetExchangeModel.rank = assetExchangeEntity.rank
        assetExchangeModel.size = assetExchangeEntity.size
        assetExchangeModel.total_volume_24h = assetExchangeEntity.total_volume_24h
        assetExchangeModel.total_volume_24h_to = assetExchangeEntity.total_volume_24h_to
        assetExchangeModel.utc_ts = assetExchangeEntity.utc_ts
        assetExchangeModel.volume = assetExchangeEntity.volume
        assetExchangeModel.yield = assetExchangeEntity.yield
        return assetExchangeModel
    }

    fun toModels(assetExchangeEntities: List<AssetExchangeEntity>): List<AssetExchangeModel> = assetExchangeEntities.map { toModel(it) }
}