package com.proto.type.base.data.remote

import com.proto.type.base.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 *  @Detail chat room api service
 */
interface ChatService {

    /**
     *  Add participants (not admin) from a chat room
     */
    @HTTP(method = "POST", path = "/v1/chat-rooms/{room_id}/users", hasBody = true)
    suspend fun addParticipants(@Path("room_id") roomId: String, @Body bodyRequest: ChatManageUsersRequest): Response<Unit>

    /**
     * Create chat room
     */
    @POST("/v1/chat-rooms")
    suspend fun createNewChat(@Body request: CreateChatRequest): ResponseBody

    /**
     * Clear chat's messages history
     */
    @PUT("/v1/chat-rooms/{room_id}/clear-markers")
    suspend fun clearChatHistory(@Path("room_id") roomId: String, @Body request: ClearChatHistoryRequest): Response<Unit>

    /**
     * Get chat rooms list
     */
    @GET("/v1/chat-rooms")
    suspend fun getRemoteChats(): ResponseBody

    /**
     * Get chat room detail
     */
    @GET("/v1/chat-rooms/{room_id}")
    suspend fun getChatDetail(@Path("room_id") roomId: String): ResponseBody

    /**
     * Remove users from room or leave room
     */
    @HTTP(method = "DELETE", path = "/v1/chat-rooms/{room_id}/users", hasBody = true)
    suspend fun leaveChat(@Path("room_id") roomId: String, @Body request: ChatLeaveRequest): Response<Unit>

    /**
     * Mark read a chat
     */
    @PUT("/v1/chat-rooms/{room_id}/read-markers")
    suspend fun markReadChat(@Path("room_id") roomId: String, @Body request: ChatMarkReadRequest): Response<Unit>

    /**
     * Promote/demote users to/from admin role
     */
    @HTTP(method = "PATCH", path = "/v1/chat-rooms/{room_id}/user-roles", hasBody = true)
    suspend fun promoteDemoteUser(@Path("room_id") roomId: String, @QueryMap queryParams: Map<String, String>, @Body bodyRequest: ChatPromoteDemoteUserRequest): Response<Unit>

    /**
     *  Remove participants (not admin) from a chat room
     */
    @HTTP(method = "DELETE", path = "/v1/chat-rooms/{room_id}/users", hasBody = true)
    suspend fun removeParticipants(@Path("room_id") roomId: String, @Body bodyRequest: ChatManageUsersRequest): Response<Unit>

    /**
     * Update room (avatar)
     */
    @PATCH("/v1/chat-rooms/{room_id}")
    suspend fun updateChatAvatar(@Path("room_id") roomId: String, @Body request: ChatUpdateAvatarRequest): Response<Unit>

    /**
     * Update room (is_pinned)
     */
    @PATCH("/v1/chat-rooms/{room_id}")
    suspend fun updateChatIsPinned(@Path("room_id") roomId: String, @Body request: ChatUpdateIsPinnedRequest): Response<Unit>

    /**
     * Update room (mute_notification)
     */
    @PATCH("/v1/chat-rooms/{room_id}")
    suspend fun updateRoomMuteNotification(@Path("room_id") roomId: String, @Body request: ChatUpdateMuteRequest): Response<Unit>

    /**
     * Update room (name)
     */
    @PATCH("/v1/chat-rooms/{room_id}")
    suspend fun updateChatName(@Path("room_id") roomId: String, @Body request: ChatUpdateNameRequest): Response<Unit>
}