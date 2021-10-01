package com.proto.type.base.repository

import com.proto.type.base.repository.asset_exchange.AssetExchangeRepositoryImpl
import com.proto.type.base.repository.asset_exchange.IAssetExchangeRepository
import com.proto.type.base.repository.chat.ChatRepositoryImpl
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.device.DeviceRepositoryImpl
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.local.LocalRepositoryImpl
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.repository.message.MessageRepositoryImpl
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<IAssetExchangeRepository> { AssetExchangeRepositoryImpl(get()) }
    single<IChatRepository> { ChatRepositoryImpl(get(), get()) }
    single<IDeviceRepository> { DeviceRepositoryImpl(get(), get(), get()) }
    single<ILocalRepository> { LocalRepositoryImpl(get()) }
    single<IMessageRepository> { MessageRepositoryImpl(get(), get(), get(), get()) }
    single<IUserRepository> { UserRepositoryImpl(get(), get(), get(), get()) }
}