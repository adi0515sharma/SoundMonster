package com.kft.soundmonster.domain.ViewModels

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kft.soundmonster.data.models.ArtistAlbumModels.AlbumResponseModel
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class ArtistModel(
    val message: String? = null,
    val isError: Boolean = false,
    val data: Artist? = null
) : Parcelable

@Parcelize
data class AlbumModel(
    val message: String? = null,
    val isError: Boolean = false,
    val data: AlbumResponseModel? = null
) : Parcelable


data class ArtistDetail(
    val artistId: String,
    val details: ArtistModel? = null,
    val album: AlbumModel? = null
)

data class HomeScreenModel(
    val pos : Int = 0,
    val data : Any?= null,
    val loading : Boolean = false
    )

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val spotifyWebRepo: SpotifyWebRepo) :
    ViewModel() {

    private val _homeScreenState = MutableStateFlow(mutableListOf<HomeScreenModel>())
    val homeScreenState: StateFlow<MutableList<HomeScreenModel>> = _homeScreenState

    private val _loadingState = MutableStateFlow(true)
    val loadingState : StateFlow<Boolean> = _loadingState


    init {

        viewModelScope.launch {

            _homeScreenState.collectLatest {
                val listOfItems = _homeScreenState.value.toMutableList()

                if(listOfItems.size == 0){
                    return@collectLatest
                }
                for(item in listOfItems){
                    if(!item.loading){
                        continue
                    }

                    if (item.data is ArtistDetail) {
                        val artistDetailJob : Deferred<ApiResult<Artist>> = async { getArtist(item.data.artistId) }
                        val artisAlbumJob : Deferred<ApiResult<AlbumResponseModel>> = async { getArtistAlbum(item.data.artistId) }

                        val responseDetail = artistDetailJob.await()
                        val responseAlbum = artisAlbumJob.await()


                        val newArtistDetail = item.copy(
                            data = item.data.copy(
                                details = ArtistModel(
                                    if (responseDetail is ApiResult.Error) responseDetail.message else null,
                                    responseDetail is ApiResult.Error,
                                    if (responseDetail is ApiResult.Success) responseDetail.data else null
                                ) ,
                                album = AlbumModel(
                                    if (responseAlbum is ApiResult.Error) responseAlbum.message else null,
                                    responseAlbum is ApiResult.Error,
                                    if (responseAlbum is ApiResult.Success) responseAlbum.data else null
                                )
                            ),
                            loading = false
                        )
                        listOfItems[listOfItems.indexOf(item)] = newArtistDetail

                    }

                }
                _loadingState.value = false
                _homeScreenState.value = listOfItems
            }
        }
    }

    fun addArtistDetail(ids: List<String>) {

        viewModelScope.launch {
            _homeScreenState.value = ids.mapIndexed {index, it->
                HomeScreenModel(index, ArtistDetail(it), true)
            }.toMutableList()

        }
    }



    suspend fun getArtist(id: String): ApiResult<Artist> {
        return spotifyWebRepo.getSingleArtist(id)
    }


    suspend fun getArtistAlbum(id: String): ApiResult<AlbumResponseModel> {
        return spotifyWebRepo.getArtistAlbum(id)
    }

}