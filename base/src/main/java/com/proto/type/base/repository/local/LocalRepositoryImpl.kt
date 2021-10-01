package com.proto.type.base.repository.local

import android.content.Context
import com.proto.type.base.Constants
import com.proto.type.base.manager.PrefsManager

class LocalRepositoryImpl(context: Context): ILocalRepository {

    private val prefsManager: PrefsManager =
        PrefsManager(context)

    override fun setSignUpAuth() {
        prefsManager.putString(PrefsManager.KEY_AUTH_MODE, PrefsManager.SIGNUP_MODE)
        prefsManager.deleteKey(PrefsManager.KEY_REG_EMAIL)
        prefsManager.deleteKey(PrefsManager.KEY_REG_FIRST_NAME)
        prefsManager.deleteKey(PrefsManager.KEY_REG_LAST_NAME)
        prefsManager.deleteKey(PrefsManager.KEY_REG_NUMBER)
        prefsManager.deleteKey(PrefsManager.KEY_REG_PWD)
        prefsManager.deleteKey(PrefsManager.KEY_REG_USR_NAME)
    }

    override fun setSignInAuth() {
        prefsManager.putString(PrefsManager.KEY_AUTH_MODE, PrefsManager.SIGNIN_MODE)
    }

    override fun isSignUpMode(): Boolean {
        return prefsManager.getString(PrefsManager.KEY_AUTH_MODE, PrefsManager.SIGNUP_MODE) == PrefsManager.SIGNUP_MODE
    }

    override fun storeEmail(email: String) {
        prefsManager.putString(PrefsManager.KEY_REG_EMAIL, email)
    }

    override fun storePassword(password: String) {
        prefsManager.putString(PrefsManager.KEY_REG_PWD, password)
    }

    override fun storeFullName(firstName: String, lastName: String) {
        prefsManager.putString(PrefsManager.KEY_REG_FIRST_NAME, firstName)
        prefsManager.putString(PrefsManager.KEY_REG_LAST_NAME, lastName)
    }

    override fun storeUsername(username: String) {
        prefsManager.putString(PrefsManager.KEY_REG_USR_NAME, username)
    }

    override fun storePhoneNumber(number: String) {
        prefsManager.putString(PrefsManager.KEY_REG_NUMBER, number)
    }

    override fun authorized() {
        prefsManager.deleteKey(PrefsManager.KEY_AUTH_MODE)
        prefsManager.deleteKey(PrefsManager.KEY_REG_FIRST_NAME)
        prefsManager.deleteKey(PrefsManager.KEY_REG_LAST_NAME)
    }

    override fun getConfigTextSize(): Float {
        return prefsManager.getFloat(Constants.Appearance.SETTINGS_TEXT_SIZE, Constants.Appearance.SETTINGS_MIN_TEXT_SIZE)
    }

    override fun getConfigDarkMode(): Boolean {
        return prefsManager.getBool(Constants.Appearance.SETTINGS_DARK_MODE, false)
    }

    override fun getGroupSound(): Int {
        return 0
    }

    override fun getIsNotDisturb(): Boolean {
        return prefsManager.getBool(Constants.Notification.SETTINGS_DO_NOT_DISTURB, false)
    }

    override fun getIsShowPreview(): Boolean {
        return prefsManager.getBool(Constants.Appearance.SETTINGS_SHOW_PREVIEW, false)
    }

    override fun getShowDoodleBackground(roomId: String): Boolean {
        return prefsManager.getBool(PrefsManager.KEY_SHOULD_SHOW_DOOBLE_BACKGROUND + "_" + roomId, true)
    }

    override fun getSuggestionBar(): Boolean {
        return prefsManager.getBool(Constants.Appearance.SETTINGS_SUGGESTION_BAR, false)
    }

    override fun getUserSound(): Int {
        return 0
    }

    override fun isGroupNotification(): Boolean {
        return prefsManager.getBool(Constants.Notification.SETTINGS_GROUP_NOTIFICATION, false)
    }

    override fun isUserNotification(): Boolean {
        return prefsManager.getBool(Constants.Notification.SETTINGS_USER_NOTIFICATION, false)
    }

    override fun setConfigDarkMode(isDarkMode: Boolean) {
        prefsManager.putBool(Constants.Appearance.SETTINGS_DARK_MODE, isDarkMode)
    }

    override fun setConfigTextSize(textSize: Float) {
        prefsManager.putFloat(Constants.Appearance.SETTINGS_TEXT_SIZE, textSize)
    }

    override fun setDisturb(isDisturb: Boolean) {
        prefsManager.putBool(Constants.Notification.SETTINGS_DO_NOT_DISTURB, isDisturb)
    }

    override fun setShowDoodleBackground(roomId: String, shouldShow: Boolean) {
        prefsManager.putBool(PrefsManager.KEY_SHOULD_SHOW_DOOBLE_BACKGROUND + "_" + roomId, shouldShow)
    }

    override fun setShowPreview(isShowPreview: Boolean) {
        prefsManager.putBool(Constants.Appearance.SETTINGS_SHOW_PREVIEW, isShowPreview)
    }

    override fun setUserShowNotification(isShowNotification: Boolean) {
        prefsManager.putBool(Constants.Notification.SETTINGS_USER_NOTIFICATION, isShowNotification)
    }

    override fun setGroupShowNotification(isShowNotification: Boolean) {
        prefsManager.putBool(Constants.Notification.SETTINGS_GROUP_NOTIFICATION, isShowNotification)
    }

    override fun getWallpaperBackground(): String {
        return prefsManager.getString(Constants.Appearance.SETTINGS_BACKGROUND, "")
    }

    override fun setWallpaperBackground(background: String) {
        prefsManager.putString(Constants.Appearance.SETTINGS_BACKGROUND, background)
    }

    override fun setSuggestionBar(isSuggest: Boolean) {
        prefsManager.putBool(Constants.Appearance.SETTINGS_SUGGESTION_BAR, isSuggest)
    }
}