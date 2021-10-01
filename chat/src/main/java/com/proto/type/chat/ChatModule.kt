package com.proto.type.chat

import com.proto.type.chat.addparticipant.AddParticipantViewModel
import com.proto.type.chat.chat.ChatViewModel
import com.proto.type.chat.creategroup.CreateGroupViewModel
import com.proto.type.chat.forward.ForwardChatViewModel
import com.proto.type.chat.settings.ChatSettingsViewModel
import com.proto.type.chat.inbox.InboxViewModel
import com.proto.type.chat.newchat.NewChatViewModel
import com.proto.type.chat.participants.ParticipantsViewModel
import com.proto.type.chat.report.ReportViewModel
import com.proto.type.chat.search.SearchChatViewModel
import com.proto.type.chat.sharedmedia.SharedMediaViewModel
import com.proto.type.chat.userprofile.UserProfileViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    viewModel { ChatViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddParticipantViewModel(get(), get(), get()) }
    viewModel { CreateGroupViewModel(get(), get()) }
    viewModel { ChatSettingsViewModel(get(), get(), get(), get()) }
    viewModel { ForwardChatViewModel(get(), get()) }
    viewModel { InboxViewModel(get(), get(), get()) }
    viewModel { NewChatViewModel(get(), get(), get()) }
    viewModel { ParticipantsViewModel(get(), get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { SharedMediaViewModel(get()) }
    viewModel { UserProfileViewModel(get(), get(), get()) }
    viewModel { SearchChatViewModel(get(), get(), get(), get()) }
}