package com.kft.soundmonster.data.repo

import android.util.Log
import com.kft.soundmonster.data.ApiInterfaces.SpotifyAuthApis
import com.kft.soundmonster.data.models.AuthModel
import com.kft.soundmonster.local.SharePref
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants.API_ERROR_DEFAULT_MESSAGE
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpotifyAuthRepo @Inject constructor(private val spotifyAuthApis : SpotifyAuthApis, private val sharePref: SharePref) {


    suspend fun getAccessToken() : ApiResult<AuthModel>{
        try{
            val accessTokenResponse = spotifyAuthApis.getAccessToken()
            sharePref.addAccessToken(accessTokenResponse.accessToken)
            return ApiResult.Success(accessTokenResponse)
        }
        catch (e : Exception){
            return ApiResult.Error(e.message ?: API_ERROR_DEFAULT_MESSAGE, e)
        }
    }
}

