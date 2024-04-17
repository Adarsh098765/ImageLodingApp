package com.adarsh.imagelodingproject.di.networkmodule

import com.adarsh.imagelodingproject.BuildConfig
import com.adarsh.imagelodingproject.data.remote.ApiService
import com.adarsh.imagelodingproject.data.remote.RemoteDataSource
import com.adarsh.imagelodingproject.data.repo.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton

    @Named("BaseUrl")
    fun provideBaseUrl(): String = BuildConfig.BASEURL  // Use BuildConfig

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,

    ): OkHttpClient {
        return OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS) // Adjust timeouts as needed
            .connectTimeout(30, TimeUnit.SECONDS) // Adjust timeouts as needed
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun gsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
        @Named("BaseUrl") baseUrl: String,
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(gsonConverterFactory).build()
    }


    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(apiService: ApiService) = RemoteDataSource(apiService)

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: RemoteDataSource) = Repository(remoteDataSource)


}