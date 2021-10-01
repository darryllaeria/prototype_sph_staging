package com.proto.type.ui

import android.os.Bundle
import androidx.navigation.findNavController
import com.proto.type.R
import com.proto.type.base.base_component.BaseActivity

class HomeActivity: BaseActivity() {

    // MARK: - Override Variable
    override var layoutId = R.layout.activity_home

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
    }

    override fun initLogic() {
        super.initLogic()
        findNavController(R.id.hostContainer).navigate(R.id.main_nav_graph)
    }

    override fun onBackPressed() { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
    }
}