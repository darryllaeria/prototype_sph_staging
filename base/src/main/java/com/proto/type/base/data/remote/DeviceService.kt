package com.proto.type.base.data.remote

import com.proto.type.base.data.model.DeleteDeviceRequest
import com.proto.type.base.data.model.RegisterDeviceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface DeviceService {

    @POST("/v1/users/devices")
    suspend fun registerDevice(@Body registerRequest: RegisterDeviceRequest): Response<Any>

    @DELETE("/v1/users/devices")
    suspend fun deleteDevice(@Body deleteRequest: DeleteDeviceRequest): Response<Any>
}