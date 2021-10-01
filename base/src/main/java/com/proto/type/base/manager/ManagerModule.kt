package com.proto.type.base.manager

import org.koin.dsl.module

val managerModule = module {
    single { ChatMessagesManager(get(), get()) }
    single { PrefsManager(get()) }
    single { TypingUsersManager() }
}