package com.proto.type.main.ui.signup.email

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.data.model.SignUpForm
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class SignUpEmailViewModel(private val localRepo: ILocalRepository,
                           private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG = SignUpEmailViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var isUserExisted = MutableLiveData<UIState>()
    var signUpForm = MutableLiveData<SignUpForm>()

    // MARK: - Public Functions
    fun checkUserEmail(email: String) {
        isUserExisted.postValue(UIState.LOADING())
        ioScope.launch {
            try {
                isUserExisted.postValue(UIState.FINISHED(userRepo.isFirebaseUserExist(email)))
            } catch (e: Exception) {
                AppLog.d(TAG, "Check user email failed with exception: $e")
                isUserExisted.postValue(UIState.FAILED())
            }
        }
    }

    fun loadSaveForm() {
        signUpForm.postValue(localRepo.getSignupForm())
    }

    fun storeEmail(email: String) {
        localRepo.storeEmail(email)
    }
}