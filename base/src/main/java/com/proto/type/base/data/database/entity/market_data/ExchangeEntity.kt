package com.proto.type.base.data.database.entity.market_data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object ExchangeEntityKey {
    const val description_value = "description_value"
    const val id = "id"
    const val holidays = "holidays"
    const val name = "name"
    const val timezone = "timezone"
    const val trading_days_of_week = "trading_days_of_week"
    const val trading_sections = "trading_sections"
    const val website = "website"
}

// MARK: - Open Class
open class ExchangeEntity(
    var description_value: String = "",
    @PrimaryKey var id: String = "",
    var holidays: String = "",
    var name: String = "",
    var timezone: String = "",
    var trading_days_of_week: String = "",
    var trading_sections: String = "",
    var website: String = ""
): RealmObject() {
    // MARK: - Companion Object
    companion object {
        val TAG: String = ExchangeEntity::class.java.simpleName
    }

    // MARK: - Constructor Function
    constructor() : this(id = "")
}