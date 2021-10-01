package com.proto.type.base.data.database.entity.market_data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object AssetExchangeEntityKey {
    const val aggregate_id = "aggregate_id"
    const val base_asset_id = "base_asset_id"
    const val chart_id = "chart_id"
    const val close_price = "close_price"
    const val earnings = "earnings"
    const val exchange_id = "exchange_id"
    const val high_price = "high_price"
    const val high52W_price = "high52W_price"
    const val id = "id"
    const val key_financials = "key_financials"
    const val latest_price = "latest_price"
    const val low_price = "low_price"
    const val low52W_price = "low52W_price"
    const val mktcap = "mktcap"
    const val mktcap_value = "mktcap_value"
    const val name = "name"
    const val open_price = "open_price"
    const val peratio = "peratio"
    const val providers = "providers"
    const val quote_asset_id = "quote_asset_id"
    const val rank = "rank"
    const val size = "size"
    const val total_volume_24h = "total_volume_24h"
    const val total_volume_24h_to = "total_volume_24h_to"
    const val utc_ts = "utc_ts"
    const val volume = "volume"
    const val yield = "yield"
}

// MARK: - Open Class
open class AssetExchangeEntity(
    var aggregate_id: String = "",
    var base_asset_id: String = "",
    var chart_id: String = "",
    var close_price: Double = 0.0,
    var earnings: Double = 0.0,
    var exchange_id: String = "",
    var high_price: Double = 0.0,
    var high52W_price: Double = 0.0,
    @PrimaryKey var id: String = "",
    var key_financials: String = "",
    var latest_price: Double = 0.0,
    var low_price: Double = 0.0,
    var low52W_price: Double = 0.0,
    var mktcap: String = "",
    var mktcap_value: Double = 0.0,
    var name: String = "",
    var open_price: Double = 0.0,
    var peratio: Double = 0.0, // Price-earnings ratio. Only use for stock
    var providers: RealmList<String> = RealmList(),
    var quote_asset_id: String = "",
    var rank: Int = 0,
    var size: Double = 0.0,
    var total_volume_24h: Double = 0.0,
    var total_volume_24h_to: Double = 0.0,
    var utc_ts: Double = 0.0,
    var volume: Double = 0.0,
    var yield: Double = 0.0 // Only use for stock
): RealmObject() {
    // MARK: - Companion Object
    companion object {
        val TAG: String = AssetExchangeEntity::class.java.simpleName
    }

    // MARK: - Constructor Function
    constructor() : this(id = "")
}