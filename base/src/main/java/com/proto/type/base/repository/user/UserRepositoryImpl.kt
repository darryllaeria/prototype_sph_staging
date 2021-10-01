package com.proto.type.base.repository.user

import android.content.Context
import android.net.Uri
import android.os.Build
import com.proto.type.base.BuildConfig
import com.proto.type.base.Constants
import com.proto.type.base.Constants.ErrorString.ERROR_NONE
import com.proto.type.base.Constants.ErrorString.ERROR_SERVER_ISSUE
import com.proto.type.base.Constants.ErrorString.ERROR_UNABLE_SAVE_LOCAL
import com.proto.type.base.Constants.ErrorString.ERROR_USERNAME_NOT_EXIST
import com.proto.type.base.JSONMutableMap
import com.proto.type.base.SuccessCallback
import com.proto.type.base.data.database.RealmDB
import com.proto.type.base.data.database.dao.UserDao
import com.proto.type.base.data.database.entity.UserEntityKey
import com.proto.type.base.data.mapper.UserMapper
import com.proto.type.base.data.model.*
import com.proto.type.base.data.remote.UserService
import com.proto.type.base.manager.PrefsManager
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

class UserRepositoryImpl(
    private val context: Context,
    private val prefsManager: PrefsManager,
//    private val firebase: FirebaseService,
    private val userDao: UserDao,
    private val userService: UserService
): IUserRepository {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = UserRepositoryImpl::class.java.simpleName
    }

    // MARK: - Database Functions
    override fun deleteLocalUser(userId: String) {
        userDao.deleteUser(userId)
    }

    override fun deleteLocalUsers(userIds: Array<String>) {
        userDao.deleteUsers(userIds)
    }

    override fun findLocalUser(fieldName: String, value: String): UserModel? {
        return runBlocking {
            userDao.findUser(fieldName, value)
        }
    }

    override fun findLocalUser(userId: String): UserModel? {
        return runBlocking {
            userDao.findUser(userId)
        }
    }

    override fun findLocalUsers(userIds: Array<String>): List<UserModel>? {
        return runBlocking {
            userDao.findUsers(userIds)
        }
    }

    override fun getCurrentLocalUser(): UserModel {
//        val id = firebase.getFirebaseUser()?.uid ?: ""
        val id = ""
        return userDao.findUser(id) ?: UserModel(id = id)
    }

    override fun getInContactLocalUsers(): List<UserModel> {
        return runBlocking {
            userDao.getInContactUsers()
        }
    }

    override fun getLocalSuggestionUsers(): List<UserModel> {
//        val fbUser = firebase.getFirebaseUser()
//        if (fbUser != null) {
//            return userDao.findRecentUsers(fbUser.email!!)
//        }
        return emptyList()
    }

    override fun getLocalUsers(): List<UserModel> {
        return runBlocking {
            userDao.getAllUsers()
        }
    }

    override fun isLoggedIn(): Boolean {
        return runBlocking {
            false
//            userDao.findUser(UserEntityKey.id, firebase.getFirebaseUser()?.uid ?: "") != null
        }
    }

    override fun saveUserModel(user: UserModel): Boolean {
        return try {
            userDao.insertOrUpdateEntity(UserMapper.toEntity(user))
            true
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    override fun saveUserModels(users: List<UserModel>): Boolean {
        return try {
            userDao.insertOrUpdateEntities(UserMapper.toEntities(users))
            true
        } catch (e: Exception) {
            AppLog.d(TAG, "Store users failed with exception: $e")
            false
        }
    }

    override fun saveUserJson(jsonObject: JSONObject): Boolean {
        return try {
            runBlocking {
                userDao.saveJSONData(jsonObject) != null
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    override fun saveUserJsons(jsonArray: JSONArray): Boolean {
        return try {
            runBlocking {
                val ids = userDao.saveJSONDatas(jsonArray)
                val dataSize = jsonArray.length()
                dataSize != 0 && ids.size == dataSize
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }

    override suspend fun updateLocalUserPhone(number: String): UserModel {
        // TODO("Implement update phone number feature")
        return UserModel.generateLocalUser()
    }

    // MARK: - Firebase Functions
    override fun confirmPasswordReset(code: String, password: String, callback: SuccessCallback) {
//        firebase.confirmPasswordReset(code, password, callback)
    }

    override suspend fun createFirebaseUser(email: String, password: String): Boolean {
//        val user = firebase.createUser(email, password)
//        if (user != null) {
//            val idToken = user.getIdToken(true).await()
//            prefsManager.putString(Constants.KEY_AUTH_KEY, idToken.token!!)
//            prefsManager.putLong(Constants.KEY_TOKEN_EXPIRATION, idToken.expirationTimestamp)
//        }
//        return user != null
        return true
    }

//    override fun getFirebaseUser() = firebase.getFirebaseUser()

    override fun isFirebaseEmailVerified(): Boolean {
//        return firebase.isEmailVerified()
        return false
    }

    override suspend fun isFirebaseUserExist(email: String): Boolean {
//        return firebase.isUserExisted(email)
        return false
    }

    override suspend fun login(email: String, password: String): String {
//        val user = firebase.login(email, password)
//        if (user != null) {
//            val idToken = user.getIdToken(true).await()
//            prefsManager.putString(Constants.KEY_AUTH_TOKEN, idToken.token!!)
//            prefsManager.putLong(Constants.KEY_TOKEN_EXPIRATION, idToken.expirationTimestamp)
//        }
//        return user?.uid ?: ""
        return ""
    }

    override fun logout(): Boolean {
        return try {
            prefsManager.putBool(Constants.KEY_FIRST_TIME, true)
//            firebase.logout()
            runBlocking {
                RealmDB.clearDB()
            }
            true
        } catch (e: Exception) {
            AppLog.d(TAG, "Logout failed with exception: $e")
            false
        }
    }

    override suspend fun resetFirebasePassword(email: String): Boolean {
//        return firebase.resetPassword(email)
        return false
    }

    override suspend fun sendFirebaseEmailVerification(uriEmailVerified: String, appPackage: String): Boolean {
//        return firebase.sendEmailVerification(uriEmailVerified, appPackage)
        return false
    }

    override suspend fun updateFirebaseAvatar(uri: Uri): String? {
//        return firebase.updateImageProfile(uri)
        return ""
    }

    override fun verifyFirebasePhoneNumber(number: String, callback: SuccessCallback) {
//        firebase.verifyPhoneNumber(number, callback)
    }

    // MARK: - Service Functions
    override suspend fun blockUser(userId: String): Boolean {
        return userService.blockUser(ManageBlockeeRequest(userId)).isSuccessful
    }

    override suspend fun checkChatQUserIdExist(userId: String): Boolean {
        return userService.checkUserExists(mapOf("user_id" to userId)).status
    }

    override suspend fun checkChatQUsernameExist(userName: String): Boolean {
        return userService.checkUserExists(mapOf("username" to userName)).status
    }

    override suspend fun deleteChatQAccount(): Boolean {
        val result = userService.deleteUser(UserDeleteRequest(Build.MODEL + BuildConfig.VERSION_NAME))
//        firebase.deleteAccount()
        runBlocking {
            RealmDB.clearDB()
        }
        return result.isSuccessful
    }

    override suspend fun loadChatQContacts() {
        val params = HashMap<String, String>()
        userService.getChatQContacts(params)
    }

    override suspend fun loadContacts(deviceId: String): Boolean {
        val params = HashMap<String, String>()
        params["attach_profile"] = "true"
        params["device_id"] = deviceId
        val jsonString = userService.getChatQContacts(params).string().trim()
        val jsonArray = JSONArray(jsonString)
        return saveUserJsons(jsonArray) // Save users info to Realm
    }

    override suspend fun loadCurrentUserBlockingData(): Boolean {
        val jsonString = userService.getBlockList().string().trim()
        val jsonObject = JSONObject(jsonString)
//        jsonObject.put(UserJsonKey.id, firebase.getFirebaseUser()?.uid ?: "")
        return saveUserJson(jsonObject) // Save user block data to Realm
    }

    override suspend fun loadCurrentUserEncryptionKey(): String {
        val jsonString = userService.getSecretKey().string().trim()
        val jsonObject = JSONObject(jsonString)
//        jsonObject.put(UserJsonKey.id, firebase.getFirebaseUser()?.uid ?: "")
        val newKey = jsonObject.optString(UserJsonKey.user_key) ?: ""
        val saveUserKeyStatus = saveUserJson(jsonObject) // Save user block data to Realm
        return if (newKey.isNotEmpty() && saveUserKeyStatus)
            newKey
        else
            ""
    }

    override suspend fun loadUser(userId: String, shouldFill: Boolean, filter: JSONMutableMap?): Boolean {
        val finalFilter = filter ?: mutableMapOf()
        if (shouldFill) { finalFilter[UserJsonKey.fill] = true }
        val jsonString = userService.getProfile(userId, finalFilter).string().trim()
        return saveUserJson(JSONObject(jsonString))
    }

    override suspend fun loadUsers(phoneNumbers: List<String>?, deviceId: String?, userIds: List<String>, shouldFill: Boolean?): Boolean {
        val jsonString = userService.getProfiles(GetProfilesRequest(phoneNumbers, deviceId, userIds, shouldFill)).string().trim()
        val jsonArray = JSONArray(jsonString)
        return saveUserJsons(jsonArray) // Save users info to Realm
    }

    override suspend fun registerNewChatQUser(request: UserRegisterRequest): Boolean {
        val jsonString = userService.createUser(request).string().trim()
        val jsonObject = JSONObject(jsonString)
        return saveUserJson(jsonObject) // Save user info to Realm
    }

    override suspend fun report(targetId: String, targetType: String, reasonType: String, reasonContent: String): Boolean {
        return userService.report(UserReportRequest(reasonContent, reasonType, targetType, targetId)).isSuccessful
    }

    override suspend fun unblockUser(userId: String): Boolean {
        return userService.unblockUser(ManageBlockeeRequest(userId)).isSuccessful
    }

    override suspend fun updateBio(userId: String, bio: String): Boolean {
        val success = userService.updateProfile("me", UserUpdateRequest(bio = bio)).isSuccessful
        return if (success) {
            val jsonMap = mapOf(UserEntityKey.id to userId, UserEntityKey.bio to bio)
            saveUserJson(JSONObject(jsonMap)) // Save user info to Realm
        } else {
            AppLog.d(TAG, "Update bio failed due to server error")
            false
        }
    }

    override suspend fun updateChatQAvatar(userId: String, avatar: AvatarModel): Boolean {
        val success = userService.updateProfile("me", UserUpdateRequest(avatar = avatar)).isSuccessful
        return if (success) {
            val jsonMap = mapOf(UserEntityKey.id to userId, UserEntityKey.avatar to avatar)
            saveUserJson(JSONObject(jsonMap)) // Save user info to Realm
        } else {
            AppLog.d(TAG, "Update avatar failed due to server error")
            false
        }
    }

    override suspend fun updateDisplayName(userId: String, displayName: String): Boolean {
        val success = userService.updateProfile("me", UserUpdateRequest(displayName = displayName)).isSuccessful
        return if (success) {
            val jsonMap = mapOf(UserEntityKey.id to userId, UserEntityKey.display_name to displayName)
            saveUserJson(JSONObject(jsonMap)) // Save user info to Realm
        } else {
            AppLog.d(TAG, "Update displayName failed due to server error")
            false
        }
    }

    override fun updateLanguage(locale: String) {
        // TODO("Implement update language feature")
    }

    override suspend fun updateUsername(userId: String, username: String): String {
        val userExistResponse = userService.checkUserExists(mapOf("username" to username))
        return if (userExistResponse.status) {
            AppLog.d(TAG, "Update ChatQId failed because ChatQId is already existed")
            ERROR_USERNAME_NOT_EXIST
        } else {
            val success = userService.updateProfile("me", UserUpdateRequest(userName = username)).isSuccessful
            if (success) {
                val jsonMap = mapOf(UserEntityKey.id to userId, UserEntityKey.username to username)
                val saveSuccess = saveUserJson(JSONObject(jsonMap))
                if (!saveSuccess)
                    AppLog.d(TAG, "Update ChatQId failed due to Realm issue")
                if (saveSuccess)
                    ERROR_UNABLE_SAVE_LOCAL
                else
                    ERROR_NONE
            } else {
                AppLog.d(TAG, "Update ChatQId failed due to server error")
                ERROR_SERVER_ISSUE
            }
        }
    }
}