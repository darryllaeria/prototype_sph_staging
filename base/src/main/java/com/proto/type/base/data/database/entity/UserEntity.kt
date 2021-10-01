package com.proto.type.base.data.database.entity

import com.proto.type.base.Constants
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// MARK: - Object
object UserEntityKey {
    const val avatar = "avatar"
    const val bio = "bio"
    const val blockee_ids = "blockee_ids"
    const val blocker_ids = "blocker_ids"
    const val deleted = "deleted"
    const val display_name = "display_name"
    const val email = "email"
    const val enc_key = "enc_key"
    const val first_name = "first_name"
    const val id = "id"
    const val in_contact = "in_contact"
    const val is_default_user = "is_default_user"
    const val last_name = "last_name"
    const val local_name = "local_name"
    const val phone_number = "phone_number"
    const val privacy_consented_ts = "privacy_consented_ts"
    const val terms_consented_ts = "terms_consented_ts"
    const val username = "username"
}

// MARK: - Open Class
open class UserEntity(
    var avatar: AvatarEntity? = null,
    var bio: String = "",
    var blockee_ids: RealmList<String> = RealmList(),
    var blocker_ids: RealmList<String> = RealmList(),
    var deleted: Boolean = false,
    var display_name: String = Constants.KEY_CHATQ_USER,
    var email: String = "",
    var enc_key: String = "",
    var first_name: String = "",
    @PrimaryKey var id: String = "",
    var in_contact: Boolean = false,
    var is_chatq_user: Boolean = true,
    var is_default_user: Boolean = false,
    var last_name: String = "",
    var local_name: String = "",
    var last_online_ts: Double = 0.0,
    var phone_number: String = "",
    var privacy_consented_ts: Double = 0.0,
    var terms_consented_ts: Double = 0.0,
    var status: String = "",
    var username: String = ""
) : RealmObject() {
    // MARK: - Companion Object
    companion object {
        val TAG: String = UserEntity::class.java.simpleName
    }

    // MARK: - Constructor Function
    constructor() : this(id = "")
}