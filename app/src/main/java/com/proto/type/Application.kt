package com.proto.type

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.proto.type.base.Constants
import com.proto.type.base.extension.applyLocale
import com.proto.type.base.manager.PrefsManager
import com.proto.type.base.data.database.RealmDB
import com.proto.type.base.data.encryption.KeyStoreWrapper
import com.proto.type.base.utils.AppLog
import io.realm.Realm
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * @Details application class for project
 */

class Application : Application(), LifecycleObserver {

    // MARK: - Companion Object
    companion object {
        private val TAG = Application::class.java.simpleName
    }

    // MARK: - Private Constants
    private val keyStore: KeyStoreWrapper by inject()
    private val prefMan: PrefsManager by inject()

    // MARK: - Application level lifecycle events
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnteredForeground() {
        AppLog.d(TAG, "Application did enter foreground")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnteredBackground() {
        AppLog.d(TAG, "Application did enter background")
    }

    // MARK: - Override Functions
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base.applyLocale())
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        applyLocale()
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        super.onCreate()
        applyLocale()
        initDependencies()
        setupRealm()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // MARK: - Private Functions
    private fun initDependencies() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@Application)
            modules(getModule())
        }
    }

    private fun setupRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmDB.getConfig(prefMan, keyStore))
        AppLog.d(TAG, "Realm path: " + Realm.getDefaultInstance().path)
    }
}