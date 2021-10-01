package com.proto.type.base.data.database.entity.market_data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object ChartEntityKey {
    const val data_points = "data_points"
    const val duration = "duration"
    const val id = "id"
    const val symbol_id = "symbol_id"
}

// MARK: - Open Class
open class ChartEntity(
    var data_points: String = "",
    var duration: String = "",
    @PrimaryKey var id: String = "",
    var symbol_id: String = ""

): RealmObject() {
    constructor() : this(id = "")
}