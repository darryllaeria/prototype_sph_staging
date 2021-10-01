package com.proto.type.base.domain

import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.repository.user.IUserRepository

class SyncContact(private val userRepo: IUserRepository): BaseUseCase<Any, Boolean>() {

    override suspend fun execute(param: Any, callback: (Boolean) -> Unit) {
        val localContacts = userRepo.loadPhoneContacts()
        val users = userRepo.getLocalUsers()
        val localContact = localContacts.filter { contact ->
            users.find { it.phone_number == contact.phone_number } == null
        }
        callback.invoke(userRepo.syncContacts(localContact))
    }
}