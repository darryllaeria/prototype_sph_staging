package com.proto.type.profile.ui.phone.changenumber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.base_component.BaseViewModel

class NewPhoneViewModel(private val deviceRepo: IDeviceRepository): BaseViewModel() {

    fun getCountryCode(): LiveData<String> {
        return MutableLiveData(deviceRepo.getCountryCode())
    }


}