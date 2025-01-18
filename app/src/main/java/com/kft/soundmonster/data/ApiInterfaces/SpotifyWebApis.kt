package com.kft.soundmonster.data.ApiInterfaces

import com.kft.soundmonster.data.models.AlbumDetailsModels.AlbumDetailResponseModel
import com.kft.soundmonster.data.models.ArtistAlbumModels.AlbumResponseModel
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.models.SearchArtitstModels.ArtistResponseModel
import com.kft.soundmonster.utils.Constants.ALBUM_DETAIL
import com.kft.soundmonster.utils.Constants.ARTIST_ALBUM
import com.kft.soundmonster.utils.Constants.FETCH_SONGS
import com.kft.soundmonster.utils.Constants.FETCH_TRACK
import com.kft.soundmonster.utils.Constants.SEARCH_API
import com.kft.soundmonster.utils.Constants.SEVERAL_ARTIST
import com.kft.soundmonster.utils.Constants.SINGLE_ARTIST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyWebApis {

    @GET(SEARCH_API)
    suspend fun getAllArtist(
        @Query("q") query : String,
        @Query("offset") offset : Int = 0,
        @Query("type") type : String = "artist"
    ) : ArtistResponseModel


    @GET(SINGLE_ARTIST)
    suspend fun getSingleArtist(
        @Path("id") artistId: String
    ) : Artist

    @GET(ARTIST_ALBUM)
    suspend fun getArtistAlbum(
        @Path("id") artistId: String
    ) : AlbumResponseModel


    @GET(SEVERAL_ARTIST)
    suspend fun getSeveralArtist(
        @Query("ids") query : String
    ) : ArtistResponseModel

    @GET(ALBUM_DETAIL)
    suspend fun getAlbumDetails(
        @Path("id") albumId: String
    ) : AlbumDetailResponseModel


    @GET(FETCH_TRACK)
    suspend fun getTrack(
        @Path("id") trackId: String
    ) : Track

}
