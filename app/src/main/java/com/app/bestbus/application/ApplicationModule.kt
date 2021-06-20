package com.app.bestbus.application

import com.app.bestbus.utils.ApiService
import com.app.bestbus.utils.SharedPreferencesHelper
import com.app.bestbus.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
            .build()
            .create(ApiService::class.java)
            /*
            val body = chain.request().body as okhttp3.FormBody
            val output = HashMap<String, String>()
            for (i in body.encodedNames.indices) {
                output[body.encodedName(i)] = body.encodedValue(i)
            }
            com.google.gson.Gson().toJson(output)
             */
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper() = SharedPreferencesHelper()
}