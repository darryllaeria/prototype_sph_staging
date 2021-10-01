package com.proto.type.home.ui.login.forgot_password

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    var isPasswordReset = MutableLiveData<UIState>()

    fun requestPasswordReset(email: String) {
        isPasswordReset.postValue(UIState.LOADING())
        ioScope.launch {
            isPasswordReset.postValue(UIState.FINISHED(userRepo.resetFirebasePassword(email)))
        }
    }
}