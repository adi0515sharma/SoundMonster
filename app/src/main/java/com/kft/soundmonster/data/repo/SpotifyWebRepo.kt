package com.kft.soundmonster.data.repo

import com.kft.soundmonster.data.ApiInterfaces.SaavnWebApis
import com.kft.soundmonster.data.ApiInterfaces.SpotifyWebApis
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpotifyWebRepo @Inject constructor(private val spotifyWebApis: SpotifyWebApis, private  val saavnWebApis : SaavnWebApis) {

    suspend fun getArtistList(offset: Int = 0, query: String? = null) = try {

        val newQuery: String = if (!query.isNullOrEmpty()) query else "artist"
        val responseModel = spotifyWebApis.getAllArtist(query = newQuery, offset = offset)
        ApiResult.Success(responseModel)

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e)

    }


    suspend fun getSingleArtist(id: String) = try {

        val responseModel = spotifyWebApis.getSingleArtist(id)
        ApiResult.Success(responseModel)

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e)

    }


    suspend fun getArtistAlbum(id: String) = try {

        val responseModel = spotifyWebApis.getArtistAlbum(id)
        ApiResult.Success(responseModel)

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e)

    }

    suspend fun getSeveralArtist(ids: List<String>) = try {

        val newIds = ids.joinToString(",")
        val responseModel = spotifyWebApis.getSeveralArtist(newIds)
        ApiResult.Success(responseModel)

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e)

    }


    suspend fun getAlbumDetails(id: String) = try {

        val responseModel = spotifyWebApis.getAlbumDetails(id)
        ApiResult.Success(responseModel)

    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e)

    }


    fun getSong(name: String) = flow{

        emit(ApiResult.Loading)
        try {

            val responseModel = saavnWebApis.getSong(name)
            if(responseModel.success){
                emit(ApiResult.Success(responseModel.data.results[0]))
            }
            else{
                emit(ApiResult.Error("No Song Found", Exception("Error : No Song Found")))
            }
        } catch (e: Exception) {

            emit(ApiResult.Error(e.localizedMessage ?: Constants.API_ERROR_DEFAULT_MESSAGE, e))

        }
    }
}