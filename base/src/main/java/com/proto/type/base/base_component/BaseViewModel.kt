package com.proto.type.base.base_component

import androidx.lifecycle.ViewModel
import com.proto.type.base.Constants.DEFAULT_MESSAGE_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

sealed class UIState {
    object INIT: UIState()
    object DONE: UIState()
    data class LOADING(val messageId: Int = DEFAULT_MESSAGE_ID): UIState()
    data class FINISHED<T>(val data: T?, val messageId: Int = DEFAULT_MESSAGE_ID): UIState()
    data class FAILED(val messageId: Int = DEFAULT_MESSAGE_ID, val tempData: Any? = null): UIState()
}

abstract class BaseViewModel: ViewModel() {

    private val job = Job()
    private val ioContext = Dispatchers.IO + job
    private val uiContext = Dispatchers.Main + job

    val ioScope = CoroutineScope(ioContext)
    val uiScope = CoroutineScope(uiContext)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}