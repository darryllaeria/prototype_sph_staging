package com.proto.type.base

import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.data.model.market_data.AssetExchangeModel

interface ChatMessagesRecipient {
    fun didReceiveNewMessage(message: MessageModel)
}

interface MarketDataRecipient {
    fun didReceiveNewMessage(assetExchange: AssetExchangeModel)
}

interface MQTTRecipient {
    fun didReceiveNewMessage(topic: String, payloadDictionary: JSONMutableMap, payloadString: String)
    fun didReceiveTimeout(topic: String)
}

interface Sender {
    val id: String
    val sender_type: SenderType
}

interface TextSuggestion {
    var id: String
    var name: String
}
