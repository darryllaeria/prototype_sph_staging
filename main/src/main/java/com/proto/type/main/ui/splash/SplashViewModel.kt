package com.proto.type.main.ui.splash

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.domain.CheckUserAuthorized
import kotlinx.coroutines.launch

class SplashViewModel(
//    private val chatMessagesManager: ChatMessagesManager,
    private val checkUserAuthorized: CheckUserAuthorized,
//    private val firebaseService: FirebaseService
): BaseViewModel() {

    // MARK: - Public Variable
    var isUserAuthorized = MutableLiveData<Boolean>()

    // MARK: - Public Function
    fun checkAuthorization() {
        ioScope.launch {
            checkUserAuthorized.invoke(Any()) {
                if (it) {
//                    chatMessagesManager.subscribe()
//                    firebaseService.checkForMarketDataUpdate()
                    Thread.sleep(500)
                }
                isUserAuthorized.postValue(it)
            }
        }
    }
}