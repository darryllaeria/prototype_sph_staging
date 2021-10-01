package com.proto.type.chat.sharedmedia

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.Constants.INITIAL_MORE_DATA
import com.proto.type.base.Constants.INITIAL_MORE_DATA_AND_MERGE
import com.proto.type.base.Constants.INITIAL_NO_MORE_DATA
import com.proto.type.base.Constants.LOADMORE_MORE_DATA
import com.proto.type.base.Constants.LOADMORE_NO_MORE_DATA
import com.proto.type.base.Constants.UPDATE_DATA_ITEM
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.MessageLinkItemModel
import com.proto.type.base.data.model.MessageMediaItemModel
import com.proto.type.base.repository.message.IMessageRepository
import com.proto.type.base.utils.AppLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import net.vrgsoft.library.LinkCrawler
import java.lang.Exception

class SharedMediaViewModel(private val messageRepo: IMessageRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = SharedMediaViewModel::class.java.simpleName
    }

    // MARK: - Public Variables
    var linksList = MutableLiveData<UIState>()
    var mediaList = MutableLiveData<UIState>()

    // MARK: Private Constants
    private val MAX_CONCURRENT_WORKING_SUBS = 3
    private val loadMetadataQueues = mutableMapOf<Int, MessageLinkItemModel>()
    private val workingSubscriptions = mutableMapOf<Int, Disposable>()

    // MARK: - Public Functions
    fun getAllMessageLinkItemsFromRealm(roomId: String) {
        uiScope.launch {
            try {
                linksList.postValue(UIState.FINISHED(messageRepo.findLocalMessageLinkItems(roomId).toMutableList()))
            } catch (e: Exception) {
                AppLog.d(TAG, "Get all messageLinkItems failed with exception: $e")
                linksList.postValue(UIState.FAILED())
            }
        }
    }

    fun getAllMessageMediaItemsFromRealm(roomId: String) {
        uiScope.launch {
            try {
                mediaList.postValue(UIState.FINISHED(messageRepo.findLocalMessageMediaItems(roomId).toMutableList()))
            } catch (e: Exception) {
                AppLog.d(TAG, "Get all messageMediaItems failed with exception: $e")
                mediaList.postValue(UIState.FAILED())
            }
        }
    }

    fun getInitialMessageLinkItems(roomId: String) {
        ioScope.launch {
            try {
                val response = messageRepo.loadMessageLinkItems(roomId)
                if (response.isSuccessful) {
                    val items = response.body()?.items?.sortedByDescending { it.sent_ts }?.toMutableList() ?: mutableListOf()
                    for (item in items) { item.room_id = roomId }
                    if (items.size < Constants.Message.ITEM_PAGE_SIZE) {
                        uiScope.launch { linksList.postValue(UIState.FINISHED(items, INITIAL_NO_MORE_DATA)) }
                    } else {
                        getCurrentLinks()?.let { linkItems ->
                            if (linkItems.isEmpty()) { return@let linksList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }

                            // Check if new data from server overlaps with Realm data, if yes merge the two data chunk into one, otherwise discard Realm data due to its obsolete and use new media data from API .
                            if (linkItems.any { mediaItem -> items.firstOrNull { it.room_id == mediaItem.room_id && it.message_id == mediaItem.message_id } != null }) {
                                items.forEachIndexed { index, item ->
                                    linkItems.firstOrNull { it.room_id == item.room_id && it.message_id == item.message_id }?.let {
                                        if (it.title != null) {
                                            linkItems.remove(it)
                                            it.link = item.link
                                            it.sent_ts = item.sent_ts
                                            items[index] = it
                                        }
                                    }
                                }
                                items.addAll(linkItems)
                                items.sortByDescending { it.sent_ts }
                                uiScope.launch { linksList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA_AND_MERGE)) }
                            } else {
                                messageRepo.deleteLocalMessageLinkItems(roomId)
                                uiScope.launch { linksList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }
                            }
                        } ?: run {
                            uiScope.launch { linksList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }
                        }
                    }
                    messageRepo.storeMessageLinkItems(items)
                } else {
                    uiScope.launch { linksList.postValue(UIState.FAILED(INITIAL_MORE_DATA, getCurrentLinks())) }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get message link items failed with exception: $e")
                uiScope.launch { linksList.postValue(UIState.FAILED(INITIAL_MORE_DATA, getCurrentLinks())) }
            }
        }
    }

    fun getInitialMessageMediaItems(roomId: String) {
        ioScope.launch {
            try {
                val response = messageRepo.loadMessageMediaItems(roomId)
                if (response.isSuccessful) {
                    var items = response.body()?.items?.sortedByDescending { it.sent_ts }?.toMutableList() ?: mutableListOf()
                    for (item in items) { item.room_id = roomId }
                    if (items.size < Constants.Message.ITEM_PAGE_SIZE) {
                        uiScope.launch { mediaList.postValue(UIState.FINISHED(items, INITIAL_NO_MORE_DATA)) }
                    } else {
                        getCurrentMedia()?.let { mediaItems ->
                            if (mediaItems.isEmpty()) { return@let mediaList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }

                            // Check if new data from server overlaps with Realm data, if yes merge the two data chunk into , otherwise discard Realm data due to its obsolete and use new media data from API .
                            if (mediaItems.any { mediaItem -> items.firstOrNull { it.room_id == mediaItem.room_id && it.message_id == mediaItem.message_id } != null }) {
                                items.addAll(mediaItems)
                                items = items.distinctBy { listOf(it.room_id, it.message_id) }.sortedByDescending { it.sent_ts }.toMutableList()
                                uiScope.launch { mediaList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA_AND_MERGE)) }
                            } else {
                                messageRepo.deleteLocalMessageMediaItems(roomId)
                                uiScope.launch { mediaList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }
                            }
                        } ?: run {
                            uiScope.launch { mediaList.postValue(UIState.FINISHED(items, INITIAL_MORE_DATA)) }
                        }
                    }
                    messageRepo.storeMessageMediaItems(items)
                } else {
                    uiScope.launch { mediaList.postValue(UIState.FAILED(INITIAL_MORE_DATA, getCurrentMedia())) }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get message media items failed with exception: $e")
                uiScope.launch { mediaList.postValue(UIState.FAILED(INITIAL_MORE_DATA, getCurrentMedia())) }
            }
        }
    }

    fun loadMetadata(linkItem: MessageLinkItemModel, position: Int) {
        loadMetadataQueues[position] = linkItem
        triggerLoadLinkMetadataIfNeeded()
    }

    fun loadMoreMessageLinkItems(roomId: String) {
        ioScope.launch {
            try {
                val currentItems = getCurrentLinks() ?: mutableListOf()
                val response = messageRepo.loadMessageLinkItems(roomId, endTimestamp = getCurrentLinks()?.lastOrNull()?.sent_ts.toString())
                if (response.isSuccessful) {
                    response.body()?.items?.let { newList ->
                        currentItems.addAll(newList)
                        for (item in newList) { item.room_id = roomId }
                        messageRepo.storeMessageLinkItems(newList)
                        uiScope.launch { linksList.postValue(UIState.FINISHED(currentItems.sortedByDescending { it.sent_ts }.toMutableList(), if (newList.size >= Constants.Message.ITEM_PAGE_SIZE) LOADMORE_MORE_DATA else LOADMORE_NO_MORE_DATA)) }
                    }
                } else {
                    uiScope.launch { linksList.postValue(UIState.FAILED(LOADMORE_MORE_DATA, currentItems)) }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get more message link items failed with exception: $e")
                uiScope.launch { linksList.postValue(UIState.FAILED(LOADMORE_MORE_DATA, getCurrentLinks())) }
            }
        }
    }

    fun loadMoreMessageMediaItems(roomId: String) {
        ioScope.launch {
            try {
                val currentItems = getCurrentMedia() ?: mutableListOf()
                val response = messageRepo.loadMessageMediaItems(roomId, endTimestamp = currentItems.lastOrNull()?.sent_ts.toString())
                if (response.isSuccessful) {
                    response.body()?.items?.let { newList ->

                        currentItems.addAll(newList)
                        for (item in newList) { item.room_id = roomId }
                        messageRepo.storeMessageMediaItems(newList)
                        uiScope.launch { mediaList.postValue(UIState.FINISHED(currentItems.sortedByDescending { it.sent_ts }.toMutableList(), if (newList.size >= Constants.Message.ITEM_PAGE_SIZE) LOADMORE_MORE_DATA else LOADMORE_NO_MORE_DATA)) }
                    }
                } else {
                    uiScope.launch { mediaList.postValue(UIState.FAILED(LOADMORE_MORE_DATA, currentItems)) }
                }
            } catch (e: Exception) {
                AppLog.d(TAG, "Get more message media items failed with exception: $e")
                uiScope.launch { mediaList.postValue(UIState.FAILED(LOADMORE_MORE_DATA, getCurrentMedia())) }
            }
        }
    }

    // MARK: - Private Functions
    private fun cleanupSubscription(key: Int, shouldRemoveKey: Boolean = true) {
        workingSubscriptions[key]?.dispose()
        if (shouldRemoveKey) workingSubscriptions.remove(key)
    }

    private fun handleLoadUrlMetadataResponse(key: Int) {
        cleanupSubscription(key)
        loadMetadataQueues.remove(key)
        triggerLoadLinkMetadataIfNeeded()
    }

    private fun getCurrentLinks(): MutableList<MessageLinkItemModel>? {
        return ((linksList.value as? UIState.FINISHED<*>)?.data as? List<*>)?.filterIsInstance<MessageLinkItemModel>()?.toMutableList()
            ?: ((linksList.value as? UIState.FAILED)?.tempData as? List<*>)?.filterIsInstance<MessageLinkItemModel>()?.toMutableList()
    }

    private fun getCurrentMedia(): MutableList<MessageMediaItemModel>? {
        return ((mediaList.value as? UIState.FINISHED<*>)?.data as? List<*>)?.filterIsInstance<MessageMediaItemModel>()?.toMutableList()
            ?: ((mediaList.value as? UIState.FAILED)?.tempData as? List<*>)?.filterIsInstance<MessageMediaItemModel>()?.toMutableList()
    }

    private fun triggerLoadLinkMetadataIfNeeded() {
        ioScope.launch {
            if (workingSubscriptions.keys.size >= MAX_CONCURRENT_WORKING_SUBS) return@launch
            loadMetadataQueues.keys.firstOrNull { !workingSubscriptions.keys.contains(it) }?.let { loadingKey ->
                loadMetadataQueues[loadingKey]?.let {
                    cleanupSubscription(loadingKey, false)
                    workingSubscriptions[loadingKey] = CompositeDisposable()
                    try {
                        workingSubscriptions[loadingKey] = LinkCrawler().parseUrl(it.link).subscribe { response ->
                            val result = response.result
                            val title = result?.title ?: ""
                            it.main_image_url = result?.images?.firstOrNull()
                            it.title = if (title.isNotEmpty()) title else it.link
                            updateMessageLinkItemToRealm(it)
                            handleLoadUrlMetadataResponse(loadingKey)
                        }
                    } catch (e: Exception) {
                        AppLog.d(TAG, "Parse the provided url failed with exception: $e")
                        handleLoadUrlMetadataResponse(loadingKey)
                    }
                } ?: run {
                    handleLoadUrlMetadataResponse(loadingKey)
                }
            }
        }
    }

    private fun updateMessageLinkItemToRealm(linkItem: MessageLinkItemModel) {
        val currentItems = getCurrentLinks() ?: mutableListOf()
        currentItems.firstOrNull { it.room_id == linkItem.room_id && it.message_id == linkItem.message_id }?.let {
            currentItems[currentItems.indexOf(it)] = linkItem
        }
        linksList.postValue(UIState.FINISHED(currentItems, UPDATE_DATA_ITEM))
        uiScope.launch { messageRepo.storeMessageLinkItems(mutableListOf(linkItem)) }
    }
}