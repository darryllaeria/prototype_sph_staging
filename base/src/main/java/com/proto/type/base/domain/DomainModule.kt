package com.proto.type.base.domain

import org.koin.dsl.module

val domainModule = module {
    single { CheckUserAuthorized(get()) }
    single { GetChatParticipants(get(), get()) }
    single { GetChatQUser(get()) }
    single { GetChats(get(), get()) }
}