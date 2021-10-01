package com.proto.type.base.data.model

// MARK: - Common Data Classes
data class MqttApiErrorResponse(
    val code: Int,
    val exception: String,
    val message: String,
    val status: String
)

data class SignUpForm(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val phoneNumber: String
)

data class Response(
    val status: String
)
