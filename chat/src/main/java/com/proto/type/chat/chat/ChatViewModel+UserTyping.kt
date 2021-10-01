package com.proto.type.chat.chat

// MARK: - ChatViewModel + User Typing
fun ChatViewModel.cleanupTypingManager() {
    typingUsersManager.invalidateTimers()
}

fun ChatViewModel.publishUsersTypingTopic(chatId: String) {
    currentUser.value?.let {
//        messageMQTTClient.publishTypingStatus(it, chatId)
    }
}

fun ChatViewModel.setupTypingManager() {
    typingUsersManager.didEndTypingCallback = {
        isTypingMessage.postValue("")
    }
    typingUsersManager.didModifyUsersCallback = {
        isTypingMessage.postValue(it)
    }
}

fun ChatViewModel.subscribeUsersTypingTopic(chatId: String) {
//    messageMQTTClient.subscribe(topic = MQTTUrls.messageRoomIsTyping(chatId), subscriber = this)
}

fun ChatViewModel.unsubscribeUsersTypingTopic(chatId: String) {
//    messageMQTTClient.unsubscribe(topic = MQTTUrls.messageRoomIsTyping(chatId))
}