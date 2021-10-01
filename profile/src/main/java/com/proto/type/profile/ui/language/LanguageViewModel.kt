package com.proto.type.profile.ui.language

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.base_component.BaseViewModel

class LanguageViewModel(private val deviceRepo: IDeviceRepository): BaseViewModel() {

    var uiModel = MutableLiveData<String>()

    fun loadLanguageConfig() {
        uiModel.postValue(deviceRepo.getLanguageConfig())
    }

    fun saveConfigLanguage(languageCode: String) {
        uiModel.postValue(languageCode)
        deviceRepo.setLanguageCode(languageCode)
    }
}