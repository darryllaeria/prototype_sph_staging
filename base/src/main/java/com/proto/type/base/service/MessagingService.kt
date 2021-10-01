package com.proto.type.base.service

//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.proto.type.base.Constants
//import com.proto.type.base.R
//import com.proto.type.base.manager.PrefsManager
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import org.koin.android.ext.android.inject
//
//class MessagingService: FirebaseMessagingService() {
//
//    companion object {
//        const val ID = 1231
//        const val CHANNEL_ID = "981"
//        const val CHANNEL_NAME = "ChatQ"
//    }
//
//    private val prefMan: PrefsManager by inject()
//    private val notifMan: NotificationManager by lazy {
//        baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//        prefMan.putString(Constants.KEY_FCM_TOKEN, token)
//    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//
//        buildNotificationChannel()
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText(getString(R.string.txt_receive_new_message))
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setAutoCancel(true)
//            .build()
//        notifMan.notify(ID, notification)
//    }
//
//    private fun buildNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            ).apply {
//                description = getString(R.string.notification_description)
//            }
//            notifMan.createNotificationChannel(channel)
//        }
//    }
//}