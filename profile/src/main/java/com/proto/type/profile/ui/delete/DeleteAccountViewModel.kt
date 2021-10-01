package com.proto.type.profile.ui.delete

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import com.proto.type.profile.R
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DeleteAccountViewModel(private val userRepo: IUserRepository,
                             private val deviceRepo: IDeviceRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = DeleteAccountViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var countryCode = MutableLiveData<String>()
    var deletingUser = MutableLiveData<UIState>()
    var isDeleted = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun checkEnterPhone(phoneNumber: String) {
        val user = userRepo.getCurrentLocalUser()
        if (user.isIdNotEmpty()) {
            if (user.phone_number == phoneNumber) {
                deletingUser.postValue(UIState.FINISHED(user))
            } else {
                deletingUser.postValue(UIState.FAILED(R.string.txt_err_wrong_number, user))
            }
        } else {
            // TODO("Handle when there is no current user")
        }
    }

    fun deleteAccount() {
        ioScope.launch {
            try {
                val accResult = async { userRepo.deleteChatQAccount() }
                val devResult = async { deviceRepo.deregisterDevice() }
                if (accResult.isCompleted && devResult.isCompleted) {
                    isDeleted.postValue(UIState.FINISHED(true))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Delete account failed with exception: $e")
                isDeleted.postValue(UIState.FAILED(R.string.txt_err_delete_account))
            }
        }
    }

    fun getCountryCode() {
        val code = deviceRepo.getCountryCode()
        if (code.isEmpty()) {
            countryCode.postValue(Constants.Language.DEFAULT_COUNTRY_CODE)
        } else {
            countryCode.postValue(code)
        }
    }
}