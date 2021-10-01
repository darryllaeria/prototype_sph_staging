package com.proto.type.contact.ui.scanqrcode

import android.Manifest
import android.net.Uri
import androidx.lifecycle.Observer
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.contact.R
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_scan_qrcode.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanQrCodeFragment: BaseFragment(), ZXingScannerView.ResultHandler {

    override var layoutId = R.layout.fragment_scan_qrcode
    private val viewModel: ScanQrCodeViewModel by viewModel()

    override fun initView() {
        super.initView()

        ivBack.setOnClickListener {
            navigateTo(R.id.back_to_contacts)
        }

        tvShowMyQR.setOnClickListener {
            navigateTo(R.id.action_show_my_qr_code)
        }

        zScanner.setResultHandler(this)
        RuntimePermissionUtils.requestPermission(requireActivity(), listOf(Manifest.permission.CAMERA), object: RuntimePermissionCallback {
            override fun onGranted() {
                zScanner.startCamera()
            }

            override fun onFailed() {
                showInfo(getString(R.string.txt_user_denied_access_camera))
            }
        })
    }

    override fun initLogic() {
        super.initLogic()
        observeChatRoom()
    }

    override fun onPause() {
        super.onPause()
        zScanner.stopCamera()
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.chat.removeObservers(this)
    }

    override fun handleResult(rawResult: Result?) {
        val scannedText = rawResult!!.text

        viewModel.findPrivateChat(scannedText!!)
        zScanner.resumeCameraPreview(this)
    }

    private fun observeChatRoom() {
        viewModel.chat.observe(this, Observer { liveData ->
            when (liveData) {
                is UIState.LOADING -> {
                    // TODO("Show loading indicator")
                }
                is UIState.FINISHED<*> -> {
                    zScanner.stopCamera()
                    (liveData.data as? ChatModel)?.let {
                        navigateTo(Uri.parse("chatq://openChat/${it.id}/${it.name}/${it.category}"))
                    }
                }
                is UIState.FAILED -> {
                    showInfo(getString(liveData.messageId))
                }
            }
        })
    }
}