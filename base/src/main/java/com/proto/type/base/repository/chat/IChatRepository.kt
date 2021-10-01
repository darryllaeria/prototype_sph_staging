package com.proto.type.base.repository.chat

import com.proto.type.base.data.model.ChatModel
import org.json.JSONArray
import org.json.JSONObject

interface IChatRepository {

    // MARK: - Database Functions
    fun deleteChat(chatId: String)

    suspend fun findChat(chatId: String): ChatModel?

    fun findPrivateChats(currentUserId: String, userId: String): List<ChatModel>

    fun getLocalChats(): List<ChatModel>

    fun saveChatJson(jsonObject: JSONObject): Boolean

    fun saveChatJsons(jsonArray: JSONArray): Boolean

    fun storeChat(chat: ChatModel)

    fun storeChats(chats: List<ChatModel>)

    // MARK: - Firebase Functions
//    suspend fun uploadChatAvatarToFirebase(uri: Uri, chatId: String, name: String, fileType: FileDataType): Pair<String?, String?>

    // MARK: - Service Functions
    suspend fun addParticipants(chatId: String, userIds: List<String>): Boolean

    suspend fun clearChatHistory(chatId: String): Boolean

    suspend fun createNewChat(adminId: String, category: String, chatId: String, name: String, participantIds: List<String>, avatar: Map<String, String>?): String

    suspend fun loadChat(chatId: String): Boolean

    suspend fun loadChats(): Boolean

    suspend fun leaveChat(chatId: String, userIds: List<String>): Boolean

    suspend fun markReadChat(chatId: String, timeStamp: Double): Boolean

    suspend fun promoteDemoteUser(chatId: String, userId: String, isAdmin: Boolean): Boolean

    suspend fun removeParticipants(chatId: String, userIds: List<String>): Boolean

    suspend fun updateChatAvatar(chatId: String, avatarId: String, avatarUrl: String): Boolean

    suspend fun updateChatIsPinned(chatId: String, isPinned: Boolean = false): Boolean

    suspend fun updateChatMuteNotification(chatId: String, muteNotification: Boolean = false): Boolean

    suspend fun updateChatName(chatId: String, name: String)
}