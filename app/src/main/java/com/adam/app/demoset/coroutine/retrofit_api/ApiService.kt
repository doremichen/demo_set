/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *        This is a retrofit api interface.
 *
 * @version 1.0
 * @since 2025/07/23
 */
package com.adam.app.demoset.coroutine.retrofit_api

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): User
}