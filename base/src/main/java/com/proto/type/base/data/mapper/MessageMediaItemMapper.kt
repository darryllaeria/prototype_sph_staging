package com.proto.type.base.data.mapper

import com.proto.type.base.data.database.entity.MessageMediaItemEntity
import com.proto.type.base.data.model.MessageMediaItemModel

object MessageMediaItemMapper {

    // MARK: - Public Functions
    fun toEntities(messageMediaItemModels: List<MessageMediaItemModel>): List<MessageMediaItemEntity> {
        val result = mutableListOf<MessageMediaItemEntity>()
        for (messageMediaItemModel in messageMediaItemModels) {
            val messageMediaItemEntity = MessageMediaItemEntity()
            messageMediaItemEntity.id = messageMediaItemModel.message_id + messageMediaItemModel.room_id
            messageMediaItemEntity.media_id = messageMediaItemModel.data.id
            messageMediaItemEntity.message_id = messageMediaItemModel.message_id
            messageMediaItemEntity.room_id = messageMediaItemModel.room_id
            messageMediaItemEntity.sent_ts = messageMediaItemModel.sent_ts
            messageMediaItemEntity.url = messageMediaItemModel.data.url
            result.add(messageMediaItemEntity)
        }
        return result
    }

    fun toModels(messageMediaItemEntities: List<MessageMediaItemEntity>): List<MessageMediaItemModel> {
        val result = mutableListOf<MessageMediaItemModel>()
        for (messageMediaItemEntity in messageMediaItemEntities) {
            val messageMediaItemModel = MessageMediaItemModel()
            messageMediaItemModel.data.id = messageMediaItemEntity.media_id
            messageMediaItemModel.data.url = messageMediaItemEntity.url
            messageMediaItemModel.room_id = messageMediaItemEntity.room_id
            messageMediaItemModel.message_id = messageMediaItemEntity.message_id
            messageMediaItemModel.sent_ts = messageMediaItemEntity.sent_ts
            result.add(messageMediaItemModel)
        }
        return result
    }
}