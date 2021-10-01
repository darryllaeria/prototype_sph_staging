package com.proto.type.base.domain

import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.ContactModel
import com.proto.type.base.repository.user.IUserRepository
import java.util.*

class GetLocalContact(private val userRepo: IUserRepository): BaseUseCase<Any, List<ContactModel>>() {

    override suspend fun execute(param: Any, callback: (List<ContactModel>) -> Unit) {
        val local = userRepo.loadPhoneContacts()
        val chatQs = userRepo.getInContactLocalUsers()
        val result = local.filter { contact ->
            chatQs.find { user -> user.phone_number.toCharArray().filter { it.isLetterOrDigit() }.joinToString(separator = "") == contact.phone_number?.toCharArray()?.filter { it.isLetterOrDigit() }?.joinToString(separator = "") } == null
        }.toMutableList()
        result.sortWith(Comparator { userA, userB ->
            userA.local_name!!.compareTo(userB.local_name!!, true)
        })
        callback.invoke(result)
    }
}