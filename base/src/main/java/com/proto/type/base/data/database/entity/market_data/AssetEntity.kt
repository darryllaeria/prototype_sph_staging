package com.proto.type.base.data.database.entity.market_data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object AssetEntityKey {
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

// MARK: - Open Class
open class AssetEntity(
    var category: String = "",
    var circulating_supply: Double = 0.0,
    var currency_symbol: String = "",
    var description_value: String = "",
    @PrimaryKey var id: String = "",
    var name: String = "",
    var next_halving: Double = 0.0,
    var reward_per_block: Double = 0.0,
    var total_supply: Double = 0.0
): RealmObject() {
    // MARK: - Companion Object
    companion object {
        val TAG: String = AssetEntity::class.java.simpleName
    }

    // MARK: - Constructor Function
    constructor() : this(id = "")
}