package com.proto.type.base.data.model.market_data

import android.graphics.Point
import com.proto.type.base.ChartDataFilterType
import com.proto.type.base.ChartPeriodType
import com.proto.type.base.utils.DateTimeUtils
import com.squareup.moshi.Json
import java.sql.Timestamp
import java.util.*

// MARK: - Object
object ChartJsonKey {
    const val id = "id"
    const val data = "data"
    const val data_points = "data_points"
    const val duration = "duration"
    const val symbol_id = "symbol_id"
}

// MARK: - Model Class
class ChartPeriod(
    val number: Int,
    val time: ChartPeriodType
) {

    // MARK: - Public Function
    fun getTotalSeconds(): Int = number * time.seconds
}

class DataPoint(
    var date: Date = Date(0),
    var point: Point = Point(),
    var value: Double = 0.0
) {
    // MARK: - Constructor
    constructor(rawData: MutableList<Double>) : this(Date(0), Point(), 0.0) {
        if (rawData.size == 2) {
            this.date = Date(Timestamp(DateTimeUtils.getMillisForAndroid(rawData.first())).time)
            this.value = rawData.last()
        }
    }
}

// MARK: - Model Data Class
data class ChartModel(
    @Json(name = ChartJsonKey.id) var id: String = "",
    @Transient var data_points: MutableList<DataPoint> = mutableListOf(),
    @Transient var duration: ChartDataFilterType = ChartDataFilterType.OneDay,
    @Transient var symbol_id: String = ""
) {
    // MARK: - Constructor
    constructor(symbolId: String, duration: ChartDataFilterType, data_points: MutableList<Any>) : this(
        symbol_id = symbolId,
        duration = duration
    ) {
        this.id = symbolId + "_" + duration.value
        for (dataPoint in data_points) {
            (dataPoint as? MutableList<Double>)?.let { data ->
                if (data.size == 2)
                    data_points.add(DataPoint(data))
            }
        }
    }
}