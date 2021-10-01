package com.proto.type.base.domain

import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.user.IUserRepository

class GetChatQUser(private val userRepo: IUserRepository): BaseUseCase<Any, List<UserModel>>() {

    override suspend fun execute(param: Any, callback: (List<UserModel>) -> Unit) {
        callback.invoke(userRepo.getInContactLocalUsers())
    }

}