package com.proto.type

import com.proto.type.home.homeModule
import com.proto.type.base.getDataModule
import com.proto.type.chat.chatModule
import org.koin.core.module.Module

fun getModule(): List<Module> {
    return mutableListOf<Module>().apply {
        addAll(getDataModule())
        add(homeModule)
        add(chatModule)
    }
}