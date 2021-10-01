package com.proto.type.base.utils

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import androidx.core.os.ConfigurationCompat
import com.proto.type.base.Constants
import com.proto.type.base.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DateTimeUtils {

    // MARK: - Companion Object
    companion object {

        // MARK: - Public Constants
        val MMMM_yyyy = "MMMM yyyy"
        val dd_MMM_yyyy_hh_mm_a = "dd MMM yyyy, hh:mm a"
        val dd_MMMM_yyyy = "dd MMMM yyyy"


        // MARK: - Public Functions
        /**
         * @method converting Android Long in milliseconds to seconds to sendReport back to server
         * @param systemMillis local Android Long in milliseconds
         * @return double value in seconds
         */
        fun getCurrentTsForIos(systemMillis: Long = System.currentTimeMillis()): Double {
            return (systemMillis / 1000).toDouble()
        }

        /**
         * @method get date string in a specified format
         * @param timestamp double value in seconds
         * @return formatted message
         */
        fun getDateString(timestamp: Double, format: String): String {
            val milliAndroid = getMillisForAndroid(timestamp)
            val formatter = SimpleDateFormat(format, ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0))
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliAndroid
            return formatter.format(calendar.time)
        }

        /**
         * @method get day of month
         * @param timestamp double value in seconds
         * @return day
         */
        fun getDayOfMonth(timeStamp: Double): String {
            val milliAndroid = getMillisForAndroid(timeStamp)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliAndroid
            return calendar.get(Calendar.DAY_OF_MONTH).toString()
        }

        /**
         * @method converting seconds to Android Long in milliseconds
         * @param timestamp double value in seconds
         * @return long value Android Long in milliseconds
         */
        fun getMillisForAndroid(timestamp: Double): Long {
            return (timestamp * 1000).toLong()
        }

        /**
         * @method get month value
         * @param timestamp double value in seconds
         * @return month
         */
        fun getMonth(timestamp: Double): Int {
            val cal = Calendar.getInstance()
            cal.timeInMillis = getMillisForAndroid(timestamp)
            return cal.get(Calendar.MONTH)
        }

        /**
         * @method get proper date string in simple format (dd MMMM yyyy)
         * @param timestamp double value in seconds
         * @return formatted message
         */
        fun getSimpleDate(timestamp: Double): String {
            val milliAndroid = getMillisForAndroid(timestamp)
            val formatter = SimpleDateFormat(dd_MMMM_yyyy, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliAndroid
            return formatter.format(calendar.time)
        }

        fun getTimeAgo(context: Context, timestamp: Double, isLastSeen: Boolean = false): String {

            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = getMillisForAndroid(timestamp)

            val days =
                TimeUnit.MILLISECONDS.toDays(abs(Calendar.getInstance().timeInMillis - calendar.timeInMillis))
            val hours =
                TimeUnit.MILLISECONDS.toHours(abs(Calendar.getInstance().timeInMillis - calendar.timeInMillis))
            val min =
                TimeUnit.MILLISECONDS.toMinutes(abs(Calendar.getInstance().timeInMillis - calendar.timeInMillis))
            val secs =
                TimeUnit.MILLISECONDS.toSeconds(abs(Calendar.getInstance().timeInMillis - calendar.timeInMillis))

            when {
                timestamp == 0.0 -> {
                    return context.getString(if (isLastSeen) R.string.last_seen_a_long_time_ago else R.string.a_long_time_ago)
                }
                days > 6 -> {
                    val simpleDateFormat =
                        SimpleDateFormat(Constants.App.DT_FORMAT_DD_MMM_YYYY, Locale.getDefault())
                    return simpleDateFormat.format(calendar.time)
                }
                days in 1..6 -> return context.getString(
                    if (isLastSeen) R.string.ls_days_ago else R.string.days_ago,
                    days
                )
                hours in 1..23 -> return context.getString(
                    if (isLastSeen) R.string.ls_hours_ago else R.string.hours_ago,
                    hours
                )
                min in 1..59 -> return context.getString(
                    if (isLastSeen) R.string.ls_min_ago else R.string.min_ago,
                    min
                )
                secs in 30..59 -> return context.getString(
                    if (isLastSeen) R.string.ls_sec_ago else R.string.sec_ago,
                    secs
                )
                else -> return context.getString(if (isLastSeen) R.string.ls_just_now else R.string.just_now)
            }
        }

        /**
         * @method get timestamp double value in seconds from a formatted string
         * @param timeString formatted time string, i.e -> 11 Jun 2020
         * @param format format of the time string above, i.e -> DD_MMM_YYYY
         * @return timestamp double value in seconds
         */
        fun getTimestampFromFormattedTimeString(timeString: String, format: String): Double {
            return try {
                val formatter = SimpleDateFormat(format, ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0))
                val date = formatter.parse(timeString)
                getCurrentTsForIos(date.time)
            } catch (e: Exception) {
                0.0
            }
        }

        /**
         * @method get year value
         * @param timestamp double value in seconds
         * @return year
         */
        fun getYear(timestamp: Double): Int {
            val cal = Calendar.getInstance()
            cal.timeInMillis = getMillisForAndroid(timestamp)
            return cal.get(Calendar.YEAR)
        }

        /**
         * @method check if two timestamps are in the same month
         * @param timestamp1 double value in seconds
         * @param timestamp2 double value in seconds
         * @return boolean value that helps indicate whether the two timestamps are in the same month
         */
        fun isInTheSameMonth(timestamp1: Double, timestamp2: Double): Boolean {
            return getMonth(timestamp1) == getMonth(timestamp2) && getYear(timestamp1) == getYear(timestamp2)
        }

        /**
         * @method check if date is today
         * @param timestamp double value in seconds
         * @return true or false
         */
        fun isToday(timestamp: Double): Boolean {
            return DateUtils.isToday(getMillisForAndroid(timestamp))
        }

        /**
         * @method get proper time unit in human readable format
         * @param timestamp double value in seconds
         * @return formatted message
         */
        fun timeAgoDisplay(context: Context, timestamp: Double, isLastSeen: Boolean = false): String {
            val millisecondsTs = getMillisForAndroid(timestamp)
            val calendarNow = Calendar.getInstance()
            val calendarTs = Calendar.getInstance()
            calendarTs.timeInMillis = millisecondsTs
            return when {
                DateUtils.isToday(millisecondsTs) -> {
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(millisecondsTs)
                }
                calendarNow.get(Calendar.WEEK_OF_YEAR) == calendarTs.get(Calendar.WEEK_OF_YEAR) -> {
                    SimpleDateFormat("EEE", Locale.getDefault()).format(millisecondsTs)
                }
                calendarNow.get(Calendar.MONTH) >= calendarTs.get(Calendar.MONTH) -> {
                    SimpleDateFormat("dd MMM", Locale.getDefault()).format(millisecondsTs)
                }
                else -> {
                    timeDateDayMonthYear(timestamp)
                }
            }
        }

        fun timeDateDayMonthYear(timestamp: Double): String {
            return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(getMillisForAndroid(timestamp))
        }

        fun timeHourMinutes(timestamp: Double): String {
            return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(getMillisForAndroid(timestamp))
        }
    }
}