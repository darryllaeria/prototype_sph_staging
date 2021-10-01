package com.proto.type.profile.ui.sound

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.device.IDeviceRepository
import com.proto.type.base.base_component.BaseViewModel

class SoundViewModel(private val deviceRepo: IDeviceRepository): BaseViewModel() {

    var soundData = MutableLiveData<String>()

    fun loadSoundConfig() {
        soundData.postValue(deviceRepo.getSoundConfig())
    }

    fun saveSelectedSound(sound: String) {
        deviceRepo.setSoundConfig(sound)
    }
}