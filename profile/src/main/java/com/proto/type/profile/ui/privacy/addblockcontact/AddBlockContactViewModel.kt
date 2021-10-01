package com.proto.type.profile.ui.privacy.addblockcontact

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.AppLog
import com.proto.type.profile.R
import kotlinx.coroutines.launch

class AddBlockContactViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = AddBlockContactViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var isBlocked = MutableLiveData<UIState>()
    var selectedList = mutableListOf<UserModel>()
    var userList = MutableLiveData<List<UserModel>>()

    // MARK: - Public Functions
    fun blockUser(userId: String) {
        isBlocked.postValue(UIState.LOADING())
        try {
            ioScope.launch {
                isBlocked.postValue(UIState.FINISHED(userRepo.blockUser(userId)))
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Block user failed with exception: $e")
            isBlocked.postValue(UIState.FAILED(R.string.txt_unexpected_error))
        }
    }

    fun getAllChatQUsersInContact() {
        userList.postValue(userRepo.getInContactLocalUsers())
    }
}