package com.proto.type.base.data.mapper

import com.proto.type.base.data.database.entity.MessageLinkItemEntity
import com.proto.type.base.data.model.MessageLinkItemModel

object MessageLinkItemMapper {

    // MARK: - Public Functions
    fun toEntities(messageLinkItemModels: List<MessageLinkItemModel>): List<MessageLinkItemEntity> {
        val result = mutableListOf<MessageLinkItemEntity>()
        for (messageLinkItemModel in messageLinkItemModels) {
            val messageLinkItemEntity = MessageLinkItemEntity()
            messageLinkItemEntity.id = messageLinkItemModel.message_id + messageLinkItemModel.room_id
            messageLinkItemEntity.link = messageLinkItemModel.link
            messageLinkItemEntity.main_image_url = messageLinkItemModel.main_image_url ?: ""
            messageLinkItemEntity.message_id = messageLinkItemModel.message_id
            messageLinkItemEntity.room_id = messageLinkItemModel.room_id
            messageLinkItemEntity.sent_ts = messageLinkItemModel.sent_ts
            messageLinkItemEntity.title = messageLinkItemModel.title ?: ""
            result.add(messageLinkItemEntity)
        }
        return result
    }

    fun toModels(messageLinkItemEntities: List<MessageLinkItemEntity>): List<MessageLinkItemModel> {
        val result = mutableListOf<MessageLinkItemModel>()
        for (messageLinkItemEntity in messageLinkItemEntities) {
            val messageLinkItemModel = MessageLinkItemModel()
            messageLinkItemModel.link = messageLinkItemEntity.link
            messageLinkItemModel.main_image_url = messageLinkItemEntity.main_image_url
            messageLinkItemModel.message_id = messageLinkItemEntity.message_id
            messageLinkItemModel.room_id = messageLinkItemEntity.room_id
            messageLinkItemModel.sent_ts = messageLinkItemEntity.sent_ts
            messageLinkItemModel.title = messageLinkItemEntity.title
            result.add(messageLinkItemModel)
        }
        return result
    }
}