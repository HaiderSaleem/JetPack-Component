package com.debugger.jetpack.retrofit.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnsplashPhoto(
    val id: String,
    val desc: String?,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUsers
) : Parcelable {

    @Parcelize
    data class UnsplashPhotoUrls(
        val raw: String,
        val regular: String,
        val small: String,
        val thumb: String,
    ) : Parcelable

    @Parcelize
    data class UnsplashUsers(
        val name: String,
        val userName: String
    ) : Parcelable {
        val attributionUrl get() = "https://unsplash.com/$userName?utm_source=JetPackApp&utm_medium=referral"
    }
}
