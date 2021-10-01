package com.proto.type.profile.ui.chatQId

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants.ErrorString.ERROR_NONE
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class ChatQIdViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatQIdViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var isUpdated = MutableLiveData<Boolean>()

    // MARK: - Private Variable
    private val currentUser = userRepo.getCurrentLocalUser()

    // MARK: - Public Function
    fun updateUserName(userName: String) {
        ioScope.launch {
            try {
                val errorMessage = userRepo.updateUsername(currentUser.id, userName)
                val success = errorMessage == ERROR_NONE
                isUpdated.postValue(success)
                if (!success)
                    AppLog.d(TAG, "Update ChatQId failed due to $errorMessage")
            } catch (e: Exception) {
                AppLog.d(TAG, "Update ChatQId failed with exception: $e")
                isUpdated.postValue(false)
            }
        }
    }
}