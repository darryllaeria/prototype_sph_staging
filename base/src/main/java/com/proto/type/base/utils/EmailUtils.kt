package com.proto.type.base.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
import com.proto.type.base.BuildConfig

object EmailUtils {

    fun sendEmail(activity: Activity, subject: String, title: String) {
        ShareCompat.IntentBuilder.from(activity)
            .setType("message/rfc822")
            .addEmailTo(BuildConfig.APP_MAIL)
            .setSubject(subject)
            .setChooserTitle(title)
            .startChooser()
    }

}