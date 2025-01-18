package com.kft.soundmonster.di

import android.util.Log
import com.kft.soundmonster.data.ApiInterfaces.SaavnWebApis
import com.kft.soundmonster.data.ApiInterfaces.SpotifyAuthApis
import com.kft.soundmonster.data.ApiInterfaces.SpotifyWebApis
import com.kft.soundmonster.data.models.AuthModel
import com.kft.soundmonster.data.repo.SpotifyAuthRepo
import com.kft.soundmonster.local.SharePref
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants
import com.kft.soundmonster.utils.Constants.SAAVN_BASE_URL
import com.kft.soundmonster.utils.Constants.SPOTIFY_AUTH
import com.kft.soundmonster.utils.Constants.SPOTIFY_BASE_WEB_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkDI {


    @LoggingInterceptor
    @Provides
    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @HeaderInterceptor
    @Provides
    fun provideHeaderInterceptor(
        spotifyAuthRepo: SpotifyAuthRepo,
        sharePref: SharePref
    ): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val accessToken = sharePref.getAccessToken()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            var response = chain.proceed(newRequest)

            Log.e("SoundMonster", "Original URL: ${newRequest.url}")


            when (response.code) {
                400 -> {
                    // Handle Bad Request
                    throw Exception("Bad Request: ${response.message}")
                }

                401 -> {
                    // Handle Unauthorized

                    response.close()

                    runBlocking {
                        val accessTokenStatus = spotifyAuthRepo.getAccessToken()
                        Log.e("SoundMonster", "fetching new access token on 401")
                        if (accessTokenStatus is ApiResult.Error) {
                            throw accessTokenStatus.throwable
                                ?: Exception(Constants.API_ERROR_DEFAULT_MESSAGE)
                        }

                        val newAccessToken = sharePref.getAccessToken()
                        val retryRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $newAccessToken")
                            .build()

                        // Make the new request and return the new response
                        response = chain.proceed(retryRequest)
                    }



                }

                403 -> {
                    // Handle Forbidden
                    throw Exception("Forbidden: You don't have permission.")
                }

                404 -> {
                    // Handle Not Found
                    throw Exception("Not Found: The requested resource is not available.")
                }

                500 -> {
                    // Handle Internal Server Error
                    throw Exception("Server Error: Please try again later.")
                }

                else -> {

                }
            }


            response
        }
    }

    @OkHttpForWebApi
    @Provides
    fun provideOkHttpClientForWebApi(
        @LoggingInterceptor loggingInterceptor: Interceptor,
        @HeaderInterceptor headerInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    @OkHttpForAuth
    @Provides
    fun provideOkHttpClientForAuth(
        @LoggingInterceptor loggingInterceptor: Interceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @AuthRetrofit
    @Provides
    fun getSpotifyAuthRetrofit(
        @OkHttpForAuth
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SPOTIFY_AUTH)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @WebApiRetrofit
    @Provides
    fun getSpotifyWebRetrofit(
        @OkHttpForWebApi
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SPOTIFY_BASE_WEB_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @SaavnApiRetrofit
    @Provides
    fun getSaavnApiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SAAVN_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun getSaavnWebApiInstanceWithRetrofi(@SaavnApiRetrofit retrofit: Retrofit) : SaavnWebApis {
        return retrofit.create(SaavnWebApis::class.java)
    }

    @Provides
    fun getSpotifyAuthApiInstanceWithRetrofit(@AuthRetrofit retrofit: Retrofit): SpotifyAuthApis {
        return retrofit.create(SpotifyAuthApis::class.java)
    }


    @Provides
    fun getSpotifyWebApiInstanceWithRetrofit(@WebApiRetrofit retrofit: Retrofit): SpotifyWebApis {
        return retrofit.create(SpotifyWebApis::class.java)
    }
}


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HeaderInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SaavnApiRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpForWebApi


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHttpForAuth
