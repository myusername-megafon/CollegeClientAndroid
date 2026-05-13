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
    @GET("api/client-app/config")
    suspend fun getClientAppConfig(): Response<ClientAppConfigResponse>

    @GET("api/Schedule/schedule/{group}/{day}")
    suspend fun getSchedule(@Path("group") group: String, @Path("day") day: String): List<String>

    @GET("api/Schedule/replacements/{group}/{day}")
    suspend fun getReplacements(@Path("group") group: String, @Path("day") day: String): List<String>

    @GET("api/Schedule/week-info")
    suspend fun getWeekInfo(): String

    @GET("api/Schedule/study-plan/{group}")
    suspend fun getStudyPlan(@Path("group") group: String): List<String>

    @POST("api/Push/register")
    suspend fun registerPushToken(@Body request: RegisterPushTokenRequest): Response<Unit>

    @POST("api/Users/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("api/Users")
    suspend fun registration(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("api/Users/request-email-code")
    suspend fun requestEmailCode(@Body request: RegisterRequest): Response<okhttp3.ResponseBody>

    @POST("api/Users/verify-email-code")
    suspend fun verifyEmailCode(@Body request: VerifyEmailCodeRequest): Response<RegisterResponse>

    @GET("api/Users/{id}/photo")
    suspend fun getUserPhoto(@Path("id") userId: Int): Response<okhttp3.ResponseBody>

    @Multipart
    @POST("api/Users/{id}/photo")
    suspend fun uploadUserPhoto(
        @Path("id") userId: Int,
        @Part photo: MultipartBody.Part
    ): Response<okhttp3.ResponseBody>
}

data class ClientAppConfigResponse(
    val registrationEnabled: Boolean = true,
    val maintenanceEnabled: Boolean = false,
    val maintenanceMessage: String? = null
)

data class RegisterResponse (
    val id: Int,
    val fio: String,
    val email: String,
    val photoFiletype: String?,
    val group: String?
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

data class RegisterPushTokenRequest(
    @SerializedName("token") val token: String,
    @SerializedName("group") val group: String?
)

data class VerifyEmailCodeRequest(
    @SerializedName("email") val email: String,
    @SerializedName("code") val code: String
)