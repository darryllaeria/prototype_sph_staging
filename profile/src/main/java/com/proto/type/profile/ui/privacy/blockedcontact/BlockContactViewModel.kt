package com.proto.type.profile.ui.privacy.blockedcontact

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch
import java.lang.Exception

class BlockContactViewModel(private val userRepo: IUserRepository,
                            private val deviceRepo: IDeviceRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = BlockContactViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var blockUsersData = MutableLiveData<List<UserModel>>()

    // MARK: - Public Function
    fun loadBlockedContacts() {
        ioScope.launch {
            try {
                val success = userRepo.loadCurrentUserBlockingData()
                if (success) {
                    val myProfile = userRepo.getCurrentLocalUser()
                    val deviceId = deviceRepo.getDeviceID()
                    if (userRepo.loadUsers(listOf(myProfile.phone_number), deviceId, myProfile.blockee_ids, null))
                        blockUsersData.postValue(userRepo.findLocalUsers(myProfile.blockee_ids.toTypedArray())?.toMutableList() ?: mutableListOf())
                    else {
                        AppLog.d(TAG, "Load block users failed due to server error.")
                        blockUsersData.postValue(emptyList())
                    }
                } else {
                    AppLog.d(TAG, "Get block data failed due to server error.")
                    blockUsersData.postValue(emptyList())
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get block data failed with exception: $e")
                blockUsersData.postValue(emptyList())
            }
        }
    }
}