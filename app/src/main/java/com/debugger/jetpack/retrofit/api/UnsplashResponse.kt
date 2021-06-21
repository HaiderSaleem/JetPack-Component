package com.debugger.jetpack.retrofit.api

import com.debugger.jetpack.retrofit.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)