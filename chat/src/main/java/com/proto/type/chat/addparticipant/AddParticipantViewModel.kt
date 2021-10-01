package com.proto.type.chat.addparticipant

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.getAllLocalContacts
import com.proto.type.base.extension.getDeviceID
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddParticipantViewModel(private val context: Context,
                              private val chatRepo: IChatRepository,
                              private val userRepo: IUserRepository) : BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = AddParticipantViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var selectedUsers = MutableLiveData<UIState>()
    var users = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun addChatMembers(roomId: String, userIds: List<String>, callback: SuccessCallback) {
        ioScope.launch {
            try {
                val success = chatRepo.addParticipants(roomId, userIds)
                uiScope.launch { callback.invoke(success) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Add chat members failed with exception: $e")
                uiScope.launch { callback.invoke(false) }
            }
        }
    }

    fun getAllUsers(existedUserIds: ArrayList<String> = arrayListOf()) {
        users.postValue(UIState.LOADING())
        ioScope.launch {
            delay(500)
            val contacts = context.getAllLocalContacts()
            try {
                userRepo.syncContacts(contacts)
                val success = userRepo.loadContacts(context.getDeviceID())
                if (success) {
                    users.postValue(UIState.FINISHED(userRepo.getLocalUsers().filter { !existedUserIds.contains(it.id) }.sortedBy { it.displayingName() }))
                } else {
                    AppLog.d(TAG, "Get all users failed due to server error.")
                    users.postValue(UIState.FINISHED(userRepo.getInContactLocalUsers()))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get all users failed due with exception: $e")
                users.postValue(UIState.FINISHED(userRepo.getInContactLocalUsers()))
            }
        }
    }

    fun getSelectedListIds(): ArrayList<String> {
        val participantsIds = arrayListOf<String>()
        ((selectedUsers.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()?.forEach {
            participantsIds.add(it.id)
        }
        return participantsIds
    }

    fun resetUserCheckbox(user: UserModel) {
        ((users.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()?.let {
            it.find { tempUser -> tempUser.id == user.id }?.is_selected = false
            users.postValue(UIState.FINISHED(it))
        }
    }

    fun searchUserList(name: String?) {
        uiScope.launch {
            try {
                if (name.isNullOrEmpty()) {
                    users.postValue(users.value)
                } else {
                    val currentData = ((users.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()
                    users.postValue(
                        UIState.FINISHED(
                            currentData?.filter {
                                it.displayingName().contains(name, true)
                            }?.toMutableList()
                        )
                    )
                }
            } catch (e: Exception) {
                users.postValue(UIState.FAILED())
            }
        }
    }

    fun setSelectedUser(user: UserModel) {
        ioScope.launch {
            try {
                val selectedUsersList = ((selectedUsers.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()?.toMutableList() ?: mutableListOf()
                if (selectedUsersList.contains(user)) {
                    selectedUsersList.remove(user)
                } else {
                    selectedUsersList.add(user)
                }
                selectedUsers.postValue(UIState.FINISHED(selectedUsersList))
            } catch (e: Exception) {

            }
        }
    }
}