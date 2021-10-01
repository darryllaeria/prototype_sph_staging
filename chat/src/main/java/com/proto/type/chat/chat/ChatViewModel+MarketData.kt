package com.proto.type.chat.chat
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.extension.findSuggestionText

// MARK: - ChatViewModel + Market Data
fun ChatViewModel.addLatestPriceSubscriptionIfAny() {
    selectedInstrument?.let {
        marketDataManager.listenToLastPrice(it.id, it.realtimePeriod(), this)
    }
}

fun ChatViewModel.cleanupLatestPriceSubscriptionIfAny() {
    selectedInstrument?.let {
        marketDataManager.stopListenToLastPrice(it.id, it.realtimePeriod(), this)
    }
}

fun ChatViewModel.findAssetExchanges() {
    val ignoringIds = listOf("RFQ_OTC", "AAPL.O")
    allSuggestions.postValue(assetExchangeRepo.findMultipleAssetExchangesExcept(ignoringIds.toTypedArray()))
}

fun ChatViewModel.findSuggestions(text: String) {
    if (text.isEmpty())
        displaySuggestions.postValue(listOf())
    else {
        val suggestionText = text.findSuggestionText()
        if (suggestionText.isNotEmpty()) {
            displaySuggestions.postValue(allSuggestions.value?.filter {
                it.id.contains(suggestionText, true) ||
                        it.name.contains(suggestionText, true)
            } ?: listOf())
        } else
            displaySuggestions.postValue(listOf())
    }
}

fun ChatViewModel.updateAndObserveSelectedSuggestion(id: String) {
    allSuggestions.value?.firstOrNull { it.id == id }?.let { suggestion ->
        cleanupLatestPriceSubscriptionIfAny()
        if (suggestion is AssetExchangeModel) {
            selectedInstrument = suggestion
            marketDataManager.listenToLastPrice(suggestion.id, suggestion.realtimePeriod(), this)
        } else {
            selectedInstrument = null
        }
    }
}