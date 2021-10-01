package com.proto.type.ui

import android.os.Bundle
import android.os.Messenger
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.proto.type.R
import com.proto.type.base.base_component.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity: BaseActivity() {

    // MARK: - Override Variable
    override var layoutId = R.layout.activity_home

    // MARK: - Private Variables
    private var isBound = false
    private var serverMessenger: Messenger? = null

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
    }

    override fun initLogic() {
        super.initLogic()
        findNavController(R.id.hostContainer).navigate(R.id.auth_nav_graph)
    }

    override fun onBackPressed() { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            isBound = false
        }
    }
}