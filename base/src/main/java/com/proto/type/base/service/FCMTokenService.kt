package com.proto.type.base.service

//import com.proto.type.base.Constants
//import com.proto.type.base.manager.PrefsManager
//import com.google.firebase.messaging.FirebaseMessagingService
//import org.koin.android.ext.android.inject
//
//class FCMTokenService : FirebaseMessagingService() {
//
//    private val prefMan: PrefsManager by inject()
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//
//        prefMan.putString(Constants.KEY_FCM_TOKEN, token)
//    }
//
//}