package com.proto.type.base.repository.user

import android.net.Uri
import com.proto.type.base.JSONMutableMap
import com.proto.type.base.SuccessCallback
import com.proto.type.base.data.model.*
import org.json.JSONArray
import org.json.JSONObject

interface IUserRepository {

    // MARK: - Database Functions
    fun deleteLocalUser(userId: String)

    fun deleteLocalUsers(userIds: Array<String>)

    fun findLocalUser(fieldName: String, value: String): UserModel?

    fun findLocalUser(userId: String): UserModel?

    fun findLocalUsers(userIds: Array<String>): List<UserModel>?

    fun getCurrentLocalUser(): UserModel

    fun getInContactLocalUsers(): List<UserModel>

    fun getLocalSuggestionUsers(): List<UserModel>

    fun getLocalUsers(): List<UserModel>

    fun isLoggedIn(): Boolean

    fun saveUserModel(user: UserModel): Boolean

    fun saveUserModels(users: List<UserModel>): Boolean

    fun saveUserJson(jsonObject: JSONObject): Boolean

    fun saveUserJsons(jsonArray: JSONArray): Boolean

    suspend fun updateLocalUserPhone(number: String): UserModel

    // MARK: - Context Function
    fun loadPhoneContacts(): List<ContactModel>

    // MARK: - Firebase Functions
    fun confirmPasswordReset(code: String, password: String, callback: SuccessCallback)

    suspend fun createFirebaseUser(email: String, password: String): Boolean

//    fun getFirebaseUser(): FirebaseUser?

    fun isFirebaseEmailVerified(): Boolean

    suspend fun isFirebaseUserExist(email: String): Boolean

    suspend fun login(email: String, password: String): String

    fun logout(): Boolean

    suspend fun resetFirebasePassword(email: String): Boolean

    suspend fun sendFirebaseEmailVerification(uriEmailVerified: String, appPackage: String): Boolean

    suspend fun updateFirebaseAvatar(uri: Uri): String?

    fun verifyFirebasePhoneNumber(number: String, callback: SuccessCallback)

    // MARK: - Service Functions
    suspend fun blockUser(userId: String): Boolean

    suspend fun checkChatQUserIdExist(userId: String): Boolean

    suspend fun checkChatQUsernameExist(userName: String): Boolean

    suspend fun deleteChatQAccount(): Boolean

    suspend fun loadChatQContacts()

    suspend fun loadContacts(deviceId: String): Boolean

    suspend fun loadCurrentUserBlockingData(): Boolean

    suspend fun loadCurrentUserEncryptionKey(): String

    suspend fun loadUser(userId: String, shouldFill: Boolean = true, filter: JSONMutableMap?): Boolean

    suspend fun loadUsers(phoneNumbers: List<String>? = null, deviceId: String? = null, userIds: List<String>, shouldFill: Boolean?): Boolean

    suspend fun registerNewChatQUser(request: UserRegisterRequest): Boolean

    suspend fun report(targetId: String, targetType: String, reasonType: String, reasonContent: String = ""): Boolean

    suspend fun syncContacts(contacts: List<ContactModel>): Boolean

    suspend fun unblockUser(userId: String): Boolean

    suspend fun updateBio(userId: String, bio: String): Boolean

    suspend fun updateChatQAvatar(userId: String, avatar: AvatarModel): Boolean

    suspend fun updateDisplayName(userId: String, displayName: String): Boolean

    fun updateLanguage(locale: String)

    suspend fun updateUsername(userId: String, username: String): String
}