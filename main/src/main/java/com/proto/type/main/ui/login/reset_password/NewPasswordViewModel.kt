package com.proto.type.main.ui.login.reset_password

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.repository.user.IUserRepository

class NewPasswordViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    var isPasswordReset = MutableLiveData<UIState>()

    fun confirmPasswordReset(code: String, password: String) {
        userRepo.confirmPasswordReset(code, password) {
            if (it) {
                isPasswordReset.postValue(UIState.FINISHED(it))
            } else {
                isPasswordReset.postValue(UIState.FAILED())
            }
        }
    }


}