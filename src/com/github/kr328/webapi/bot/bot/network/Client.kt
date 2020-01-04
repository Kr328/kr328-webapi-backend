package com.github.kr328.webapi.bot.bot.network

import retrofit2.Retrofit

class Client(val retrofit: Retrofit) :
    IClient by retrofit.create(IClient::class.java)