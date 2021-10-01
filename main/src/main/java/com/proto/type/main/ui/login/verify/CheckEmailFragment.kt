package com.proto.type.main.ui.login.verify

import android.content.Intent
import com.proto.type.main.R
import com.proto.type.base.base_component.BaseFragment
import kotlinx.android.synthetic.main.fragment_check_email.*

class CheckEmailFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_check_email

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btContinue.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_APP_EMAIL)
            }
            startActivity(intent)
        }

        tvResend.setOnClickListener {
            showLoading()
//            val settings = ActionCodeSettings.newBuilder()
//                .setHandleCodeInApp(true)
//                .setUrl(Constants.Uri.URI_RESET_PASSWORD)
//                .setAndroidPackageName("com.proto.typestaging", true, "23")
//                .build()
//            FirebaseAuth.getInstance().sendPasswordResetEmail(arguments?.getString(Constants.KEY_EMAIL)!!, settings)
//                .addOnCompleteListener {
//                    hideLoading()
//                    if (!it.isSuccessful) {
//                        showInfo("Show error ${it.exception?.message}")
//                    }
//                }
//                .addOnFailureListener {
//                    hideLoading()
//                    showInfo("Showing error ${it.message}")
//                }
        }
    }
}