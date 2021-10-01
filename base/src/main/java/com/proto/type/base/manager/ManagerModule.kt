package com.proto.type.base.manager

import org.koin.dsl.module

val managerModule = module {
    single { PrefsManager(get()) }
}