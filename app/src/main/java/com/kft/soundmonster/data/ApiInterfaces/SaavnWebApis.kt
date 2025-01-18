package com.kft.soundmonster.data.ApiInterfaces

import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.models.SongResponseModels.SongResponseModels
import com.kft.soundmonster.utils.Constants.FETCH_SONGS
import retrofit2.http.GET
import retrofit2.http.Query

interface SaavnWebApis{

    @GET(FETCH_SONGS)
    suspend fun getSong(
        @Query("query") query: String,
        @Query("limit") limit : Int = 1
    ) : SongResponseModels
}