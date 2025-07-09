//package com.example.tirociniokelyon.com.example.tirociniokelyon.remote
//
//import android.util.Log
//import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.ApiConstants
//import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.PersistentCookieJar
//import com.example.tirociniokelyon.com.example.tirociniokelyon.utils.SessionCookieJar
//import com.google.gson.GsonBuilder
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Inject
//
//class RetrofitClient @Inject constructor(
//    retrofit: Retrofit
//) {
//
//    private val authAPI = retrofit.create(APIAuth::class.java)
//
//    companion object {
//
//        val okHttpClient = OkHttpClient.Builder()
//            .cookieJar(PersistentCookieJar())
//            .build()
//
//
//        val gson = GsonBuilder()
//            .setLenient()
//            .serializeNulls()
//            .create()
//
//
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(ApiConstants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .client(okHttpClient)
//            .build()
//
//        val api = retrofit.create(APIAuth::class.java)
//
//    }
//}