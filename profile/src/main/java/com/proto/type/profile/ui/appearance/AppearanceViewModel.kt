package com.proto.type.profile.ui.appearance

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.repository.local.ILocalRepository
import com.proto.type.base.base_component.BaseViewModel

data class AppearanceData(
    val textSize: Float,
    val darkMode: Boolean,
    val backgroundConfig: String = "",
    val suggestionBar: Boolean
)

class AppearanceViewModel(private val localRepo: ILocalRepository): BaseViewModel() {

    var appearanceConfigs = MutableLiveData<AppearanceData>()

    fun loadConfig() {
        val configs = AppearanceData(textSize = localRepo.getConfigTextSize(),
            darkMode = localRepo.getConfigDarkMode(),
            suggestionBar = localRepo.getSuggestionBar())
        appearanceConfigs.postValue(configs)
    }

    fun setTextSize(size: Float) {
        localRepo.setConfigTextSize(size)
    }

    fun setDarkMode(isDarkMode: Boolean) {
        localRepo.setConfigDarkMode(isDarkMode)
    }

    fun setSuggestionBar(isSuggestion: Boolean) {
        localRepo.setSuggestionBar(isSuggestion)
    }
}