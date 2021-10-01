//package com.proto.type
//
//import com.facebook.stetho.Stetho
//import com.uphyca.stetho_realm.RealmInspectorModulesProvider
//
///**
// * @Details A application instance for debug mode
// * @Author Ranosys Technologies
// * @Date 09-Oct-2019
// */
//
//class ChatQDebugApplication : ChatQApplication() {
//
//    override fun onCreate() {
//        super.onCreate()
//        setupStetho()
//    }
//
//    private fun setupStetho() {
//        Stetho.initialize(
//            Stetho.newInitializerBuilder(this)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
//                .build()
//        )
//    }
//}