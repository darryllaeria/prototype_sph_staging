package com.proto.type.contact.ui.yourqrcode

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.contact.util.QrCodeUtils
import kotlinx.coroutines.launch

class UserQrCodeViewModel(private val context: Context,
                          private var userRepo: IUserRepository): BaseViewModel() {

    var qrCodeLiveData = MutableLiveData<Bitmap>()

    fun getCurrentUserId(): String = userRepo.getCurrentLocalUser().id

    fun generateQRCode(data: String) {
        ioScope.launch {
            qrCodeLiveData.postValue(QrCodeUtils.generateQrCode(context.resources, data))
        }
    }
}