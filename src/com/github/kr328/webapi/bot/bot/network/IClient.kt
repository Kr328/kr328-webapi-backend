package com.github.kr328.webapi.bot.bot.network

import com.github.kr328.webapi.bot.bot.network.markup.Markup
import com.github.kr328.webapi.bot.bot.network.updates.Update
import retrofit2.http.*

interface IClient {
    @GET("getUpdates")
    suspend fun getUpdates(@Query("offset") offset: Long,
                           @Query("timeout") timeout: Long = 60): Response<List<Update>>

    @FormUrlEncoded
    @POST("sendMessage")
    suspend fun sendMessage(@Field("chat_id") chatId: Long,
                            @Field("text") text: String,
                            @Field("reply_to_message_id") replyToMessageId: Long?,
                            @Field("reply_markup") replyMarkup: Markup?)
}