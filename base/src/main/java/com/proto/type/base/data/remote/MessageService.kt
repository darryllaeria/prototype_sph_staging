package com.proto.type.base.data.remote

import com.proto.type.base.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MessageService {

    /**
     * Get link message items of a chat
     */
    @GET("/v1/messages/chat-item-list")
    suspend fun getLinkItems(@Query("room_id") roomId: String,
                             @Query("page_size") pageSize: Int = 0,
                             @Query("type") type: String = "Link",
                             @Query("end_time") endTime: String? = null): Response<MessageLinkResponse>

    /**
     * Get media (image/video) message items of a chat
     */
    @GET("/v1/messages/chat-item-list")
    suspend fun getMediaItems(@Query("room_id") roomId: String,
                              @Query("page_size") pageSize: Int = 0,
                              @Query("type") type: String = "Media",
                              @Query("end_time") endTime: String? = null): Response<MessageMediaResponse>

    /**
     * Get messages of a chat (inner information part of the chat)
     */
    @GET("/v1/messages")
    suspend fun getMessages(
        @Query("room_id") roomId: String,
        @Query("page_size") pageSize: Int? = 0,
        @Query("end_time") endTime: Double? = null
    ): ResponseBody

    /**
     * Send text message inside chat
     */
    @POST("/v1/messages/user-messages")
    suspend fun sendMessage(@Body request: CreateMessageRequest): ResponseBody
}