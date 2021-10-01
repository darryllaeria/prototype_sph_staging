package com.proto.type.base.repository.asset_exchange

import com.proto.type.base.data.database.dao.market_data.AssetExchangeDao
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.data.model.market_data.AssetExchangeModel
import com.proto.type.base.repository.message.MessageRepositoryImpl
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.runBlocking
import org.json.JSONObject


class AssetExchangeRepositoryImpl(
    private val assetExchangeDao: AssetExchangeDao
): IAssetExchangeRepository {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = AssetExchangeRepositoryImpl::class.java.simpleName
    }

    // MARK: - Database Functions
    override fun findAssetExchange(assetExchangeId: String): AssetExchangeModel? {
        return runBlocking {
            assetExchangeDao.findAssetExchange(assetExchangeId)
        }
    }

    override fun findMultipleAssetExchangesExcept(ids: Array<String>): List<AssetExchangeModel> {
        return runBlocking {
            assetExchangeDao.findMultipleAssetExchangesExcept(ids)
        }
    }

    override fun getAllAssetExchanges(): List<AssetExchangeModel> {
        return runBlocking {
            assetExchangeDao.getAllAssetExchanges()
        }
    }

    override fun updateAssetExchangeJson(jsonObject: JSONObject): Boolean {
        return try {
            runBlocking {
                assetExchangeDao.updateJSONData(jsonObject) != null
            }
        } catch (e: Exception) {
            AppLog.d(TAG, "Store user failed with exception: $e")
            false
        }
    }
}