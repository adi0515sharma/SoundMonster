package com.kft.soundmonster.data.models

import com.google.gson.annotations.SerializedName


data class AuthModel(
    @SerializedName("access_token")
    val accessToken : String,
    @SerializedName("token_type")
    val tokenType : String,
    @SerializedName("expires_in")
    val expiresIn : String
)
