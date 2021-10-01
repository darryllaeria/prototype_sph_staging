package com.proto.type.base.repository.chat

import android.net.Uri
import com.proto.type.base.data.database.dao.ChatDao
import com.proto.type.base.data.database.entity.ChatEntityKey
import com.proto.type.base.data.mapper.ChatMapper
import com.proto.type.base.data.model.*
import com.proto.type.base.data.remote.ChatService
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.DateTimeUtils
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

class ChatRepositoryImpl(
    private val chatService: ChatService,
//    private val firebase: FirebaseService,
    private val chatDao: ChatDao
): IChatRepository {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatRepositoryImpl::class.java.simpleName
    }

    // MARK: - Database Functions
    override fun deleteChat(chatId: String) {
        chatDao.deleteChat(chatId)
    }
    override suspend fun findChat(chatId: String): ChatModel? {
        return runBlocking {
            chatDao.findChat(chatId)
        }
    }

    override fun findPrivateChats(currentUserId: String, userId: String): List<ChatModel> {
        return runBlocking {
            chatDao.findChats(ChatEntityKey.category, "private").filter { it.participant_ids?.containsAll(listOf(currentUserId, userId)) ?: false }
        }
    }

    override fun getLocalChats(): List<ChatModel> {
        return runBlocking {
            chatDao.getAllChats()
        }
    }

    override fun saveChatJson(jsonObject: JSONObject): Boolean {
        return try {
            runBlocking {
                chatDao.saveJSONData(jsonObject) != null
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    override fun saveChatJsons(jsonArray: JSONArray): Boolean {
        return try {
            runBlocking {
                val ids = chatDao.saveJSONDatas(jsonArray)
                val dataSize = jsonArray.length()
                dataSize != 0 && ids.size == dataSize
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    /**
     * @Detail Store chat list from API to repository
     */
    override fun storeChat(chat: ChatModel) {
        chatDao.insertOrUpdateEntity(ChatMapper.toEntity(chat))
    }

    /**
     * @Detail Store chat list from API to repository
     */
    override fun storeChats(chats: List<ChatModel>) {
        runBlocking {
            chatDao.insertOrUpdateEntities(ChatMapper.toEntities(chats))
        }
    }

    // MARK: - Firebase Functions
//    override suspend fun uploadChatAvatarToFirebase(uri: Uri, chatId: String, name: String, fileType: FileDataType): Pair<String?, String?> {
//        return firebase.uploadFile(uri,name, fileType)
//    }

    // MARK: - Service Functions
    /**
     * @Detail Normal user can add their friends to a chat room
     */
    override suspend fun addParticipants(chatId: String, userIds: List<String>): Boolean {
        return chatService.addParticipants(chatId, ChatManageUsersRequest(userIds)).isSuccessful
    }

    /**
     * @Detail Clear messages history of a chat
     */
    override suspend fun clearChatHistory(chatId: String): Boolean {
        return chatService.clearChatHistory(chatId, ClearChatHistoryRequest(DateTimeUtils.getCurrentTsForIos())).isSuccessful
    }

    /**
     * @Detail Create a new chat room
     */
    override suspend fun createNewChat(adminId: String, category: String, chatId: String, name: String, participantIds: List<String>, avatar: Map<String, String>?): String {
        val jsonString = chatService.createNewChat(CreateChatRequest(listOf(adminId), category, chatId, name, participantIds, avatar)).string().trim()
        val jsonObject = JSONObject(jsonString)
        val id = (jsonObject.opt(ChatJsonKey.id) as? String) ?: ""
        return if (saveChatJson(jsonObject)) id else ""
    }

    /**
     * @Detail Remove users from room or leave room API
     */
    override suspend fun leaveChat(chatId: String, userIds: List<String>): Boolean {
        return chatService.leaveChat(chatId, ChatLeaveRequest(userIds)).isSuccessful
    }

    /**
     * @Detail Get chat room details from API
     */
    override suspend fun loadChat(chatId: String): Boolean {
        val jsonString = chatService.getChatDetail(chatId).string().trim()
        return saveChatJson(JSONObject(jsonString))
    }

    /**
     * @Detail Get chat list data from API
     */
    override suspend fun loadChats(): Boolean {
        val jsonString = chatService.getRemoteChats().string().trim()
        val jsonArray = JSONArray(jsonString)
        return saveChatJsons(jsonArray) // Save chats info to Realm
    }

    override suspend fun markReadChat(chatId: String, timeStamp: Double): Boolean {
        return chatService.markReadChat(chatId, ChatMarkReadRequest(timeStamp)).isSuccessful
    }

    /**
     * @Detail Promote/demote admin role of a user in a chat room
     */
    override suspend fun promoteDemoteUser(chatId: String, userId: String, isAdmin: Boolean): Boolean {
        return chatService.promoteDemoteUser(chatId, mapOf("action" to if (isAdmin) "demote" else "promote"), ChatPromoteDemoteUserRequest(userId)).isSuccessful
    }

    /**
     * @Detail UserModel with admin role can remove participants (not admin) from a chat room
     */
    override suspend fun removeParticipants(chatId: String, userIds: List<String>): Boolean {
        return chatService.removeParticipants(chatId, ChatManageUsersRequest(userIds)).isSuccessful
    }

    /**
     * @Detail Update room details (avatar)
     */
    override suspend fun updateChatAvatar(chatId: String, avatarId: String, avatarUrl: String): Boolean {
        return chatService.updateChatAvatar(chatId, ChatUpdateAvatarRequest(mapOf("id" to avatarId, "url" to avatarUrl))).isSuccessful
    }

    /**
     * @Detail Update chat details (is_pinned)
     */
    override suspend fun updateChatIsPinned(chatId: String, isPinned: Boolean): Boolean {
        val result = chatService.updateChatIsPinned(chatId, ChatUpdateIsPinnedRequest(isPinned)).isSuccessful
        if (result) {
            val chat = findChat(chatId)
            return if (chat != null) {
                chat.is_pinned = isPinned
                storeChat(chat)
                true
            } else {
                false
            }
        }
        return false
    }

    /**
     * @Detail Update chat details (mute)
     */
    override suspend fun updateChatMuteNotification(chatId: String, muteNotification: Boolean): Boolean {
        val result = chatService.updateRoomMuteNotification(chatId, ChatUpdateMuteRequest(muteNotification)).isSuccessful
        if (result) {
            val chat = findChat(chatId)
            return if (chat != null) {
                chat.mute_notification = muteNotification
                storeChat(chat)
                true
            } else {
                false
            }
        }
        return false
    }

    /**
     * @Detail Update room details (name)
     */
    override suspend fun updateChatName(chatId: String, name: String) {
        chatService.updateChatName(chatId, ChatUpdateNameRequest(name))
    }
}