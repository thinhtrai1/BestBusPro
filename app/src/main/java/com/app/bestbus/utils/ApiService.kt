package com.app.bestbus.utils

import com.app.bestbus.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    fun login(@Field("email") email: String, @Field("password") password: String): Response<User>

    @FormUrlEncoded
    @POST("signUp.php")
    fun signUp(@Field("email") email: String, @Field("password") password: String, @Field("name") name: String): Response<User>

    @Multipart
    @POST("updateProfile.php")
    fun updateProfile(@Part("userId") userId: RequestBody,
                      @Part("name") name: RequestBody,
                      @Part("email") contact_no: RequestBody,
                      @Part("phone") password: RequestBody,
                      @Part("password") latitude: RequestBody,
                      @Part image: MultipartBody.Part?): Response<User>

    @GET("getTour.php")
    suspend fun getTour(@Query("from") from: String, @Query("to") to: String): Response<ArrayList<Tour>>

    @FormUrlEncoded
    @POST("bookTour.php")
    fun booking(@Field("userId") userId: Int?,
                @Field("name") name: String,
                @Field("email") email: String,
                @Field("phone") phone: String,
                @Field("tourId") tourId: Int,
                @Field("date") date: String?,
                @Field("seatList") seatSelected: String,
                @Field("paymentMethod") paymentMethod: String,
                @Field("paymentInformation") paymentInformation: String,
                @Field("totalAmount") totalAmount: Float): Response<Ticket>

    @GET("getDeal.php")
    suspend fun getDeal(): Response<ArrayList<Deal>>

    @GET("getOffer.php")
    suspend fun getOffer(): Response<ArrayList<Offer>>

    @GET("scanTicket.php")
    fun scanTicket(@Query("qrCode") qrCode: String): Response<Ticket>

    @FormUrlEncoded
    @POST("addTour.php")
    fun addTour(@Field("tourName") tourName: String,
                @Field("oldPrice") oldPrice: String,
                @Field("price") price: String,
                @Field("startTime") startTime: String,
                @Field("time") time: String,
                @Field("fromCity") fromCity: String?,
                @Field("toCity") toCity: String,
                @Field("seatQuantity") seatQuantity: String,
                @Field("count") count: String,
                @Field("vat") vat: String): Response<ResponseBody>
}