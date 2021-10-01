package com.proto.type.base.data.firebase
//
//import android.content.Context
//import android.net.Uri
//import android.os.Build
//import com.proto.type.base.Constants
//import com.proto.type.base.SuccessCallback
//import com.proto.type.base.await
//import com.proto.type.base.R
//import com.proto.type.base.data.database.dao.market_data.AssetDao
//import com.proto.type.base.data.database.dao.market_data.AssetExchangeDao
//import com.proto.type.base.data.database.dao.market_data.ExchangeDao
//import com.proto.type.base.data.database.entity.market_data.AssetEntity
//import com.proto.type.base.data.database.entity.market_data.AssetExchangeEntity
//import com.proto.type.base.data.database.entity.market_data.ExchangeEntity
//import com.proto.type.base.manager.PrefsManager
//import com.proto.type.base.extension.isValidJsonObject
//import com.proto.type.base.extension.toListJSONObject
//import com.proto.type.base.utils.AppLog
//import com.google.android.gms.tasks.TaskExecutors
//import com.google.firebase.FirebaseApp
//import com.google.firebase.FirebaseException
//import com.google.firebase.auth.*
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import java.util.*
//import java.util.concurrent.TimeUnit
//import kotlin.text.Charsets.UTF_8
//
//enum class FileDataType(val type: String) {
//    JPG("image/jpg"),
//    JPEG("image/jpeg"),
//    PNG("image/png"),
//    OTHER("application/octet-stream")
//}
//
//enum class NewImagePurpose {
//    GROUP_CHAT_AVATAR {
//        override fun generateUniqueImageId(userId: String, roomId: String): String {
//            var elements = mutableListOf(userId, UUID.randomUUID().toString())
//            if (roomId.isNotEmpty()) { elements.add(ordinal, roomId) }
//            return elements.joinToString(".")
//        }
//
//        override fun imageNameFromMessageId(userId: String, roomId: String, messageId: String): String {
//            var elements = mutableListOf(userId, messageId)
//            if (roomId.isNotEmpty()) { elements.add(ordinal, roomId) }
//            return elements.joinToString(".")
//        }
//    },
//    MESSAGE {
//        override fun generateUniqueImageId(userId: String, roomId: String): String {
//            var elements = mutableListOf(userId, UUID.randomUUID().toString())
//            if (roomId.isNotEmpty()) { elements.add(ordinal, roomId) }
//            return elements.joinToString(".")
//        }
//
//        override fun imageNameFromMessageId(userId: String, roomId: String, messageId: String): String {
//            var elements = mutableListOf(userId, messageId)
//            if (roomId.isNotEmpty()) { elements.add(ordinal, roomId) }
//            return elements.joinToString(".")
//        }
//    },
//    USER_AVATAR {
//        override fun generateUniqueImageId(userId: String, roomId: String): String {
//            val elements = listOf(userId, UUID.randomUUID().toString())
//            return elements.joinToString(".")
//        }
//
//        override fun imageNameFromMessageId(userId: String, roomId: String, messageId: String): String {
//            val elements = listOf(userId, messageId)
//            return elements.joinToString(".")
//        }
//    };
//
//    // MARK: - Abstract Functions
//    abstract fun generateUniqueImageId(userId: String, roomId: String = ""): String
//    abstract fun imageNameFromMessageId(userId: String, roomId: String = "", messageId: String = ""): String
//}
//
//class FirebaseService(
//    private val firebaseAuth: FirebaseAuth,
//    private val firebaseConfig: FirebaseRemoteConfig,
//    private val firebaseStorage: FirebaseStorage,
//    private val phoneAuth: PhoneAuthProvider,
//    private val prefsMan: PrefsManager
//) {
//
//    // MARK: - Companion Object
//    companion object {
//        private val TAG: String = FirebaseService::class.java.simpleName
//    }
//
//    // MARK: - Public Variable
//    var isRemoteConfigActivated = false
//        private set // the setter is private and has the default implementation
//
//    // MARK: - Private Variable
//    private var isRetrievingRemoteConfig = false
//        private set // the setter is private and has the default implementation
//
//    // MARK: - Private Constants
//    private val assetDao: AssetDao by lazy {
//        AssetDao()
//    }
//    private val assetExchangeDao: AssetExchangeDao by lazy {
//        AssetExchangeDao()
//    }
//    private val exchangeDao: ExchangeDao by lazy {
//        ExchangeDao()
//    }
//
//    // MARK: - Public Functions
//    fun checkForMarketDataUpdate() {
//        if (!isRemoteConfigActivated) {
//            AppLog.d(TAG, "Firebase remote config is not activated")
//            return
//        }
//        val assetVersion = firebaseConfig.getString("asset_version")?.toFloat()
//        val assetExchangeVersion = firebaseConfig.getString("asset_exchange_version")?.toFloat()
//        val exchangeVersion = firebaseConfig.getString("exchange_version")?.toFloat()
//        if (assetVersion != null && assetExchangeVersion != null && exchangeVersion != null) {
//            updateNewDataFromFirebase(AssetEntity.TAG, assetVersion) {
//                updateNewDataFromFirebase(AssetExchangeEntity.TAG, assetExchangeVersion)
//            }
//            updateNewDataFromFirebase(ExchangeEntity.TAG, exchangeVersion)
//        } else {
//            AppLog.d(TAG, "The versions are not retrievable, cannot compare")
//        }
//    }
//
//    fun clearLocalToken() {
//        prefsMan.deleteKey(Constants.KEY_AUTH_TOKEN)
//        prefsMan.deleteKey(Constants.KEY_TOKEN_EXPIRATION)
//    }
//
//    fun confirmPasswordReset(code: String, password: String, callback: SuccessCallback) {
//        firebaseAuth.confirmPasswordReset(code, password).addOnCompleteListener {
//            callback.invoke(it.isSuccessful)
//        }
//    }
//
//    suspend fun createUser(email: String, password: String): FirebaseUser? {
//        return try {
//            firebaseAuth.createUserWithEmailAndPassword(email, password).await().user
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Create Firebase user failed with exception: $e")
//            null
//        }
//    }
//
//    fun deleteAccount() {
//        firebaseAuth.currentUser?.delete()
//    }
//
//    fun fetchLatestRemoteConfig(successCallback: SuccessCallback? = null) {
//        if (isRetrievingRemoteConfig) return
//        isRetrievingRemoteConfig = true
//        firebaseConfig.fetchAndActivate()
//            .addOnCompleteListener { task ->
//                isRetrievingRemoteConfig = false
//                isRemoteConfigActivated = task.isSuccessful
//                if (isRemoteConfigActivated) {
//                    val updated = task.result
//                    AppLog.d(TAG, "Config params updated: $updated")
//                } else {
//                    AppLog.d(TAG, "Config params updated failed")
//                }
//                successCallback?.invoke(isRemoteConfigActivated)
//            }
//    }
//
//    fun getFirebaseUser() = firebaseAuth.currentUser
//
//    fun isEmailVerified(): Boolean {
//        return firebaseAuth.currentUser?.isEmailVerified ?: false
//    }
//
//    suspend fun isUserExisted(email: String): Boolean {
//        return try {
//            val result = firebaseAuth.fetchSignInMethodsForEmail(email).await()
//            !result.signInMethods.isNullOrEmpty()
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Check is user existed failed with exception: $e")
//            false
//        }
//    }
//
//    suspend fun login(email: String, password: String): FirebaseUser? {
//        return try {
//            firebaseAuth.signInWithEmailAndPassword(email, password).await().user
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Login using email & password failed with exception: $e")
//            null
//        }
//    }
//
//    fun logout() {
//        firebaseAuth.signOut()
//    }
//
//    fun refreshAuth() {
//        firebaseAuth.currentUser?.reload()
//    }
//
//    suspend fun refreshToken(): GetTokenResult? {
//        return firebaseAuth.currentUser?.getIdToken(true)?.await()
//    }
//
//    fun refreshLocalToken() {
//        if (prefsMan.getLong(Constants.KEY_TOKEN_EXPIRATION, 0) - (System.currentTimeMillis() / 1000) <= Constants.PRE_EXPIRED_TIME) {
//            CoroutineScope(Dispatchers.Main).launch {
//                val result = refreshToken()
//                if (result != null) {
//                    prefsMan.putString(Constants.KEY_AUTH_TOKEN, result.token!!)
//                    prefsMan.putLong(Constants.KEY_TOKEN_EXPIRATION, result.expirationTimestamp)
//                }
//            }
//        }
//    }
//
//    suspend fun resetPassword(email: String): Boolean {
//        val settings = ActionCodeSettings.newBuilder()
//            .setHandleCodeInApp(true)
//            .setUrl(Constants.Uri.URI_RESET_PASSWORD)
//            .setAndroidPackageName("com.chatqsg.chatqstaging", true, Build.VERSION_CODES.M.toString())
//            .build()
//        return try {
//            firebaseAuth.sendPasswordResetEmail(email, settings).await()
//            true
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Reset password failed with exception: $e")
//            false
//        }
//    }
//
//    suspend fun sendEmailVerification(uriEmailVerified: String, appPackage: String): Boolean {
//        val settings = ActionCodeSettings.newBuilder()
//            .setHandleCodeInApp(true)
//            .setUrl(uriEmailVerified)
//            .setAndroidPackageName(appPackage, true, Build.VERSION_CODES.M.toString())
//            .build()
//        return try {
//            firebaseAuth.currentUser?.sendEmailVerification(settings)?.await()
//            true
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Send email verification failed with exception: $e")
//            false
//        }
//    }
//
//    fun setupFirebaseApp(context: Context) {
//        FirebaseApp.initializeApp(context)
//        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
//            if (it.isSuccessful) {
//                val token = it.result!!.token
//                prefsMan.putString(Constants.KEY_FCM_TOKEN, token)
//            }
//        }
//    }
//
//    fun setupFirebaseRemoteConfig() {
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 3600
//        }
//        firebaseConfig.setConfigSettingsAsync(configSettings)
//        firebaseConfig.setDefaultsAsync(R.xml.staging_remote_config_defaults)
//    }
//
//    suspend fun uploadFile(uri: Uri, name: String, fileType: FileDataType): Pair<String?, String?> {
//        val fileRef = firebaseStorage.reference.child(generateRefPath(name, fileType))
//        return try {
//            fileRef.putFile(uri).await()
//            val url = fileRef.downloadUrl.await()
//            Pair(name, url.toString())
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Upload file failed with exception: $e")
//            Pair(null, null)
//        }
//    }
//
//    fun uploadFile(uri: Uri, name: String, fileType: FileDataType, uploadCallback: (String?, String?) -> Unit) {
//        val fileRef = firebaseStorage.reference.child(generateRefPath(name, fileType))
//        try {
//            fileRef.putFile(uri).addOnCompleteListener {
//                if (it.isSuccessful) {
//                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                        uploadCallback.invoke(name, downloadUri.toString())
//                    }.addOnFailureListener {
//                        uploadCallback.invoke(null, null)
//                    }
//                } else {
//                    uploadCallback.invoke(null, null)
//                }
//            }.addOnFailureListener {
//                uploadCallback.invoke(null, null)
//            }
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Upload file failed with exception: $e")
//            uploadCallback.invoke(null, null)
//        }
//    }
//
//    suspend fun updateImageProfile(uri: Uri): String? {
//        val userId = firebaseAuth.currentUser?.uid!!
//        val avatarRef = firebaseStorage.reference.child("$userId/images/${uri.lastPathSegment}")
//        return try {
//            avatarRef.putFile(uri).await()
//            val url = avatarRef.downloadUrl.await()
//            url.toString()
//        } catch (e: Exception) {
//            AppLog.d(TAG, "Update image profile with exception: $e")
//            null
//        }
//    }
//
//    fun verifyPhoneNumber(number: String, callback: SuccessCallback) {
//        phoneAuth.verifyPhoneNumber(number, Constants.DELAY_OTP_TIME, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        firebaseAuth.currentUser?.updatePhoneNumber(credential)
//                    }
//                }
//            }
//
//            override fun onCodeSent(verifyId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                super.onCodeSent(verifyId, token)
//            }
//
//            override fun onVerificationFailed(p0: FirebaseException) { }
//        })
//    }
//
//    // MARK: - Private Function
//    private fun generateRefPath(name: String, fileType: FileDataType): String {
//        val typeComponents = fileType.type.split("/")
//        var currentCategory = "general"
//        typeComponents.firstOrNull()?.let {
//            if (listOf("image", "video").contains(it)) {
//                currentCategory = it + "s"
//            }
//        }
//        var currentExtension = ""
//        typeComponents.lastOrNull()?.let {
//            if (listOf("jpeg", "png").contains(it)) {
//                currentExtension = it
//            }
//        }
//        return "$currentCategory/$name${if (currentExtension.isNotEmpty()) ".$currentExtension" else ""}"
//    }
//
//    /// Method for updating new Instrument data from Firebase Storage
//    ///
//    /// - Parameters:
//    ///   - classType: The Realm Object type used for the update.
//    ///   - version: new version in Remote Config
//    ///   - successCallback: when the new data has been saved, this block will be called
//    private fun updateNewDataFromFirebase(classType: String, version: Float, successCallback: SuccessCallback? = null) {
//        var userPrefKey = ""
//        var fileName = ""
//        when (classType) {
//            AssetEntity.TAG -> {
//                fileName = "Assets.json"
//                userPrefKey = PrefsManager.KEY_CACHE_ASSETS_DATA
//            }
//            AssetExchangeEntity.TAG -> {
//                fileName = "AssetExchanges.json"
//                userPrefKey = PrefsManager.KEY_CACHE_ASSETEXCHANGES_DATA
//            }
//            ExchangeEntity.TAG -> {
//                fileName = "Exchanges.json"
//                userPrefKey = PrefsManager.KEY_CACHE_EXCHANGES_DATA
//            }
//            else -> {
//                AppLog.d(TAG, "Not supported for updating from Firebase Storage")
//                successCallback?.invoke(false)
//                return
//            }
//        }
//        if (version <= prefsMan.getFloat(userPrefKey, 1F)) {
//            AppLog.d(TAG, "No new data from Firebase Storage, no need to update")
//            successCallback?.invoke(true)
//            return
//        }
//
//        // Download in memory with a maximum allowed size of 5MB (5 * 1024 * 1024 bytes)
//        AppLog.d(TAG, "Getting latest Json data from Firebase Storage for type: $classType")
//        firebaseStorage.reference.child(Constants.RESOURCES_FOLDER_INSTRUMENT+ "/" + fileName).getBytes(5 * 1024 * 1024).addOnCompleteListener { task ->
//            val jsonString = task.result?.toString(UTF_8) ?: ""
//            AppLog.d(TAG, "Json result from backend: $jsonString")
//            if (jsonString.isValidJsonObject()) {
//                prefsMan.putFloat(userPrefKey, version)
//                JSONObject(jsonString).optJSONArray("items")?.let { items ->
//                    var ids = items.toListJSONObject().mapNotNull { it.optString("id") }.toTypedArray()
//                    var savedIds = listOf<String>()
//                    when (classType) {
//                        AssetEntity.TAG -> {
//                            savedIds = assetDao.saveJSONDatas(items)
//                            assetDao.deleteAssetsNotIn(ids)
//                        }
//                        AssetExchangeEntity.TAG -> {
//                            savedIds = assetExchangeDao.saveJSONDatas(items)
//                            assetExchangeDao.deleteAssetExchangesNotIn(ids)
//                        }
//                        ExchangeEntity.TAG -> {
//                            savedIds = exchangeDao.saveJSONDatas(items)
//                            exchangeDao.deleteExchangesNotIn(ids)
//                        }
//                        else -> AppLog.d(TAG, "Not supported! Do nothing.")
//                    }
//                    AppLog.d(TAG, "Saved ids: $savedIds")
//                }
//            }
//            successCallback?.invoke(true)
//        }.addOnFailureListener { exception ->
//            AppLog.d(TAG, "Getting latest Json file from Firebase Storage failed due to exception: $exception")
//            successCallback?.invoke(false)
//        }
//    }
//}