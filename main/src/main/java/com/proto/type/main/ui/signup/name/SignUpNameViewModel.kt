package com.proto.type.main.ui.signup.name

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.data.model.SignUpForm
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.base_component.BaseViewModel

/**
 * @Details ViewModel for SignUp by name fragment
 */

class SignUpNameViewModel(private val localRepo: ILocalRepository): BaseViewModel() {

    var signUpForm = MutableLiveData<SignUpForm>()

    fun storeUser(firstName: String, lastName: String) {
        localRepo.storeFullName(firstName, lastName)
    }

    fun loadSaveForm() {
        signUpForm.postValue(localRepo.getSignupForm())
    }

}