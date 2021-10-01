package com.proto.type.base.data.encryption.payload_encryption

/**
 * @Details A class for defining all possible payload message types
 * @Author Ranosys Technologies
 * @Date 21-Oct-2019
 */
object EncryptionType {
    // Request types
    const val EncryptionWithCorrelIDAndReqIDAndVersion = "2c8c"

    // Response types
    const val EncryptionOnly = "2c86"
    const val EncryptionNone = "2c87"
    const val EncryptionOnlySymmetric = "2c89"
    const val EncryptionWithReqID = "2c8d"
}