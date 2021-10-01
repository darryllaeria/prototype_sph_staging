package com.proto.type.base.domain

import org.koin.dsl.module

val domainModule = module {
    single { CheckUserAuthorized(get()) }
    single { FindPrivateChat(get(), get()) }
    single { GetChatParticipants(get(), get()) }
    single { GetChatQUser(get()) }
    single { GetChats(get(), get()) }
    single { GetLocalContact(get()) }
    single { GetMessages(get()) }
    single { SyncContact(get()) }
}