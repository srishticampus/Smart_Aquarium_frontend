package com.project.aquafarm.api

import com.project.aquafarm.analysis.model.DateBasedValuesResponse
import com.project.aquafarm.analysis.model.DateResponse
import com.project.aquafarm.analysis.model.RecentAnalysisResponse
import com.project.aquafarm.dashboard.ProfileViewResponse
import com.project.aquafarm.dashboard.SensorDataResponse
import com.project.aquafarm.login.ForgotPasswordResponse
import com.project.aquafarm.login.LoginResponse
import com.project.aquafarm.profile.model.ProfileUpdateResponse
import com.project.aquafarm.resetpassword.ResetPasswordResponse
import com.project.aquafarm.signup.SignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface APIInterface {

    @FormUrlEncoded
    @POST("registration.php")
    suspend fun userSignup(
        @FieldMap params: HashMap<String?, String>
    ): Response<SignupResponse>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun userLogin(
        @FieldMap params: HashMap<String?, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("change_password.php")
    suspend fun resetPassword(
        @FieldMap params: HashMap<String?, String>

    ): Response<ResetPasswordResponse>

    @POST("view_profile.php")
    suspend fun viewProfile(
        @Query("userid") userId: String
    ): Response<ProfileViewResponse>

    @FormUrlEncoded
    @POST("profile_update.php")
    suspend fun updateUserProfile(

        @Field("userid") id: Int,
        @Field("first_name") firstname: String,
        @Field("last_name") lastname: String,
        @Field("email") email: String,
        @Field("phone") mobile: String,
    ): Response<ProfileUpdateResponse>

    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun forgotPassword(
        @FieldMap params: HashMap<String?, String>
    ): Response<ForgotPasswordResponse>

    @POST("store_sensor_data.php")
    suspend fun storeSensorData(
        @Body payload: Map<String, String>
    ): Response<SensorDataResponse>

    @GET("recent_analysis.php")
    suspend fun recentAnalysisData(

    ): Response<RecentAnalysisResponse>

    @GET("date_list.php")
    suspend fun selectDate(

    ): Response<DateResponse>

    @FormUrlEncoded
    @POST("view_analysis_date.php")
    suspend fun getAnalysisDataByDate(
        @Field("date") date: String
    ): Response<DateBasedValuesResponse>

}