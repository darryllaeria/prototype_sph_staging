package com.proto.type.base.data.mapper.market_data

import com.proto.type.base.AssetCategory
import com.proto.type.base.data.model.market_data.AssetModel
import com.proto.type.base.data.database.entity.market_data.AssetEntity

object AssetMapper {

    // MARK: - Public Functions
    fun toEntities(assetModels: List<AssetModel>): List<AssetEntity> = assetModels.map { toEntity(it) }

    fun toEntity(assetModel: AssetModel): AssetEntity {
        val assetEntity = AssetEntity()
        assetEntity.category = assetModel.category.value
        assetEntity.circulating_supply = assetModel.circulating_supply
        assetEntity.currency_symbol = assetModel.currency_symbol
        assetEntity.description_value = assetModel.description_value
        assetEntity.id = assetModel.id
        assetEntity.name = assetModel.name
        assetEntity.next_halving = assetModel.next_halving
        assetEntity.reward_per_block = assetModel.reward_per_block
        assetEntity.total_supply = assetModel.total_supply
        return assetEntity
    }

    fun toModel(assetEntity: AssetEntity): AssetModel {
        val assetModel = AssetModel()
        assetModel.category = AssetCategory.values().firstOrNull { it.value == assetEntity.category } ?: AssetCategory.Other
        assetModel.circulating_supply = assetEntity.circulating_supply
        assetModel.currency_symbol = assetEntity.currency_symbol
        assetModel.description_value = assetEntity.description_value
        assetModel.id = assetEntity.id
        assetModel.name = assetEntity.name
        assetModel.next_halving = assetEntity.next_halving
        assetModel.reward_per_block = assetEntity.reward_per_block
        assetModel.total_supply = assetEntity.total_supply
        return assetModel
    }

    fun toModels(assetEntities: List<AssetEntity>): List<AssetModel> = assetEntities.map { toModel(it) }
}