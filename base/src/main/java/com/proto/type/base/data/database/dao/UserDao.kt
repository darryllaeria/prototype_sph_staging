package com.proto.type.base.data.database.dao

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.database.entity.UserEntity
import com.proto.type.base.data.database.entity.UserEntityKey
import com.proto.type.base.data.mapper.UserMapper
import com.proto.type.base.data.model.AvatarJsonKey
import com.proto.type.base.data.model.UserJsonKey
import com.proto.type.base.data.model.UserModel
import io.realm.Case
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject

enum class ConsentAction {
    CONSENT,
    WITHDRAW;

    // MARK: - Companion Object
    companion object {
        fun contain(action: String): Boolean = values().map { it.name.toLowerCase() }.contains(action)
    }
}

class UserDao: BaseDao() {

    // MARK: - Public Functions
    fun deleteUser(userId: String) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(UserEntity::class.java)
                    .equalTo(UserEntityKey.id, userId)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun deleteUsers(userIds: Array<String>) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.where(UserEntity::class.java)
                    .`in`(UserEntityKey.id, userIds)
                    .findAll()
                    .deleteAllFromRealm()
            }
        }
    }

    fun findRecentUsers(email: String): List<UserModel> {
        // TODO("Implement load recent users function")
        return emptyList()
    }

    fun findUser(fieldId: String, fieldValue: String): UserModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(UserEntity::class.java)
                .equalTo(fieldId, fieldValue)
                .findFirst()?.let { UserMapper.toModel(it) }
        }
    }

    fun findUser(userId: String): UserModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(UserEntity::class.java)
                .equalTo(UserEntityKey.id, userId)
                .findFirst()?.let { UserMapper.toModel(it) }
        }
    }

    fun findUsers(userIds: Array<String>): List<UserModel> {
        Realm.getDefaultInstance().use { realm ->
            return UserMapper.toModels(
                realm.where(UserEntity::class.java)
                .`in`(UserEntityKey.id, userIds, Case.SENSITIVE)
                .findAll()
            )
        }
    }

    fun getAllUsers(): List<UserModel> {
        Realm.getDefaultInstance().use { realm ->
            return UserMapper.toModels(
                realm.where(UserEntity::class.java)
                    .findAll()
            )
        }
    }

    fun getCurrentUser(): UserModel? {
        Realm.getDefaultInstance().use { realm ->
            return realm.where(UserEntity::class.java)
            .equalTo(UserEntityKey.is_default_user, true)
            .findFirst()?.let { UserMapper.toModel(it) }
        }
    }

    fun getInContactUsers(): List<UserModel> {
        Realm.getDefaultInstance().use { realm ->
            return UserMapper.toModels(
                realm.where(UserEntity::class.java)
                .equalTo(UserEntityKey.in_contact, true)
                .findAll()
            )
        }
    }

    fun saveJSONData(rawJSONObject: JSONObject): String? {
        val id = (rawJSONObject.opt(UserJsonKey.id) as? String) ?: ""
        if (id.isEmpty()) {
            return null
        }
        val jsonMap: JSONMutableMap = mutableMapOf(UserEntityKey.id to id)
        (rawJSONObject.opt(UserJsonKey.bio) as? String)?.let { jsonMap[UserEntityKey.bio] = it }
        (rawJSONObject.opt(UserJsonKey.blockee_ids) as? JSONArray)?.let { jsonMap[UserEntityKey.blockee_ids] = it }
        (rawJSONObject.opt(UserJsonKey.blocker_ids) as? JSONArray)?.let { jsonMap[UserEntityKey.blocker_ids] = it }
        (rawJSONObject.opt(UserJsonKey.deleted) as? Boolean)?.let { jsonMap[UserEntityKey.deleted] = it }
        val exist = (rawJSONObject.opt(UserJsonKey.exists) as? Boolean) ?: true
        if (!exist) { jsonMap[UserEntityKey.deleted] = true } // Backend asks frontend team to assume that if user object doesn't exist in the backend, it means that user is deleted and implement this logic on frontend instead of backend.
        (rawJSONObject.opt(UserJsonKey.user_key) as? String)?.let { jsonMap[UserEntityKey.enc_key] = it }
        val deleted = jsonMap[UserJsonKey.deleted] as? Boolean ?: false
        if (deleted) { // If user is deleted, all data must be cleared out due to GDPR
            jsonMap[UserJsonKey.avatar] = null // Overwrite avatar object to be blank.
            jsonMap[UserJsonKey.email] = "" // Overwrite avatar value to be blank.
            jsonMap[UserJsonKey.first_name] = "" // Overwrite avatar value to be blank.
            jsonMap[UserJsonKey.last_name] = "" // Overwrite avatar value to be blank.
            jsonMap[UserJsonKey.local_name] = "" // Overwrite avatar value to be blank.
            jsonMap[UserJsonKey.display_name] = "" // Overwrite avatar value to be blank.
            jsonMap[UserJsonKey.phone_number] = "" // Overwrite avatar value to be blank.
        } else {
            (rawJSONObject.opt(UserJsonKey.avatar) as? JSONObject)?.let {
                if (!(it.opt(AvatarJsonKey.url) as? String).isNullOrEmpty()) jsonMap[UserEntityKey.avatar] = it
            }
            (rawJSONObject.opt(UserJsonKey.email) as? String)?.let { jsonMap[UserEntityKey.email] = it }
            (rawJSONObject.opt(UserJsonKey.first_name) as? String)?.let { jsonMap[UserEntityKey.first_name] = it }
            (rawJSONObject.opt(UserJsonKey.last_name) as? String)?.let { jsonMap[UserEntityKey.last_name] = it }
            (rawJSONObject.opt(UserJsonKey.local_name) as? String)?.let { jsonMap[UserEntityKey.local_name] = it }
            (rawJSONObject.opt(UserJsonKey.display_name) as? String)?.let { jsonMap[UserEntityKey.display_name] = it }
            (rawJSONObject.opt(UserJsonKey.phone_number) as? String)?.let { jsonMap[UserEntityKey.phone_number] = it }
        }
        val userConsentJSONObject = rawJSONObject.opt(UserJsonKey.consent) as? JSONObject
        val privacyConsentJSONObject = userConsentJSONObject?.opt(UserJsonKey.privacy) as? JSONObject
        val privacyConsentAction = privacyConsentJSONObject?.opt(UserJsonKey.action) as? String ?: ""
        if (ConsentAction.contain(privacyConsentAction)) {
            when (ConsentAction.valueOf(privacyConsentAction.toUpperCase())) {
                ConsentAction.CONSENT -> (privacyConsentJSONObject?.opt(UserJsonKey.ts) as? Double)?.let { jsonMap[UserEntityKey.privacy_consented_ts] = it }
                ConsentAction.WITHDRAW -> jsonMap[UserEntityKey.privacy_consented_ts] = 0.0
            }
        }
        val termsConsentJSONObject = userConsentJSONObject?.opt(UserJsonKey.terms) as? JSONObject
        val termsConsentAction = termsConsentJSONObject?.opt(UserJsonKey.action) as? String ?: ""
        if (ConsentAction.contain(termsConsentAction)) {
            when (ConsentAction.valueOf(termsConsentAction.toUpperCase())) {
                ConsentAction.CONSENT -> (termsConsentJSONObject?.opt(UserJsonKey.ts) as? Double)?.let { jsonMap[UserEntityKey.terms_consented_ts] = it }
                ConsentAction.WITHDRAW -> jsonMap[UserEntityKey.terms_consented_ts] = 0.0
            }
        }
        (rawJSONObject.opt(UserJsonKey.username) as? String)?.let { jsonMap[UserEntityKey.username] = it }
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                it.createOrUpdateObjectFromJson(UserEntity::class.java, JSONObject(jsonMap))
            }
        }
        return id
    }

    fun saveJSONDatas(rawJSONArray: JSONArray): List<String> {
        val ids = mutableListOf<String>()
        for (i in 0 until rawJSONArray.length()) {
            rawJSONArray.optJSONObject(i)?.let { jsonObject ->
                saveJSONData(jsonObject)?.let { ids.add(it) }
            }
        }
        return ids
    }
}