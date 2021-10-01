package com.proto.type.base.repository.asset_exchange

import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import org.json.JSONArray
import org.json.JSONObject

interface IAssetExchangeRepository {

    // MARK: - Database Functions
    fun findAssetExchange(assetExchangeId: String): AssetExchangeModel?

    fun findMultipleAssetExchangesExcept(ids: Array<String>): List<AssetExchangeModel>

    fun getAllAssetExchanges(): List<AssetExchangeModel>

    fun updateAssetExchangeJson(jsonObject: JSONObject): Boolean
}