package com.proto.type.base.data.mapper

import com.proto.type.base.Constants
import com.proto.type.base.data.database.dao.MessageDao
import com.proto.type.base.data.database.dao.MessageDataType
import com.proto.type.base.data.database.dao.SendStatus
import com.proto.type.base.data.database.entity.MessageEntity
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.extension.convertToList
import com.proto.type.base.extension.convertToRealmList

object MessageMapper {

    // MARK: - Private Constant
    private val messageDao: MessageDao by lazy {
        MessageDao()
    }

    // MARK: - Public Functions
    fun toEntities(messageModels: List<MessageModel>): List<MessageEntity> = messageModels.map { toEntity(it) }

    fun toModel(messageEntity: MessageEntity): MessageModel {
        val messageModel = MessageModel(id = messageEntity.id, room_id = messageEntity.room_id)
        messageModel.is_edited = messageEntity.is_edited
        messageModel.is_forwarded = messageEntity.is_forwarded
        messageModel.data.type = MessageDataType.valueOf(messageEntity.data_type)
        messageModel.data.value_url = messageEntity.data_url
        messageModel.data.value = messageEntity.data_value
        messageModel.mention_ids = messageEntity.mention_ids.convertToList()
        messageModel.modified_ts = messageEntity.modified_ts
        messageModel.quoted_message = messageDao.findQuoteMessage(messageEntity.quoted_message_id)
        messageModel.quoted_message_id = messageEntity.quoted_message_id
        messageModel.room_id = messageEntity.room_id
        messageModel.send_status = SendStatus.valueOf(messageEntity.send_status)
        messageModel.sender.id = messageEntity.sender_id
        messageModel.sender.type = messageEntity.sender_type
        messageModel.sent_ts = messageEntity.sent_ts
        return messageModel
    }

    fun toModels(messageEntities: List<MessageEntity>): List<MessageModel> = messageEntities.map { toModel(it) }

    // MARK: - Private Functions
    private fun toEntity(messageModel: MessageModel): MessageEntity {
        val messageEntity = MessageEntity(id = messageModel.id, room_id = messageModel.room_id)
        messageEntity.is_edited = messageModel.is_edited
        messageEntity.is_forwarded = messageModel.is_forwarded
        val messageDataType = messageModel.data.type ?: MessageDataType.Text
        messageEntity.data_type = messageDataType.name
        messageEntity.data_value = when (messageDataType) {
            MessageDataType.Text -> messageModel.data.value
            MessageDataType.Image -> messageModel.data.value_url
            else -> messageModel.data.value_any.toString()
        }
        messageEntity.mention_ids = messageModel.mention_ids.convertToRealmList()
        messageEntity.modified_ts = messageModel.modified_ts
        messageEntity.quoted_message_id = messageModel.quoted_message_id
        messageEntity.room_id = messageModel.room_id
        messageEntity.send_status = messageModel.send_status.name
        messageEntity.sender_id = messageModel.sender.id ?: ""
        messageEntity.sender_type = messageModel.sender.type ?: ""
        messageEntity.sent_ts = messageModel.sent_ts
        return messageEntity
    }
}