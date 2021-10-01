package com.proto.type.base.utils

import android.util.Log
import com.proto.type.base.BuildConfig

object AppLog {

    private const val APP_LOG_TAG: String = "ChatQ"

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            d(APP_LOG_TAG, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            d(APP_LOG_TAG, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg)
        }
    }

    fun w(msg: String) {
        if (BuildConfig.DEBUG) {
            d(APP_LOG_TAG, msg)
        }
    }

    fun printStackTrace(ex: Exception) {
        if (BuildConfig.DEBUG) {
            ex.printStackTrace()
        }
    }

    fun printStackTrace(ex: Throwable) {
        if (BuildConfig.DEBUG) {
            ex.stackTrace
        }
    }
}