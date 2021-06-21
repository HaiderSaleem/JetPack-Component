package com.debugger.jetpack.retrofit.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.debugger.jetpack.retrofit.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {

     fun getSearchResult(query: String) =
         Pager(
             config = PagingConfig(
                 20,
                 maxSize = 100,
                 enablePlaceholders = false
             ),
             pagingSourceFactory = {
                 UnsplashPagingSource(unsplashApi, query)
             }
         ).liveData
}