package com.proto.type.main.ui.signup.mobile

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.Constants
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.base_component.BaseViewModel

class SignUpMobileViewModel(private val deviceRepository: IDeviceRepository,
                            private val localRepository: ILocalRepository): BaseViewModel() {

    var countryCode = MutableLiveData<String>()

    fun getCountryCode() {
        val code = deviceRepository.getCountryCode()
        countryCode.postValue(if (code.isEmpty()) Constants.Language.DEFAULT_COUNTRY_CODE else code)
    }

    fun storePhoneNumber(phoneNumber: String) {
        localRepository.storePhoneNumber(phoneNumber)
    }
}