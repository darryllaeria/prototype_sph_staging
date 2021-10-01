package com.proto.type.base.data.remote

import com.proto.type.base.JSONMutableMap
import com.proto.type.base.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @Detail user api service
 */

interface UserService {

    /**
     * Block a user
     */
    @POST("/v1/users/block-list")
    suspend fun blockUser(@Body request: ManageBlockeeRequest): Response<Unit>

    /**
     * Check if a user exists in ChatQ system or not
     */
    @GET("/users")
    suspend fun checkUserExists(@QueryMap params: Map<String, String>): UserExistResponse

    /**
     * Create user
     */
    @POST("/v1/users")
    suspend fun createUser(@Body request: UserRegisterRequest): ResponseBody

    /**
     * Delete user
     */
    @HTTP(method = "DELETE", path = "/v1/users", hasBody = true)
    suspend fun deleteUser(@Body userAgent: UserDeleteRequest): Response<Unit>

    /**
     * Get blockers and blockees lists of the currently logged user.
     */
    @GET("/v1/users/block-list")
    suspend fun getBlockList(): ResponseBody

    /**
     * Get all ChatQ users based on give phone numbers from local contacts.
     */
    @GET("/v1/users/contacts")
    suspend fun getChatQContacts(@QueryMap params: Map<String, String>): ResponseBody

    /**
     * Get users' online status.
     */
    @POST("/users/last-onlines")
    suspend fun getLastOnline(@Body user_ids: List<String>): ResponseBody

    /**
     * Get user profile
     */
    @GET("/v1/users/{user_id}/profile")
    suspend fun getProfile(@Path("user_id") userId: String, @QueryMap params: JSONMutableMap): ResponseBody

    /**
     * Get user profiles (override UserModel by parsing response directly to UserModel Model)
     */
    @POST("/v1/users/profiles")
    suspend fun getProfiles(@Body request: GetProfilesRequest): ResponseBody

    /**
     * Get current user's secret key
     */
    @GET("/v1/users/secret-key")
    suspend fun getSecretKey(): ResponseBody

    /**
     * Report a user
     */
    @POST("/v1/users/reports")
    suspend fun report(@Body userReport: UserReportRequest): Response<Unit>

    /**
     * Search for a user by username (ChatQ ID)
     */
    @GET("/v1/users/search")
    suspend fun searchUser(@Query(value = "username") userName: String)

    /**
     * Sync local contacts to ChatQ server
     */
    @PUT("/v1/users/contacts")
    suspend fun syncContacts(@Body request: ContactsRequest): ResponseBody

    /**
     * Unblock a user
     */
    @HTTP(method = "DELETE", path = "/v1/users/block-list", hasBody = true)
    suspend fun unblockUser(@Body request: ManageBlockeeRequest): Response<Unit>

    /**
     * Update user profile
     */
    @PATCH("/v1/users/{user_id}/profile")
    suspend fun updateProfile(@Path("user_id") userId: String, @Body request: UserUpdateRequest):Response<Unit>

//    @GET("/v1/mkt/{id}")
//    fun getChartValueAsync(
//        @Path("id") id: String,
//        @Query("period") period: String,
//        @Query("duration") duration: String
//    ): Deferred<Response<ChartResponse>>
//
//    @GET("v1/{id}/raw-metrics")
//    fun getRawMetricsAsync(@Path("id") id: String): Deferred<Response<RawMetricsResponse>>
}

