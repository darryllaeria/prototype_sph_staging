package com.proto.type.base

object Constants {

    const val DB_NAME = "chatq.realm"
    const val DEFAULT_MESSAGE_ID = -1
    const val GMAIL_PACKAGE = "com.google.android.gm"
    const val GMAIL_CONVERSATION = "com.google.android.gm.ConversationListActivity"
    const val KEY_EMAIL = "email"
    const val KEY_FIRST_TIME: String = "is_first_time"
    const val KEY_IV_DB = "iv_db"
    const val KEY_PHONE = "key_phone"
    const val KEY_VERSION_REALM_SCHEMA = "realm_schema_version"
    const val KEY_SECRET_DB = "secret_db"
    const val MAX_COUNT = 20
    const val MAX_USERNAME = 20
    const val MIN_USERNAME = 5
    const val PRE_EXPIRED_TIME = 1_500L
    const val STRING_DASH = "-"
    const val DEFAULT_CURRENCY = "$"

    // OTP
    const val KEY_OTP_TYPE = "otp_type"
    const val RECENT_CHAT = "recent_chat"
    const val RECENT_CHAT_TITLE = "recent_chat_title"
    const val MOBILE_NUMBER = "mobile_number"
    const val ROOM_ID = "user_id"
    const val STATUS = "status"
    const val DATA = "dataModule"
    const val ITEMS = "items"
    const val CLIP_BOARD = "clip_board_chatq"

    // Error
    object ErrorString {
        const val ERROR_NONE = "error_none"
        const val ERROR_UNABLE_SAVE_LOCAL = "error_unable_save_local"
        const val ERROR_SERVER_ISSUE = "error_server_issue"
        const val ERROR_USERNAME_NOT_EXIST = "error_username_not_exist"
    }

    // App
    object App {
        const val CLICK_DISABLE_DURATION = 1000L
        const val DT_FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy"
        const val DIR_NAME = "ChatQ_Images"
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id="
        const val DEVELOPER_MAIL = "developers@chatq.sg"
        const val FEEDBACK_MAIL = "feedback@chatq.sg"
        const val APP_MARKET_URL = "market://details?id="
        const val VIEW_ANIMATION_DURATION = 300L
    }

    // Firebase
    const val RESOURCES_FOLDER_INSTRUMENT = "InstrumentResources"

    // Profile Settings
    object Appearance {
        const val SETTINGS_TEXT_SIZE = "textsize"
        const val SETTINGS_MIN_TEXT_SIZE = 13f
        const val SETTINGS_MAX_TEXT_SIZE = 20f
        const val SETTINGS_DARK_MODE = "mode"
        const val SETTINGS_SUGGESTION_BAR = "suggesstion_bar"
        const val SETTINGS_BACKGROUND = "settings_background"
        const val SETTINGS_SHOW_PREVIEW = "settings_show_preview"
    }

    // Encryption
    object Encryption {
        const val AES_KEY_SIZE = 32
        const val AES_IV_SIZE = 16
        const val DATA_TYPE_PRESENTATION_SIZE = 2
        const val REQUEST_ID_SIZE = 1
    }

    // Language code
    object Language {
        const val KEY_SETTINGS_LANGUAGE = "key_language"
        const val LANG_CODE_DEFAULT = "en"
        const val LANG_CODE_THAI = "th"
        const val LANG_CODE_JAPANESE = "ja"
        const val LANG_CODE_VIETNAMESE = "vi"
        const val LANG_CODE_ENGLISH = "en"
        const val DEFAULT_COUNTRY_CODE = "en"
    }

    // MQTT
    object MQTT {
        const val MQTT_MESSAGE_ENDPOINT = BuildConfig.MQTT_BASE_URL + ":" + BuildConfig.MQTT_PORT
        const val MQTT_MESSAGE_USER_NAME = BuildConfig.MQTT_USER_NAME
        const val MQTT_MESSAGE_PASSWORD = BuildConfig.MQTT_PASSWORD
        const val MQTT_MARKET_DATA_ENDPOINT = BuildConfig.MQTT_MK_BASE_URL + ":" + BuildConfig.MQTT_PORT
        const val MQTT_MARKET_DATA_USER_NAME = BuildConfig.MQTT_MK_USER_NAME
        const val MQTT_MARKET_DATA_PASSWORD = BuildConfig.MQTT_MK_PASSWORD
        const val INTERVAL_KEEP_ALIVE = 60
    }

    // Notification & Sound
    object Notification {
        const val KEY_SETTINGS_SOUND = "key_sound"
        const val DEFAULT_SOUND = "none"
        const val SETTINGS_DO_NOT_DISTURB = "settings_disturb"
        const val SETTINGS_USER_NOTIFICATION = "settings_user_notification"
        const val SETTINGS_GROUP_NOTIFICATION = "settings_group_notification"
    }

    // Feed back emails
    const val FEEDBACK_MAIL = "feedback@chatq.sg"
    const val DEVELOPER_MAIL = "developers@chatq.sg"

    const val PASSWORD_LENGTH = 6

    const val SECOND = 1_000L
    const val DELAY_WAITING_OTP = 30_000L
    const val TIME_OUT = 3_000
    const val GOOGLE_ADDRESS = "www.google.com"
    const val PLUS_SIGN = "+"
    const val DELAY_KEYBOARD = 100L
    const val ANIMATE_DOT = 150L
    const val DELAY_TIME = 2_500L
    const val DELAY_OTP_TIME = 60L

    const val KEY_IS_LOGGED_IN = "is_login"
    const val KEY_AUTH_KEY = "auth_key"
    const val KEY_ENC_KEY = "enc_key"
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_FCM_TOKEN = "fcm_token"
    const val KEY_TOKEN_EXPIRATION = "token_expiration"
    const val KEY_USER_ID = "user_id"

    // Authentication
    const val AUTH_USERNAME_MIN = 5
    const val AUTH_USERNAME_MAX = 20

    // Chat
    const val CHAT_CATEGORY_BOT_DASHBOARD = "bot_dashboard"
    const val CHAT_CATEGORY_DASHBOARD = "user_bot_dashboard"
    const val CHAT_CATEGORY_GROUP = "group"
    const val CHAT_CATEGORY_PRIVATE = "private"
    const val CHAT_GROUP_NAME_MAX_LENGTH = 20
    const val KEY_CHATQ_USER = "ChatQ User"
    const val KEY_LINK_URL = "link_url"
    const val KEY_OPEN_ROOM_ID = "open_room_id"
    const val KEY_ROOM_AVATAR = "room_avatar"
    const val KEY_ROOM_ID = "room_id"
    const val KEY_MESSAGE_ACTION_ID = "message_action_id"
    const val KEY_ROOM_NAME = "room_name"
    const val KEY_PARTICIPANT_ID = "participant_id"
    const val KEY_IS_SELF_CHAT = "is_self_chat"
    const val KEY_IS_ADD_FRIEND = "is_add_friend"
    const val KEY_CHAT_PARTICIPANTS_IDS = "participant_ids"
    const val KEY_BACK_NAV_DESTINATION = "back_nav_destination"

    // Contact
    const val CONTACT_PERMISSION_SHOW = "contact_permission"

    // Pagination states
    val INITIAL_MORE_DATA = 0
    val INITIAL_MORE_DATA_AND_MERGE = 1
    val INITIAL_NO_MORE_DATA = 2
    val LOADMORE_MORE_DATA = 3
    val LOADMORE_NO_MORE_DATA = 4
    val UPDATE_DATA_ITEM = 5

    // Uri
    object Uri {
        const val URI_CHAT = "chatq://chat"
        const val URI_CONTACTS = "chatq://contacts"
        const val URI_INBOX = "chatq://inbox"
        const val URI_LOGIN = "chatq://login"
        const val URI_NEW_CHAT = "chatq://openChat"
        const val URI_ONBOARD = "chatq://onboard"
        const val URI_PROFILE = "chatq://profile"
        const val URI_RESET = "chatq://resetpassword?code="
        const val URI_RESET_PASSWORD = "https://chatq-staging.firebaseapp.com/password/reset"
        const val URI_VERIFIED_EMAIL = "https://chatq-staging.firebaseapp.com/email/verified"
    }

    // Chat/Message
    object Message {
        const val CHAT_PAGE_SIZE = 40
        const val CHAT_PRE_DISTANCE_SIZE = 30
        const val ITEM_PAGE_SIZE = 20
        const val PAGE_SIZE = 40
        const val ROOM_NEW = "new_room"
        const val CHAT_SENDER_USER = "User"
        const val CHAT_SENDER_BOT = "Bot"
    }

    // Inbox Search
    const val SEARCH_ROOM_SIZE_KEY = "roomSize"
    const val SEARCH_TYPE_ALL = "all"
    const val SEARCH_TYPE_SYMBOL = "symbol"
    const val SEARCH_TYPE_PEOPLE = "people"
    const val SEARCH_TYPE_GROUPS = "groups"
    const val SEARCH_TYPE_MESSAGES = "messages"

    // Avatar initials
    const val AVATAR_INITIALS_DEFAULT = 2

    // Report
    object Report {
        const val MAX_REPORT_CONTENT_CHARS_COUNT = 500
        const val KEY_REPORT_REASON = "report_reason"
        const val KEY_REPORT_TARGET = "report_target"
        const val KEY_REPORT_TARGET_ID = "report_target_id"
    }

    object Request {
        const val REQUEST_CODE_CHOOSE = 100
        const val REQUEST_IMAGE_CAPTURE = 101
    }
}