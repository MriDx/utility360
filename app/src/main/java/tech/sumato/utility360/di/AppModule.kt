package tech.sumato.utility360.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.location.LocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.play.core.ktx.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.undabot.izzy.parser.GsonParser
import com.undabot.izzy.parser.Izzy
import com.undabot.izzy.parser.IzzyConfiguration
import com.undabot.izzy.retrofit.IzzyRetrofitConverter
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
import tech.sumato.utility360.data.local.AppDao
import tech.sumato.utility360.data.local.AppDatabase
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.site.SiteVerificationResource
import tech.sumato.utility360.data.remote.web_service.services.ApiHelper
import tech.sumato.utility360.data.remote.web_service.services.ApiHelperImpl
import tech.sumato.utility360.data.remote.web_service.services.ApiService
import tech.sumato.utility360.data.utils.HomeFragmentActionData
import tech.sumato.utility360.data.utils.ProfileActionData
import tech.sumato.utility360.data.utils.getHomeFragmentActions
import tech.sumato.utility360.data.utils.getProfileActions
import tech.sumato.utility360.utils.AnnotatedConverter
import tech.sumato.utility360.utils.GsonInterface
import tech.sumato.utility360.utils.IzzyInterface
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideBaseUrl(): String = "http://pbg-test.sumato.tech/api/v1/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @Singleton
    fun provideGsonParser(gson: Gson): GsonParser {
        return GsonParser(
            izzyConfiguration = IzzyConfiguration(
                resourceTypes = arrayOf(
                    CustomerResource::class.java,
                    SiteVerificationResource::class.java,
                )
            ),
            gson = gson
        )
    }

    @Provides
    @Singleton
    fun provideIzzy(gsonParser: GsonParser): Izzy {
        return Izzy(izzyJsonParser = gsonParser)
    }

    @Provides
    @Singleton
    fun provideIzzyRetrofitConverter(izzy: Izzy): IzzyRetrofitConverter {
        return IzzyRetrofitConverter(izzy = izzy)
    }

    @Provides
    @Singleton
    fun provideAnnotatedConverter(
        gson: Gson,
        izzyRetrofitConverter: IzzyRetrofitConverter
    ): AnnotatedConverter {
        return AnnotatedConverter(
            factoryMap = mapOf(
                GsonInterface::class.java to GsonConverterFactory.create(gson),
                IzzyInterface::class.java to izzyRetrofitConverter
            )
        )
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
            //   if (BuildConfig.DEBUG)
            addInterceptor(loggingInterceptor)
            addInterceptor(authInterceptor)
            callTimeout(600, TimeUnit.SECONDS)
            readTimeout(600, TimeUnit.SECONDS)
            connectTimeout(10000, TimeUnit.SECONDS)
        }.build()

    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        annotatedConverter: AnnotatedConverter
    ): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            client(okHttpClient)
            addConverterFactory(annotatedConverter)
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
    fun provideAppDao(appDatabase: AppDatabase): AppDao =
        appDatabase.appDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("APP_PREF", MODE_PRIVATE)


    @Provides
    fun provideProfileActions(): List<ProfileActionData> = getProfileActions()

    @Provides
    fun provideHomeFragmentActions(): List<HomeFragmentActionData> = getHomeFragmentActions()

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }


}