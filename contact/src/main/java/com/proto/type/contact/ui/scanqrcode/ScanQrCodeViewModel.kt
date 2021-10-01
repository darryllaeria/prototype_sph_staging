package com.proto.type.contact.ui.scanqrcode

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.repository.chat.IChatRepository
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.encryption.AESDecryption
import com.proto.type.base.data.encryption.Obfuscator
import com.proto.type.base.utils.AppLog
import com.proto.type.contact.R
import kotlinx.coroutines.launch

class ScanQrCodeViewModel(
    private val userRepo: IUserRepository,
    private val chatRepo: IChatRepository
) : BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ScanQrCodeViewModel::class.java.simpleName
    }

    // MARK: - Public Variable
    var chat = MutableLiveData<UIState>()

    // MARK: - Public Function
    fun findPrivateChat(scannedText: String?) {
        try {
            val userId = AESDecryption.decryptWithAES(scannedText, Obfuscator.reveal())
            if (userId == null) {
                chat.postValue(UIState.FAILED())
            } else {
                chat.postValue(UIState.LOADING())

                // Get private room from realmDB, request it if cannot find
                ioScope.launch {
                    try {
                        val currentUserId = loadCurrentUserId()
                        val chatRooms = chatRepo.findPrivateChats(currentUserId!!, userId)
                        if (chatRooms.isNotEmpty()) {
                            chat.postValue(UIState.FINISHED(chatRooms.first()))
                        } else {
                            val chatId = chatRepo.createNewChat(
                                currentUserId,
                                Constants.CHAT_CATEGORY_PRIVATE,
                                "$currentUserId!!.$userId",
                                "",
                                listOf(currentUserId, userId),
                                null
                            )
                            val chatModel = chatRepo.findChat(chatId)
                            if (chatModel != null)
                                chat.postValue(UIState.FINISHED(chatModel))
                            else {
                                AppLog.d(TAG, "Create new chat failed due to realm finding or server error")
                                chat.postValue(UIState.FAILED(R.string.txt_unexpected_error))
                            }
                        }
                    } catch (e: Exception) {
                        AppLog.d(TAG, "Find private chat failed with exception: $e")
                        chat.postValue(UIState.FAILED(R.string.txt_unexpected_error))

                    }
                }
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Decrypt obfuscator failed with exception: $e")
            chat.postValue(UIState.FAILED(R.string.txt_unexpected_error))
        }
    }

    // MARK: - Public Function
    private fun loadCurrentUserId(): String? {
        val currentUser = userRepo.getCurrentLocalUser()
        return if (currentUser.id.isNotEmpty()) currentUser.id else null
    }
}