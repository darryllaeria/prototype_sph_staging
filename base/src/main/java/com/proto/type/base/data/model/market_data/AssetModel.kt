package com.proto.type.base.data.model.market_data

import com.proto.type.base.AssetCategory
import com.proto.type.base.Constants
import com.proto.type.base.extension.isPositive
import com.proto.type.base.extension.withSpace
import com.proto.type.base.utils.DateTimeUtils
import com.squareup.moshi.Json

// MARK: - Object
object AssetJsonKey {
    const val category = "category"
    const val circulating_supply = "circulating_supply"
    const val currency_symbol = "currency_symbol"
    const val description_value = "description_value"
    const val id = "id"
    const val name = "name"
    const val next_halving = "next_halving"
    const val reward_per_block = "reward_per_block"
    const val total_supply = "total_supply"
}

// MARK: - Model Data Class
data class AssetModel(
    @Json(name = AssetJsonKey.id) var id: String = "",
    @Transient var category: AssetCategory = AssetCategory.Other,
    @Transient var circulating_supply: Double = 0.0,
    @Transient var currency_symbol: String = "",
    @Transient var description_value: String = "",
    @Transient var name: String = "",
    @Transient var next_halving: Double = 0.0,
    @Transient var reward_per_block: Double = 0.0,
    @Transient var total_supply: Double = 0.0
) {
    // MARK: - Public Functions
    fun circulatingSupplyDisplay(): String {
        return if (circulating_supply.isPositive())
            circulating_supply.withSpace()
        else
            Constants.STRING_DASH
    }

    fun nextHalvingDisplay(): String {
        return if (next_halving.isPositive())
            DateTimeUtils.getDateString(next_halving, DateTimeUtils.dd_MMMM_yyyy)
        else
            Constants.STRING_DASH
    }

    fun rewardPerBlockDisplay(): String {
        return if (reward_per_block.isPositive())
            "$reward_per_block $id"
        else
            Constants.STRING_DASH
    }

    fun totalSupplyDisplay(): String {
        return if (total_supply.isPositive())
            total_supply.withSpace()
        else
            Constants.STRING_DASH
    }
}