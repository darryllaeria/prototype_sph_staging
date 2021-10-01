package com.proto.type.base.data.database

import com.proto.type.base.data.database.dao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.dsl.module
import java.util.concurrent.Executors

val localModule = module {
    single { ChatDao() }
    single { UserDao() }

    single { CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) }
}