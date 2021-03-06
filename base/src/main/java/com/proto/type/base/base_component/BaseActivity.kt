package com.proto.type.base.base_component

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Messenger
import androidx.appcompat.app.AppCompatActivity
import com.proto.type.base.R
import com.proto.type.base.extension.applyLocale
import com.proto.type.base.extension.hasInternetConnection
import com.novoda.merlin.Merlin
import com.tapadoo.alerter.Alerter

abstract class BaseActivity : AppCompatActivity() {

    abstract var layoutId: Int
    private val merlin by lazy {
        Merlin.Builder()
            .withConnectableCallbacks()
            .withDisconnectableCallbacks()
            .build(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        initView()
    }

    override fun onResume() {
        super.onResume()
        initEvent()
        initLogic()
    }

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context?.applyLocale())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applicationContext.applyLocale()
    }

    override fun onStop() {
        super.onStop()
        merlin.unbind()
    }

    open fun initView() {
    }

    open fun initEvent() {
        if (!hasInternetConnection()) {
            handleConnectionOff()
        }
        with(merlin) {
            bind()
            registerConnectable {
                handleConnectionBack()
            }
            registerDisconnectable {
                handleConnectionOff()
            }
        }
    }

    open fun initLogic() {}

    /**
     * @method Handle case connection gone
     */
    open fun handleConnectionOff() {
        Alerter.create(this, R.layout.item_no_network)
            .setDismissable(false)
            .enableInfiniteDuration(true)
            .show()
    }

    /**
     * @method Handle connection back
     */
    open fun handleConnectionBack() {
        Alerter.clearCurrent(this)
    }
}