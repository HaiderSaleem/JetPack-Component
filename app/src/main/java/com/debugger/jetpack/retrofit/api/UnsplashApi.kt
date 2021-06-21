package com.debugger.jetpack.retrofit.api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import com.debugger.jetpack.BuildConfig


interface UnsplashApi {

    companion object{

        const val CLIENT_ID = BuildConfig.UNSPLASH_ACCESS_KEY
        const val BASE_URL = "https://api.unsplash.com/"
    }
    @Headers("Accept-Version: v1","Authorization: Client-ID $CLIENT_ID")

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query:String,
        @Query("page") page:Int,
        @Query("per_page") perPage:Int

    ): UnsplashResponse
}