package com.proto.type.base.data.model.market_data

import com.proto.type.base.AssetCategory
import com.proto.type.base.BaseCurrency
import com.proto.type.base.Constants
import com.proto.type.base.TextSuggestion
import com.proto.type.base.extension.isPositive
import com.proto.type.base.extension.noDecimalFormatted
import com.proto.type.base.extension.shortFormatted
import com.squareup.moshi.Json

// MARK: - Object
object AssetExchangeJsonKey {
    const val aggregate_id = "aggregate_id"
    const val close = "close"
    const val earnings = "earnings"
    const val high = "high"
    const val id = "id"
    const val price = "price"
    const val low = "low"
    const val name = "name"
    const val open = "open"
    const val peratio = "peratio"
    const val providers = "providers"
    const val rank = "rank"
    const val size = "size"
    const val supply = "supply"
    const val total_volume_24h = "total_volume_24h"
    const val total_volume_24h_to = "total_volume_24h_to"
    const val utc_ts = "utc_ts"
    const val value = "value"
    const val volume = "volume"
    const val yield = "yield"
}

// MARK: - Model Data Class
data class AssetExchangeModel(
    @Json(name = AssetExchangeJsonKey.id) override var id: String = "",
    @Json(name = AssetExchangeJsonKey.name) override var name: String = "",
    @Json(name = AssetExchangeJsonKey.providers) var providers: MutableList<String> = mutableListOf(),
    @Transient var aggregate_id: String = "",
    @Transient var base_asset: AssetModel? = null,
    @Transient var chart_id: String = "",
    @Transient var charts: MutableList<ChartModel> = mutableListOf(),
    @Transient var close_price: Double = 0.0,
    @Transient var earnings: Double = 0.0,
    @Transient var exchange_id: String = "",
    @Transient var high_price: Double = 0.0,
    @Transient var high52W_price: Double = 0.0,
    @Transient var key_financials: String = "",
    @Transient var latest_price: Double = 0.0,
    @Transient var low_price: Double = 0.0,
    @Transient var low52W_price: Double = 0.0,
    @Transient var mktcap: String = "",
    @Transient var mktcap_value: Double = 0.0,
    @Transient var open_price: Double = 0.0,
    @Transient var peratio: Double = 0.0,
    @Transient var rank: Int = 0,
    @Transient var quote_asset: AssetModel? = null,
    @Transient var size: Double = 0.0,
    @Transient var total_volume_24h: Double = 0.0,
    @Transient var total_volume_24h_to: Double = 0.0,
    @Transient var utc_ts: Double = 0.0,
    @Transient var volume: Double = 0.0,
    @Transient var yield: Double = 0.0
): TextSuggestion {
    // MARK: - Public Variable
    var exchangeModel: ExchangeModel? = null

    // MARK: - Public Functions
    fun latestPriceDisplay(): String = quoteCurrency() + formatPrice(latest_price)

    fun realtimePeriod(): String = if (base_asset?.category ?: AssetCategory.Crypto == AssetCategory.Crypto) "5s" else "rt"

    // MARK: - Private Functions
    private fun formatPrice(price: Double): String {
        if (!price.isPositive()) return "-"
        return quote_asset?.id?.let {
            if (it == BaseCurrency.Yen.unit && price >= 1000) // Jerome doesn't want to show decimal if the currency is YEN and the price is higher than 1000.
                price.noDecimalFormatted()
            else
                price.shortFormatted()
        } ?: run {
            price.shortFormatted()
        }
    }

    private fun quoteCurrency(): String = quote_asset?.currency_symbol ?: Constants.DEFAULT_CURRENCY
}