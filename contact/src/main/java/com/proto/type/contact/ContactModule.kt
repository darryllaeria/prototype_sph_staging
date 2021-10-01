package com.proto.type.contact

import com.proto.type.contact.ui.contact.ContactViewModel
import com.proto.type.contact.ui.scanqrcode.ScanQrCodeViewModel
import com.proto.type.contact.ui.search.SearchAllViewModel
import com.proto.type.contact.ui.yourqrcode.UserQrCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val contactModule = module {
    viewModel { ContactViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { UserQrCodeViewModel(get(), get()) }

    viewModel { ScanQrCodeViewModel(get(), get()) }

    viewModel { SearchAllViewModel(get()) }
}