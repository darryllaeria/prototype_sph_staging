package com.proto.type.base

import com.proto.type.base.data.model.market_data.ChartPeriod
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.utils.DateTimeUtils

enum class AssetCategory(val value: String) {
    Crypto("crypto"),
    Other("other"),
    Real("real"),
    Stock("stock"),
    TildeOnly("tilde_only")
}

enum class BaseCurrency(val unit: String) {
    Usd("USD"),
    Yen("JPY")
}

enum class ChartDataFilterType(
    val value: String,
    val index: Int,
    val timeFrame: String,
    val totalTimeInSeconds: Int,
    val cryptoPeriod: ChartPeriod,
    val stockPeriod: ChartPeriod
) {
    FiveYears("5Y", 5, "Past 5 Years", 157680000, ChartPeriod(7, ChartPeriodType.Day), ChartPeriod(7, ChartPeriodType.Day)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int = totalTimeInSeconds
    }, // 157680000 5*365*24*60*60s
    OneDay("1D", 0, "Today", 86400, ChartPeriod(5, ChartPeriodType.Minute), ChartPeriod(5, ChartPeriodType.Minute)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int {
            val exchangeTradingDay = assetExchange.exchangeModel?.exchange_trading_days?.firstOrNull()
            return if (exchangeTradingDay != null)
                (exchangeTradingDay.end_ts - exchangeTradingDay.start_ts).toInt()
            else
                totalTimeInSeconds
        }
    }, // 86400 24*60*60s
    OneMonth("1M", 2, "Past Month", 2592000, ChartPeriod(4, ChartPeriodType.Hour), ChartPeriod(1, ChartPeriodType.Hour)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int = totalTimeInSeconds
    }, // 2592000 30*24*60*60s
    OneWeek("1W", 1, "Past Week", 604800, ChartPeriod(1, ChartPeriodType.Hour), ChartPeriod(30, ChartPeriodType.Minute)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int = totalTimeInSeconds
    }, // 604800 7*24*60*60s
    OneYear("1Y", 4, "Past Year", 31536000, ChartPeriod(1, ChartPeriodType.Day), ChartPeriod(1, ChartPeriodType.Day)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int = totalTimeInSeconds
    }, // 31536000 365*24*60*60s
    ThreeMonths("3M", 3, "Past 3 Months", 7776000, ChartPeriod(1, ChartPeriodType.Day), ChartPeriod(1, ChartPeriodType.Day)) {
        override fun totalTimeInSection(assetExchange: AssetExchangeModel): Int = totalTimeInSeconds
    }; // 7776000 3*30*24*60*60s

    // MARK: - Companion Object
    companion object {
        fun generateFilter(index: Int): ChartDataFilterType = values().firstOrNull { it.index == index } ?: OneDay
    }

    // MARK: - Public Function
    fun isInRangeForTs(timestamp: Double): Boolean = totalTimeInSeconds > DateTimeUtils.getCurrentTsForIos() - timestamp

    // MARK: - Abstract Function
    abstract fun totalTimeInSection(assetExchange: AssetExchangeModel): Int
}

enum class ChartPeriodType(val value: String, val seconds: Int) {
    Day("D", 86400),
    Hour("h", 3600),
    Minute("m", 60),
    Month("M", 2592000),
    Second("s", 1),
    Week("W", 604800),
    Year("Y", 31536000)
}


enum class SenderType(val type: String) {
    BotInstance("bot"),
    System("system"),
    User("user")
}