package com.proto.type.base.data.database

import com.proto.type.base.data.database.dao.*
import com.proto.type.base.data.database.dao.market_data.AssetDao
import com.proto.type.base.data.database.dao.market_data.AssetExchangeDao
import com.proto.type.base.data.database.dao.market_data.ChartDao
import com.proto.type.base.data.database.dao.market_data.ExchangeDao

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.dsl.module
import java.util.concurrent.Executors

val localModule = module {
    single { AssetDao() }
    single { AssetExchangeDao() }
    single { ExchangeDao() }
    single { ChartDao() }
    single { ChatDao() }
    single { MessageDao() }
    single { MessageLinkItemDao() }
    single { MessageMediaItemDao() }
    single { UserDao() }

    single { CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) }
}