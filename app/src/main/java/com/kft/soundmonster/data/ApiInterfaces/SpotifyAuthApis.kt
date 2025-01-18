package com.kft.soundmonster.data.ApiInterfaces

import com.kft.soundmonster.data.models.AuthModel
import com.kft.soundmonster.utils.Constants.CLIENT_ID
import com.kft.soundmonster.utils.Constants.CLIENT_SECRET
import com.kft.soundmonster.utils.Constants.GENERATE_TOKEN
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST



interface SpotifyAuthApis {

    @FormUrlEncoded
    @POST(GENERATE_TOKEN)
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("client_id") clientId: String = CLIENT_ID,
        @Field("client_secret") clientSecret: String = CLIENT_SECRET
    ):AuthModel
}