package com.proto.type

import com.proto.type.main.homeModule
import com.proto.type.base.getDataModule
import org.koin.core.module.Module

fun getModule(): List<Module> {
    return mutableListOf<Module>().apply {
        addAll(getDataModule())
        add(homeModule)
    }
}