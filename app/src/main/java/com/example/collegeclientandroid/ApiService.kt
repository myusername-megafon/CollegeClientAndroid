package com.example.collegeclientandroid

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/Schedule/schedule/{group}/{day}")
    suspend fun getSchedule(@Path("group") group: String, @Path("day") day: String): List<String>
}