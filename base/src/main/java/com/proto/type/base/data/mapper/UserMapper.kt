package com.proto.type.base.data.mapper

import com.proto.type.base.extension.convertToList
import com.proto.type.base.extension.convertToRealmList
import com.proto.type.base.data.database.entity.UserEntity
import com.proto.type.base.data.model.UserModel

object UserMapper {

    // MARK: - Public Functions
    fun toEntities(userModels: List<UserModel>): List<UserEntity> = userModels.map { toEntity(it) }

    fun toEntity(userModel: UserModel): UserEntity {
        val userEntity = UserEntity()
        userEntity.avatar = AvatarMapper.toEntity(userModel.avatar)
        userEntity.bio = userModel.bio ?: ""
        userEntity.blockee_ids = userModel.blockee_ids.convertToRealmList()
        userEntity.blocker_ids = userModel.blocker_ids.convertToRealmList()
        userEntity.display_name = userModel.display_name ?: ""
        userEntity.email = userModel.email ?: ""
        userEntity.enc_key = userModel.encryption_key ?: ""
        userEntity.first_name = userModel.first_name ?: ""
        userEntity.id = userModel.id
        userEntity.in_contact = userModel.in_contact ?: false
        userEntity.is_default_user = userModel.is_default_user
        userEntity.deleted = userModel.deleted
        userEntity.last_name = userModel.last_name ?: ""
        userEntity.last_online_ts = userModel.last_online_ts
        userEntity.local_name = userModel.local_name ?: ""
        userEntity.phone_number = userModel.phone_number
        userEntity.privacy_consented_ts = userModel.privacy_consented_ts
        userEntity.terms_consented_ts = userModel.terms_consented_ts
        userEntity.username = userModel.username ?: ""
        return userEntity
    }

    fun toModel(userEntity: UserEntity): UserModel {
        val userModel = UserModel()
        userModel.avatar = AvatarMapper.toModel(userEntity.avatar)
        userModel.bio = userEntity.bio
        userModel.blockee_ids = userEntity.blockee_ids.convertToList()
        userModel.blocker_ids = userEntity.blocker_ids.convertToList()
        userModel.display_name = userEntity.display_name
        userModel.email = userEntity.email
        userModel.encryption_key = userEntity.enc_key
        userModel.first_name = userEntity.first_name
        userModel.id = userEntity.id
        userModel.in_contact = userEntity.in_contact
        userModel.is_default_user = userEntity.is_default_user
        userModel.deleted = userEntity.deleted
        userModel.last_name = userEntity.last_name
        userModel.last_online_ts = userEntity.last_online_ts
        userModel.local_name = userEntity.local_name
        userModel.phone_number = userEntity.phone_number
        userModel.privacy_consented_ts = userEntity.privacy_consented_ts
        userModel.terms_consented_ts = userEntity.terms_consented_ts
        userModel.username = userEntity.username
        return userModel
    }

    fun toModels(userEntities: List<UserEntity>): List<UserModel> = userEntities.map { toModel(it) }
}