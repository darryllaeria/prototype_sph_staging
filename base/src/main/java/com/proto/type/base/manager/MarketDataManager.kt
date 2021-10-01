package com.proto.type.base.manager

import com.proto.type.base.*
import com.proto.type.base.data.model.market_data.AssetExchangeJsonKey
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.repository.asset_exchange.IAssetExchangeRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class MarketDataManager(
    private val assetExChangeRepo: IAssetExchangeRepository
//    private val mqttManager: MQTTManager
): MQTTRecipient {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = MarketDataManager::class.java.simpleName
    }

    // MARK: - Private Constants
//    private val marketDataMQTTClient = mqttManager.marketDataMQTTClient
    private val recipientsData: MutableMap<String, MutableList<MarketDataRecipient>> = mutableMapOf()
    private val subscribedIds: MutableMap<String, String> = mutableMapOf()

    // MARK: - Override Functions
    override fun didReceiveNewMessage(
        topic: String,
        payloadDictionary: JSONMutableMap,
        payloadString: String
    ) {
        AppLog.d(TAG, "topic: $topic, payload string: $payloadString, payload map: $payloadDictionary")
        var jsonObject = JSONObject(payloadDictionary)
        subscribedIds[topic]?.let {
            jsonObject.put(AssetExchangeJsonKey.id, it)
        }
        val success = assetExChangeRepo.updateAssetExchangeJson(jsonObject)
        if (success) {
            (jsonObject[AssetExchangeJsonKey.id] as? String)?.let {
                runBlocking {
                    val assetExchange = assetExChangeRepo.findAssetExchange(it)
                    if (assetExchange != null)
                        notifyRecipient(topic, assetExchange)
                    else
                        AppLog.d(TAG, "Notify recipients failed due to unable to find saved AssetExchange from Realm.")
                }
            } ?: run {
                AppLog.d(TAG, "Notify recipients failed due to no assetExchange id: $payloadString")
            }
        } else {
            AppLog.d(TAG, "Notify recipients failed due to unable to save AssetExchange Json response: $payloadString")
        }
    }

    override fun didReceiveTimeout(topic: String) {
        // TODO("Handle when receive a timeout alert during subscription")
    }

    // MARK: - Public Functions
    fun listenToLastPrice(assetExchangeId: String, assetExchangePeriod: String, recipient: MarketDataRecipient) {
//        val topic = MQTTUrls.latestPrice(assetExchangeId, assetExchangePeriod)
//        recipientsData[topic]?.let {
//            if (it.isEmpty()) {
//                marketDataMQTTClient.subscribe(topic = topic, subscriber = this)
//            }
//            if (!it.contains(recipient)) {
//                it.add(recipient)
//            }
//        } ?: run {
//            marketDataMQTTClient.subscribe(topic = topic, subscriber = this)
//            recipientsData[topic] = mutableListOf(recipient)
//            subscribedIds[topic] = assetExchangeId
//        }
    }

    fun stopListenToLastPrice(assetExchangeId: String, assetExchangePeriod: String, recipient: MarketDataRecipient) {
//        val topic = MQTTUrls.latestPrice(assetExchangeId, assetExchangePeriod)
//        recipientsData[topic]?.let {
//            if (it.isNotEmpty() && it.contains(recipient)) {
//                it.remove(recipient)
//                if (it.isEmpty()) {
//                    marketDataMQTTClient.unsubscribe(topic)
//                }
//            }
//        }
    }

    // MARK: - Private Function
    private fun notifyRecipient(topic: String, assetExchange: AssetExchangeModel) {
        recipientsData[topic]?.forEach {
            it.didReceiveNewMessage(assetExchange)
        }
    }
}