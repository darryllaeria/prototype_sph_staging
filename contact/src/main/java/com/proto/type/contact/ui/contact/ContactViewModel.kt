package com.proto.type.contact.ui.contact

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.domain.FindPrivateChat
import com.proto.type.base.domain.GetLocalContact
import com.proto.type.base.domain.SyncContact
import com.proto.type.base.extension.getAllLocalContacts
import com.proto.type.base.extension.getDeviceID
import com.proto.type.base.manager.PrefsManager
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.*

class ContactViewModel(private val context: Context,
                       private val findPrivateChat: FindPrivateChat,
                       private val getLocalContact: GetLocalContact,
                       private val prefsManager: PrefsManager,
                       private val syncContact: SyncContact,
                       private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ContactViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var chat = MutableLiveData<UIState>()
    var chatQContacts = MutableLiveData<UIState>()
    var friendName = MutableLiveData<String>()
    var localContacts = MutableLiveData<UIState>()

    // MARK: - Public Functions
    fun enterChatRoom(friendId: String) {
        ioScope.launch {
            try {
                findPrivateChat.invoke(friendId) {
                    chat.postValue(UIState.FINISHED(it))
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Enter chat room failed with exception: $e")
            }
        }
    }

    fun getAllContacts() {
        chatQContacts.postValue(UIState.LOADING())
        ioScope.launch {
            val contacts = context.getAllLocalContacts()
            try {
                userRepo.syncContacts(contacts)
                val success = userRepo.loadContacts(context.getDeviceID())
                if (success) {
                    val loadedUsers = userRepo.getLocalUsers().sortedBy { it.displayingName() }
                    chatQContacts.postValue(UIState.FINISHED(loadedUsers))
                    ioScope.launch {
                        loadedUsers.apply {
                            getLocalContact.invoke(Any()) {
                                localContacts.postValue(UIState.FINISHED(it))
                            }
                        }
                    }
                } else {
                    AppLog.d(TAG, "Get all users failed due to server error.")
                    loadInContactLocalUsers()
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get all contacts failed with exception: $e")
                loadInContactLocalUsers()
            }
        }
    }

    fun getPermissionMessageShow(): Boolean {
        return prefsManager.getBool(Constants.CONTACT_PERMISSION_SHOW, true)
    }

    fun setPermissionMessageShow(show: Boolean) {
        prefsManager.putBool(Constants.CONTACT_PERMISSION_SHOW, show)
    }

    fun setFriendName(name: String) {
        friendName.postValue(name)
    }

    fun syncContacts() {
        ioScope.launch {
            syncContact.invoke(Any()) {
                getAllContacts()
            }
        }
    }

    // MARK: - Private Functions
    private fun loadInContactLocalUsers() {
        val inContactLocalUsers = userRepo.getInContactLocalUsers()
        chatQContacts.postValue(UIState.FINISHED(inContactLocalUsers))
        localContacts.postValue(UIState.FINISHED(inContactLocalUsers))
    }
}