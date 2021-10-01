package com.proto.type.contact.ui.yourqrcode

import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.data.encryption.AESDecryption
import com.proto.type.base.data.encryption.Obfuscator
import com.proto.type.contact.R
import kotlinx.android.synthetic.main.fragment_user_qrcode.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserQrCodeFragment : BaseFragment() {

    override var layoutId = R.layout.fragment_user_qrcode
    private val viewModel: UserQrCodeViewModel by viewModel()

    override fun initView() {
        super.initView()

        ivBack.setOnClickListener {
            navigateTo(R.id.back_to_contacts)
        }

        tvScanQRCode.setOnClickListener {
            navigateTo(R.id.action_scan_qr_code)
        }

        val currentUserId = viewModel.getCurrentUserId()
        if (currentUserId.isNotEmpty()) {
            val encryptedUserId = AESDecryption.encrypt(
                currentUserId,
                Obfuscator.reveal()
            )

            viewModel.generateQRCode(encryptedUserId!!)
            viewModel.qrCodeLiveData.observe(this, Observer {
                Glide.with(context!!)
                    .load(it)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivQrCode)
            })
        }
    }

}