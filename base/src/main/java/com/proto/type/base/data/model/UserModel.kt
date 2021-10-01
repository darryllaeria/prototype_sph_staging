package com.proto.type.base.data.model

import android.content.Context
import com.proto.type.base.Constants.KEY_CHATQ_USER
import com.proto.type.base.utils.DateTimeUtils
import com.squareup.moshi.Json
import java.util.*

// MARK: - Object
object UserJsonKey {
    const val action = "action"
    const val avatar = "avatar"
    const val bio = "bio"
    const val blockee_id = "blockee_id"
    const val blockee_ids = "blockee_ids"
    const val blocker_ids = "blocker_ids"
    const val consent = "consent"
    const val deleted = "deleted"
    const val device_id = "device_id"
    const val display_name = "display_name"
    const val email = "email"
    const val exists = "exists"
    const val fill = "fill"
    const val first_name = "first_name"
    const val id = "id"
    const val in_contact = "in_contact"
    const val last_name = "last_name"
    const val last_online_ts = "last_online_ts"
    const val local_name = "local_name"
    const val phone_number = "phone_number"
    const val phone_numbers = "phone_numbers"
    const val privacy = "privacy"
    const val status = "status"
    const val terms = "terms"
    const val ts = "ts"
    const val user_agent = "user_agent"
    const val user_ids = "user_ids"
    const val user_key = "user_key"
    const val username = "username"
}

// MARK: - Model Data Class
data class UserModel(
    @Json(name = UserJsonKey.avatar) var avatar: AvatarModel? = null,
    @Json(name = UserJsonKey.bio) var bio: String? = "",
    @Json(name = UserJsonKey.blockee_ids) var blockee_ids: List<String> = listOf(),
    @Json(name = UserJsonKey.blocker_ids) var blocker_ids: List<String> = listOf(),
    @Json(name = UserJsonKey.display_name) var display_name: String = KEY_CHATQ_USER,
    @Json(name = UserJsonKey.email) var email: String? = "",
    @Json(name = UserJsonKey.user_key) var encryption_key: String? = "",
    @Json(name = UserJsonKey.exists) var exists: Boolean? = true,
    @Json(name = UserJsonKey.first_name) var first_name: String? = "",
    @Json(name = UserJsonKey.id) override var id: String = "",
    @Json(name = UserJsonKey.in_contact) var in_contact: Boolean? = false,
    @Json(name = UserJsonKey.last_name) var last_name: String? = "",
    @Json(name = UserJsonKey.phone_number) var phone_number: String = "",
    @Json(name = UserJsonKey.username) var username: String? = "",
    @Json(name = UserJsonKey.last_online_ts) var last_online_ts: Double = 0.0,
    @Json(name = UserJsonKey.local_name) var local_name: String? = "",
    @Transient var deleted: Boolean = false,
    @Transient var is_selected: Boolean = false,
    @Transient var is_default_user: Boolean = false,
    @Transient var privacy_consented_ts: Double = 0.0,
    @Transient override val sender_type: SenderType = SenderType.User,
    @Transient var terms_consented_ts: Double = 0.0
): Sender {

    // MARK: - Companion Object
    companion object {
        fun generateLocalUser(): UserModel {
            val user = UserModel()
            user.id = UUID.randomUUID().toString()
            return user
        }
    }

    // MARK: - Public Functions
    fun displayingName(): String {
        return if (!local_name.isNullOrEmpty() && !is_default_user)
            local_name!!
        else
            display_name ?: KEY_CHATQ_USER
    }

    fun isIdNotEmpty(): Boolean = id.isNotEmpty()

    fun lastOnlineTime(context: Context) = DateTimeUtils.getTimeAgo(context, last_online_ts, true)
}

// MARK: - Request Data Classes
data class ManageBlockeeRequest(@Json(name = UserJsonKey.blockee_id) val blockeeId: String)

data class GetProfilesRequest(@Json(name = UserJsonKey.phone_numbers) val phoneNumber: List<String>? = null,
                              @Json(name = UserJsonKey.device_id) val deviceId: String? = null,
                              @Json(name = UserJsonKey.user_ids) val userIds: List<String>? = null,
                              @Json(name = UserJsonKey.fill) val shouldFill: Boolean? = null)

data class UserDeleteRequest(@Json(name = UserJsonKey.user_agent) val userAgent: String)

data class UserRegisterRequest(@Json(name = UserJsonKey.phone_number) val number: String,
                               @Json(name = UserJsonKey.avatar) val avatar: String,
                               @Json(name = UserJsonKey.last_name) val lastName: String,
                               @Json(name = UserJsonKey.display_name) val displayName: String,
                               @Json(name = UserJsonKey.email) val email: String,
                               @Json(name = UserJsonKey.username) val username: String? = null)

data class UserReportRequest(@Json(name = "reason_description") val reasonContent: String,
                             @Json(name = "reason_type") val reasonType: String,
                             @Json(name = "target_category") val targetCategory: String,
                             @Json(name = "target_id") val targetId: String)

data class UserUpdateRequest(@Json(name = UserJsonKey.avatar) val avatar: AvatarModel? = null,
                             @Json(name = UserJsonKey.username) val userName: String? = null,
                             @Json(name = UserJsonKey.bio) val bio: String? = null,
                             @Json(name = UserJsonKey.last_name) val lastName: String? = null,
                             @Json(name = UserJsonKey.first_name) val firstName: String? = null,
                             @Json(name = UserJsonKey.display_name) val displayName: String? = null,
                             @Json(name = UserJsonKey.email) val email: String? = null)

interface Sender {
    val id: String
    val sender_type: SenderType
}

enum class SenderType(val type: String) {
    BotInstance("bot"),
    System("system"),
    User("user")
}

// MARK: - Response Data Class
data class UserExistResponse(val status: Boolean)
