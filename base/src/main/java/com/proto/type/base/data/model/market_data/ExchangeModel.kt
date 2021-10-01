package com.proto.type.base.data.model.market_data

import com.proto.type.base.utils.DateTimeUtils
import com.squareup.moshi.Json
import org.json.JSONObject

// MARK: - Object
object ExchangeJsonKey {
    const val description_value = "description_value"
    const val endDay = "endDay"
    const val endTS = "endTS"
    const val gmtOffset = "gmtOffset"
    const val hasSummerTime = "hasSummerTime"
    const val holidays = "holidays"
    const val id = "id"
    const val name = "name"
    const val startDay = "startDay"
    const val startTS = "startTS"
    const val summerEndDate = "summerEndDate"
    const val summerOffset = "summerOffset"
    const val summerStartDate = "summerStartDate"
    const val timezone = "timezone"
    const val trading_days_of_week = "trading_days_of_week"
    const val trading_sections = "trading_sections"
    const val website = "website"
}

// MARK: - Model Data Classes
data class ExchangeModel(
    @Json(name = ExchangeJsonKey.id) var id: String = "",
    @Transient var description_value: String = "",
    @Transient var exchange_timezone: ExchangeTimezone = ExchangeTimezone(JSONObject()),
    @Transient var exchange_trading_days: MutableList<ExchangeTradingDay> = mutableListOf(),
    @Transient var holidays: String = "",
    @Transient var name: String = "",
    @Transient var trading_sections: String = "",
    @Transient var website: String = ""
) {
    // MARK: - Public Function
    fun queryChartTsForStock(exchangeTradingDay: ExchangeTradingDay? = null, currentZeroHourExchangeDateTs: Double = 0.0): Pair<Double, Double> { // Only use for stock
        // TODO("Implement this function and remove commented iOS code")
//        if let exchangeTradingDay = exchangeTradingDay {
//            let currentDate = Date(timeIntervalSince1970: currentZeroHourExchangeDateTS)
//            if exchangeTradingDay.startDay == currentDate.dayName(offset: exchangeTimeZone.tz) {
//            /// Current date is same with the `exchangeTradingDay`.
//            /// `startTime` should be calculated by `currentZeroHourExchangeDate` + `startTime` of `exchangeTradingDay`
//            /// `endTime` should be calculated by `currentZeroHourExchangeDate` + `endTime` of `exchangeTradingDay`
//            return (currentZeroHourExchangeDateTS + exchangeTradingDay.startTS, currentZeroHourExchangeDateTS + exchangeTradingDay.endTS)
//        } else {
//            // If the current day not the same with the `exchangeTradingDay`, that means current day is in the Weekend, should use Recursive one more time.
//            return queryChartTSsForStock(exchangeTradingDay: exchangeTradingDay, currentZeroHourExchangeDateTS: currentZeroHourExchangeDateTS - 24 * 3600)
//        }
//        } else {
//            let date = Date()
//
//            /// `dateName` Monday, Tuesday, Wednesday, Thursday, Friday. of the exchange time
//            let dayName = date.dayName(offset: exchangeTimeZone.tz)
//            let currentTS = date.timeIntervalSince1970
//
//                    /// `zeroHourExchangeDate` it is 00:00 at the exchange time.
//                    let zeroHourExchangeDate = date.zeroHourTimeIntervalSince1970(offset: exchangeTimeZone.tz)
//            guard let tradingDay = exchangeTradingDays.first(where: { $0.startDay == dayName }), let index = exchangeTradingDays.firstIndex(where: { $0.startDay == dayName }) else {
//                // Recursive - Current day is in the Weekend
//                // Because current day is the Weekend then we have to load the previous trading section.
//                return queryChartTSsForStock(exchangeTradingDay: exchangeTradingDays.last, currentZeroHourExchangeDateTS: zeroHourExchangeDate - 24 * 3600)
//            }
//            let startTime = zeroHourExchangeDate + tradingDay.startTS
//            if currentTS < startTime {
//                // Recursive - Use previous exchange date
//                // The currentTS is earlier the start time, that means the section does not start yet. i.e: Today is Tuesday, current time is 8h00 AM but the start time of trading section is 9h30 AM. Should get the chart at the previous trading section.
//                // If the current time is in Weekend, it should be loaded the chart of previous trading section. exactly last Friday.
//                let previous = index == 0 ? exchangeTradingDays.count - 1 : index - 1
//                return queryChartTSsForStock(exchangeTradingDay: exchangeTradingDays[previous], currentZeroHourExchangeDateTS: zeroHourExchangeDate - 24 * 3600)
//            } else {
//                let endTime = zeroHourExchangeDate + tradingDay.endTS
//
//                // The current day is in the same trading section day, startTime is the same with start section trading day time
//                // If the current time is earlier than the end time, that means the trading section is not over
//                // If the current time is later than the end time, that means the trading section is over
//                return (from: startTime, to: currentTS < endTime ? currentTS : endTime)
//            }
//        }
        return Pair(0.0, 0.0)
    }
}

data class ExchangeTimezone(
    @Json(name = ExchangeJsonKey.gmtOffset) val gmt_offset: Double = 0.0,
    @Json(name = ExchangeJsonKey.hasSummerTime) val has_summer_time: Boolean = false,
    @Json(name = ExchangeJsonKey.summerOffset) val summer_offset: Double = 0.0,
    @Transient val summer_end_date_ts: Double = 0.0,
    @Transient val summer_start_date_ts: Double = 0.0
) {
    // MARK: - Constructor
    constructor(rawDataJson: JSONObject): this(
        rawDataJson.optDouble(ExchangeJsonKey.gmtOffset),
        rawDataJson.optBoolean(ExchangeJsonKey.hasSummerTime),
        rawDataJson.optDouble(ExchangeJsonKey.summerOffset),
        DateTimeUtils.getTimestampFromFormattedTimeString(rawDataJson.optString(ExchangeJsonKey.summerEndDate) ?: "", DateTimeUtils.dd_MMM_yyyy_hh_mm_a),
        DateTimeUtils.getTimestampFromFormattedTimeString(rawDataJson.optString(ExchangeJsonKey.summerStartDate) ?: "", DateTimeUtils.dd_MMM_yyyy_hh_mm_a)
    ) { }

    // MARK: - Public Function
    fun tz(): Double {
        return if (!has_summer_time) {
            gmt_offset
        } else {
            val currentTs = DateTimeUtils.getCurrentTsForIos()
            if (currentTs in summer_start_date_ts..summer_end_date_ts) summer_offset else gmt_offset
        }
    }
}

data class ExchangeTradingDay(
    @Json(name = ExchangeJsonKey.endDay) val end_day: String = "",
    @Json(name = ExchangeJsonKey.endTS) val end_ts: Double = 0.0,
    @Json(name = ExchangeJsonKey.startDay) val start_day: String = "",
    @Json(name = ExchangeJsonKey.startTS) val start_ts: Double = 0.0
) {
    // MARK: - Constructor
    constructor(rawDataJson: JSONObject) : this(
        rawDataJson.optString(ExchangeJsonKey.endDay),
        rawDataJson.optDouble(ExchangeJsonKey.endTS),
        rawDataJson.optString(ExchangeJsonKey.startDay),
        rawDataJson.optDouble(ExchangeJsonKey.startTS)
    ) { }
}