package com.proto.type.base.repository

import com.proto.type.base.repository.chat.ChatRepositoryImpl
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.local.LocalRepositoryImpl
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<IChatRepository> { ChatRepositoryImpl(get(), get()) }
    single<ILocalRepository> { LocalRepositoryImpl(get()) }
    single<IUserRepository> { UserRepositoryImpl(get(), get(), get(), get()) }
}