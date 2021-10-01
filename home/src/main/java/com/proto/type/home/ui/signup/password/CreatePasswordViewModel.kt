package com.proto.type.home.ui.signup.password

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState


import kotlinx.coroutines.launch

class CreatePasswordViewModel(private val localRepo: ILocalRepository,
                              private val userRepo: IUserRepository): BaseViewModel() {

    var isUserCreated = MutableLiveData<UIState>()

    fun storePassword(password: String) {
        isUserCreated.postValue(UIState.LOADING())
        localRepo.storePassword(password)

//        if (userRepo.getFirebaseUser() != null) {
//            isUserCreated.postValue(UIState.FINISHED(true))
//        } else {
//            val form = localRepo.getSignupForm()
//            ioScope.launch {
//                val result = userRepo.createFirebaseUser(form.email, form.password)
//                isUserCreated.postValue(UIState.FINISHED(result))
//            }
//        }
    }
}