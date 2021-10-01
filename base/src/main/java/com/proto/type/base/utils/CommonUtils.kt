package com.proto.type.base.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.proto.type.base.Constants
import com.proto.type.base.R
import com.proto.type.base.data.database.entity.UserEntity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.realm.Realm
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

object CommonUtils {

    // MARK: - Public Constant
    private val TAG: String = CommonUtils::class.java.simpleName

    // MARK: - Public Functions
    /**
     * @method convert bitmap to byte array to store in DB
     * @param bitmap bitmap of image
     * @return byte array of image
     */
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    /**
     * @method convert byte array to bitmap
     * @param byteArray byte array of image
     * @return bitmap is image
     */
    fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        byteArray?.let {
            return BitmapFactory.decodeByteArray(it, 0, it.size)
        }
        return null
    }

    /**
     * @method converts json to class/pojo
     * @param json to be converted
     * @param classOfT the pojo
     * @return Any the converted json class/pojo
     * */
    fun <T> getClassFromJson(json: String, classOfT: Class<T>): Any {
        val newJson = json.replace("\"extra_data\": \"\"", "\"extra_data\": {}")
        return try {
            Gson().fromJson(newJson, classOfT as Type)
        } catch (e: JsonSyntaxException) {
            AppLog.e(TAG, "Cannot get class from json with exception: $e")
            ""
        }
    }

    /**
     * @method converts json to class/pojo
     * @param json to be converted
     * @param classOfT the pojo
     * @return Any the converted json class/pojo
     * */
    fun <T> getClassFromJsonNew(json: String, classOfT: Class<T>): T {
        val newJson = json.replace("\"extra_data\": \"\"", "\"extra_data\": {}")
        return Gson().fromJson(newJson, classOfT as Type)
    }

    /**
     * @method get name of mentioned user from message string
     * @param data_value the message string
     * @return the message string with replaced names
     */
    fun getMentionNames(data_value: String, withHtml: Boolean = true): String {
        var mentionedMessage = data_value
        val splitArr = mentionedMessage.split("@")
        val realm = Realm.getDefaultInstance()
        for (user in splitArr) {
            if (user.length >= 10 /*SharedPrefs.getString(SharedPrefs.USER_ID).length*/) {
                val userId = user.substring(0, 28)
                val userName = realm.use {
                    it.where(UserEntity::class.java).equalTo(
                        "id",
                        userId
                    ).findFirst()?.local_name ?: ""
                }
                mentionedMessage = if (withHtml) {
                    mentionedMessage.replace(
                        "@$userId",
                        "<span style=\"background-color: #55999999; padding-left: 5px; padding-right: 5px; border: 2px solid gray;border-radius: 10px;\"><strong>@$userName</strong></span>"
                    )
                } else {
                    mentionedMessage.replace(
                        "@$userId",
                        "@$userName"
                    )
                }
            }
        }
        return mentionedMessage
    }

    /**
     * @method create a new image file to save edited image
     * @return File file object that contains new file path
     */
    fun getNewFile(): File {
        val fileDir = File(
            Environment.getExternalStorageDirectory().toString()
                    + File.separator + Constants.App.DIR_NAME
        )
        val fileImage = File(
            Environment.getExternalStorageDirectory().toString()
                    + File.separator + Constants.App.DIR_NAME + File.separator + System.currentTimeMillis() + ".jpg"
        )
        try {
            if (fileDir.exists().not()) fileDir.mkdirs()
            if (fileImage.exists().not()) fileImage.createNewFile()
        } catch (e: IOException) {
            AppLog.d(TAG, "Can't get new file with exception: $e")
        }
        return fileImage
    }


    /**
     * @method To hide the soft key pad if open
     * @param context context of fragment
     */
    fun hideKeyboard(context: Context) {
        val activity = context as Activity
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * @method to detect the message mentioning user
     * @param message the message string
     * @return is mentioning any user
     */
    fun isMentioning(message: String): Boolean {
        var isMentioning = false
        if (message.contains("@")) {
            val arr = message.split("@").last()
            if (arr.isNotEmpty() && arr.contains(" ").not()) {
                isMentioning = true
            }
        }
        return isMentioning
    }

    fun isMyId(userId: String): Boolean {
        return true
        //return SharedPrefs.getString(SharedPrefs.USER_ID) == userId
    }

    /**
     * @method open Browser of the mobile
     * @param context context of the current fragment
     * @param url string to be displayed at Browser
     */
    fun openBrowser(context: Context, url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }

    /**
     * @method open call dialler of the mobile
     * @param context context of the current fragment
     * @param phoneNumber string to be displayed at dialler
     */
    fun openDialler(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    }

    fun openPlayStoreForApp(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.App.APP_MARKET_URL + appPackageName)
                )
            )
        } catch (e: ActivityNotFoundException) {
            AppLog.d(TAG, "Open playstore failed with exception: $e")
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse((Constants.App.PLAY_STORE_URL + appPackageName))
                )
            )
        }
    }

    /**
     * @method open a mailer to send/report
     * @param activity context
     */
    fun shareReportMail(activity: Activity, isReport: Boolean) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(
            Intent.EXTRA_EMAIL,
            if (isReport) arrayOf(Constants.App.DEVELOPER_MAIL) else arrayOf(Constants.App.FEEDBACK_MAIL)
        )
        i.putExtra(
            Intent.EXTRA_SUBJECT,
            if (isReport) "[CHATQ-Android] Bug Reporting - " + Calendar.getInstance().time else "[CHATQ-IOS] My experience's feedback."
        )
        i.putExtra(
            Intent.EXTRA_TEXT,
            if (isReport) "Write here and tell us more about the problem you are having:" else "Write here and tell us what we can improve.\nYour feedback is important for us."
        )
        try {
            activity.startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (e: ActivityNotFoundException) {
            AppLog.d(TAG, "Start the report mail activity failed with exception: $e")
        }
    }

    /**
     * @method show invite friends dialog
     * @param activity context of dialog
     */
    fun showInviteDialog(activity: Activity) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT,
            activity.getString(R.string.invite_friend_msg)
        )
        activity.startActivity(
            Intent.createChooser(
                intent,
                activity.getString(R.string.invite_friends)
            )
        )
    }

    /**
     * @method To show the soft key pad
     * @param view view to be opened on behalf
     */
    fun showKeyboard(view: View, delay: Long = 200) {
        Handler().postDelayed({
            if (view.requestFocus()) {
                val activity = view.context as Activity
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(
                    view,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
        }, delay)
    }
}