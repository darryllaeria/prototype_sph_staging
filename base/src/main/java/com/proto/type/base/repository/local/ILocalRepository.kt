package com.proto.type.base.repository.local

interface ILocalRepository {

    /**
     * Setup sign up mode while registering
     */
    fun setSignUpAuth()

    /**
     * Setup sign in mode while registering
     */
    fun setSignInAuth()

    /**
     * Check if current mode is sign up
     */
    fun isSignUpMode(): Boolean

    /**
     * Store email form
     */
    fun storeEmail(email: String)

    /**
     * Store password
     */
    fun storePassword(password: String)

    /**
     * Store register information for last step
     */
    fun storeFullName(firstName: String, lastName: String)

    /**
     * Store user name
     */
    fun storeUsername(username: String)

    /**
     * Store number
     */
    fun storePhoneNumber(number: String)

    /**
     * When finish register process, clear all form
     */
    fun authorized()

    fun getConfigTextSize(): Float

    fun setConfigTextSize(textSize: Float)

    fun getConfigDarkMode(): Boolean

    fun setConfigDarkMode(darkMode: Boolean)

    fun getWallpaperBackground(): String

    fun setWallpaperBackground(background: String)

    fun getShowDoodleBackground(roomId: String): Boolean

    fun setShowDoodleBackground(roomId: String, shouldShow: Boolean)

    fun getSuggestionBar(): Boolean

    fun setSuggestionBar(isSuggest: Boolean)

    fun getIsNotDisturb(): Boolean

    fun getIsShowPreview(): Boolean

    fun isUserNotification(): Boolean

    fun getUserSound(): Int

    fun isGroupNotification(): Boolean

    fun getGroupSound(): Int

    fun setDisturb(isDisturb: Boolean)

    fun setShowPreview(isShowPreview: Boolean)

    fun setUserShowNotification(isShowNotification: Boolean)

    fun setGroupShowNotification(showNotification: Boolean)
}