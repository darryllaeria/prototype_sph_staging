package com.proto.type.base.domain

import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseUseCase
import com.proto.type.base.repository.user.IUserRepository

class CheckUserAuthorized(private val userRepo: IUserRepository): BaseUseCase<Any, Boolean>() {

    override suspend fun execute(param: Any, callback: SuccessCallback) {
        callback.invoke(userRepo.isLoggedIn())
    }
}