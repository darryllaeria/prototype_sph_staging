package com.proto.type.contact.ui.search

import androidx.lifecycle.MutableLiveData
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.contact.adapter.MarketData

class SearchAllViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    var people = MutableLiveData<List<UserModel>>()
    var symbols = MutableLiveData<List<MarketData>>()

    fun loadSuggestData() {
        val users = userRepo.getLocalSuggestionUsers().apply {
            sortedBy { it.display_name }
        }
        people.postValue(users)

        val mockData = listOf(
            MarketData("Bitcoin", "JPY", "BTC_JPY", 1126.200, 4.25),
            MarketData("Bitcoin", "USD", "BTC_USD", 10126.200, 3.25),
            MarketData("Bitcoin Cash", "USD", "BCH_USD", 196.200, 7.25)
        ).apply {
            sortedBy { it.cryptoName }
        }
        symbols.postValue(mockData)
    }
}