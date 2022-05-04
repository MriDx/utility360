package com.mridx.apptemplate.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.android.play.core.ktx.BuildConfig
import com.google.gson.Gson
import com.mridx.apptemplate.data.local.AppDao
import com.mridx.apptemplate.data.local.AppDatabase
import com.mridx.apptemplate.data.remote.web_service.services.ApiHelper
import com.mridx.apptemplate.data.remote.web_service.services.ApiHelperImpl
import com.mridx.apptemplate.data.remote.web_service.services.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://yourbaseurl.com"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(): Interceptor {
        // TODO: build auth request
        return Interceptor { chain ->
            val request = chain.request()
            val authRequest = request.newBuilder()
                .build()
            chain.proceed(authRequest)
        }
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG)
                addInterceptor(loggingInterceptor)
            addInterceptor(authInterceptor)
            callTimeout(600, TimeUnit.SECONDS)
            readTimeout(600, TimeUnit.SECONDS)
            connectTimeout(10000, TimeUnit.SECONDS)
            build()
        }.build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(gson))
        }.build()
    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.build(context)


    @Singleton
    @Provides
    fun provideAppeDao(appDatabase: AppDatabase): AppDao =
        appDatabase.appDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("APP_PREF", MODE_PRIVATE)


}