package com.proto.type.base

import com.proto.type.base.data.database.localModule
import com.proto.type.base.data.encryption.KeyStoreWrapper
import com.proto.type.base.data.remote.remoteModule
import com.proto.type.base.domain.domainModule
import com.proto.type.base.manager.managerModule
import com.proto.type.base.repository.repositoryModule
import org.koin.core.module.Module
import org.koin.dsl.module

val utilModule = module {
    single { KeyStoreWrapper(get()) }
}

fun getDataModule(): List<Module> {
    return mutableListOf(utilModule).apply {
        add(domainModule)
        add(localModule)
        add(managerModule)
        add(remoteModule)
        add(repositoryModule)
    }
}