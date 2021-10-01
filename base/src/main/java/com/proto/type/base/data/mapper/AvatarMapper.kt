package com.proto.type.base.data.mapper

import com.proto.type.base.data.database.entity.AvatarEntity
import com.proto.type.base.data.model.AvatarModel

object AvatarMapper {

    // MARK: - Public Functions
    fun toEntity(avatarModel: AvatarModel?): AvatarEntity {
        return if (avatarModel != null)
            AvatarEntity(
                file_name = avatarModel.file_name,
                id = avatarModel.id ?: "",
                type = avatarModel.type,
                url = avatarModel.url
            )
        else
            AvatarEntity()
    }

    fun toModel(avatarEntity: AvatarEntity?): AvatarModel {
        return if (avatarEntity != null)
            AvatarModel(
                file_name = avatarEntity.file_name,
                id = avatarEntity.id,
                type = avatarEntity.type,
                url = avatarEntity.url
            )
        else
            AvatarModel()
    }
}