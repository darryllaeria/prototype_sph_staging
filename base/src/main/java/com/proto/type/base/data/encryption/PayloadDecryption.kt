package com.proto.type.base.data.encryption

import com.proto.type.base.data.encryption.payload_encryption.Base64
import com.proto.type.base.utils.AppLog

object PayloadDecryption {

    // MARK: - Public Constant
    private val TAG: String = PayloadDecryption::class.java.simpleName

    // MARK: - Public Function
    @ExperimentalUnsignedTypes
    fun decryptPayload(payload: ByteArray, secretKey: String): String {
        return try {
            val data = payload.toUByteArray()
            val prefix = data.copyOfRange(0, 2).joinToString("") { it.toString(16) }
            val iv = data.copyOfRange(2, 18).joinToString("") { it.toString(16) }
            val md5 = data.copyOfRange(18, 34).joinToString("") { it.toString(16) }
            val msg = data.copyOfRange(34, payload.size).joinToString("") { it.toString(16) }
            AppLog.d(TAG, "About to decrypt payload with prefix: $prefix - IV: $iv - Secret: ${Base64.decode(secretKey)} - Md5: $md5 - Msg: $msg")
            // TODO("Implement decrypt payload feature")
            ""
        } catch (e: Exception) {
            AppLog.d(TAG, "Decrypt payload failed with exception: e")
            ""
        }
    }

}