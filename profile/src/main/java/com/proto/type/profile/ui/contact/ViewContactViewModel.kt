package com.proto.type.profile.ui.contact

import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import kotlinx.coroutines.launch

class ViewContactViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    fun loadContacts() {
        ioScope.launch {
            userRepo.loadChatQContacts()
        }
    }
}
