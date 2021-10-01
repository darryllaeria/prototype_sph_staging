package com.proto.type.base.data.mapper

import com.proto.type.base.data.database.dao.MessageDao
import com.proto.type.base.extension.convertToList
import com.proto.type.base.extension.convertToRealmList
import com.proto.type.base.data.database.entity.ChatEntity
import com.proto.type.base.data.model.ChatModel
import io.realm.RealmList

object ChatMapper {

    // MARK: - Private Constant
    private val messageDao: MessageDao by lazy {
        MessageDao()
    }

    // MARK: - Public Functions
    fun toEntities(chatModels: List<ChatModel>): List<ChatEntity> = chatModels.map { toEntity(it) }

    fun toEntity(chatModel: ChatModel): ChatEntity {
        val chatEntity = ChatEntity()
        chatEntity.admin_ids = chatModel.admin_ids.convertToRealmList()
        chatEntity.avatar = AvatarMapper.toEntity(chatModel.avatar)
        chatEntity.category = chatModel.category ?: ""
        chatEntity.creator_id = chatModel.creator_id ?: ""
        chatEntity.id = chatModel.id
        chatEntity.is_pinned = chatModel.is_pinned
        chatEntity.is_removed = chatModel.is_removed
        chatEntity.mute_notification = chatModel.mute_notification
        chatEntity.name = chatModel.name ?: ""
        chatEntity.participant_ids = chatModel.participant_ids?.convertToRealmList() ?: RealmList()
        chatEntity.read_message_ts = chatModel.read_message_ts ?: 0.0
        chatEntity.unread_bot_count = chatModel.unread_bot_count
        chatEntity.unread_count = chatModel.unread_count
        return chatEntity
    }

    fun toModel(chatEntity: ChatEntity): ChatModel {
        val chatModel = ChatModel()
        chatModel.admin_ids = chatEntity.admin_ids.convertToList()
        chatModel.avatar = AvatarMapper.toModel(chatEntity.avatar)
        chatModel.category = chatEntity.category
        chatModel.creator_id = chatEntity.creator_id
        chatModel.id = chatEntity.id
        chatModel.is_pinned = chatEntity.is_pinned
        chatModel.is_removed = chatEntity.is_removed
        chatModel.last_message = messageDao.findLatestMessage(chatEntity.id)
        chatModel.mute_notification = chatEntity.mute_notification
        chatModel.name = chatEntity.name
        chatModel.participant_ids = chatEntity.participant_ids.convertToList()
        chatModel.read_message_ts = chatEntity.read_message_ts
        chatModel.unread_bot_count = chatEntity.unread_bot_count
        chatModel.unread_count = chatEntity.unread_count
        return chatModel
    }

    fun toModels(chatEntities: List<ChatEntity>): List<ChatModel> = chatEntities.map { toModel(it) }
}