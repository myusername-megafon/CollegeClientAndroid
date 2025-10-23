package com.example.collegeclientandroid.network

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("api/Schedule/schedule/{group}/{day}")
    suspend fun getSchedule(@Path("group") group: String, @Path("day") day: String): List<String>

    @POST("api/Users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/Users")
    suspend fun registration(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("api/Users/{id}/photo")
    suspend fun getUserPhoto(@Path("id") userId: Int): Response<okhttp3.ResponseBody>

    @Multipart
    @POST("api/Users/{id}/photo")
    suspend fun uploadUserPhoto(
        @Path("id") userId: Int,
        @Part photo: MultipartBody.Part
    ): Response<okhttp3.ResponseBody>
}

data class RegisterResponse (
    val id: Int,
    val fio: String,
    val email: String,
    val photoFiletype: String,
    val group: String
)

data class RegisterRequest (
    @SerializedName("fio") val FIO: String,
    @SerializedName("email") val Email: String,
    @SerializedName("password") val Password: String,
    @SerializedName("photo") val Photo: String,
    @SerializedName("group") val Group: String
)

data class LoginResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("fio") val fio: String,
    @SerializedName("email") val email: String,
    @SerializedName("photoFiletype") val photo: String,
    @SerializedName("group") val group: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String)