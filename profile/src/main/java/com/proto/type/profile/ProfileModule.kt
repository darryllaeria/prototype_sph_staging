package com.proto.type.profile

import com.proto.type.profile.ui.appearance.AppearanceViewModel
import com.proto.type.profile.ui.appearance.wallpaper.WallpaperViewModel
import com.proto.type.profile.ui.contact.ViewContactViewModel
import com.proto.type.profile.ui.profileTab.ProfileTabViewModel
import com.proto.type.profile.ui.delete.DeleteAccountViewModel
import com.proto.type.profile.ui.email.inbox.CheckInboxViewModel
import com.proto.type.profile.ui.email.request.EmailVerifyViewModel
import com.proto.type.profile.ui.language.LanguageViewModel
import com.proto.type.profile.ui.notification.NotificationViewModel
import com.proto.type.profile.ui.phone.changenumber.NewPhoneViewModel
import com.proto.type.profile.ui.phone.otp.OtpVerifyViewModel
import com.proto.type.profile.ui.privacy.addblockcontact.AddBlockContactViewModel
import com.proto.type.profile.ui.privacy.blockedcontact.BlockContactViewModel
import com.proto.type.profile.ui.sound.SoundViewModel
import com.proto.type.profile.ui.status.StatusViewModel
import com.proto.type.profile.ui.chatQId.ChatQIdViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {

    viewModel { AddBlockContactViewModel(get()) }

    viewModel { AppearanceViewModel(get()) }

    viewModel { BlockContactViewModel(get(), get()) }

    viewModel { ChatQIdViewModel(get()) }

    viewModel { CheckInboxViewModel(get()) }

    viewModel { DeleteAccountViewModel(get(), get()) }

    viewModel { EmailVerifyViewModel(get()) }

    viewModel { LanguageViewModel(get()) }

    viewModel { NewPhoneViewModel(get()) }

    viewModel { NotificationViewModel(get()) }

    viewModel { OtpVerifyViewModel(get()) }

    viewModel { ProfileTabViewModel(get(), get()) }

    viewModel { SoundViewModel(get()) }

    viewModel { StatusViewModel(get()) }

    viewModel { ViewContactViewModel(get()) }

    viewModel { WallpaperViewModel() }
}