package com.app.bestbus.data.home.di

import com.app.bestbus.data.home.repository.*
import com.app.bestbus.utils.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {

    @Provides
    @Singleton
    fun provideHomeRepository(apiService: ApiService): HomeRepository = HomeRepositoryImpl(apiService)
}