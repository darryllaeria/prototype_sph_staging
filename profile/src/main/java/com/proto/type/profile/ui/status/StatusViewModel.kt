package com.proto.type.profile.ui.status

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class StatusViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = StatusViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var isUpdated = MutableLiveData<Boolean>()

    // MARK: - Private Variable
    private val currentUser = userRepo.getCurrentLocalUser()

    // MARK: - Public Function
    fun updateStatus(status: String) {
        ioScope.launch {
            try {
                val success = userRepo.updateBio(currentUser.id, status)
                isUpdated.postValue(success)
                if (!success)
                    AppLog.d(TAG, "Update user bio failed due to server error")
            } catch (e: Exception) {
                AppLog.d(TAG, "Update user bio failed with exception: $e")
                isUpdated.postValue(false)
            }
        }
    }
}