package com.example.tirociniokelyon.com.example.tirociniokelyon.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.example.tirociniokelyon.BuildConfig
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.ApiConstants
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.AuthInterceptor
import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PersistentCookieJar

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, cookieJar: PersistentCookieJar, authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .cookieJar(cookieJar = cookieJar)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.PROD_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiAuth(retrofit: Retrofit): APIAuth {
        return retrofit.create(APIAuth::class.java)
    }

    @Provides
    @Singleton
    fun provideAPIinvite(retrofit: Retrofit): APIinvite {
        return retrofit.create(APIinvite::class.java)
    }

    @Provides
    @Singleton
    fun provideAPIpatient(retrofit: Retrofit): APIpatient {
        return retrofit.create(APIpatient::class.java)
    }

    @Provides
    @Singleton
    fun provideAPIuser(retrofit: Retrofit): APIuser {
        return retrofit.create(APIuser::class.java)
    }

    @Provides
    @Singleton
    fun provideAPIreservation(retrofit: Retrofit): APIreservation {
        return retrofit.create(APIreservation::class.java)
    }
}

