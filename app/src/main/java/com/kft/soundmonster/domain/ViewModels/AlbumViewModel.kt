package com.kft.soundmonster.domain.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kft.soundmonster.data.models.AlbumDetailsModels.AlbumDetailResponseModel
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.getAudioThumbnail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject





data class TrackState(
    val tracks: Track ?= null,
    val loading : Boolean = false,
    val message: String?
)

data class AlbumModelState(val albumDetail : AlbumDetailResponseModel, val tracks : List<TrackState>)




@HiltViewModel
class AlbumViewModel @Inject constructor(val spotifyWebApis: SpotifyWebRepo): ViewModel() {

    val _albumData : MutableStateFlow<ApiResult<AlbumDetailResponseModel>> = MutableStateFlow(ApiResult.Loading)
    val albumData : StateFlow<ApiResult<AlbumDetailResponseModel>> = _albumData



    fun fetchAlbumDetails(id : String){
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            val albumDetailResponse = spotifyWebApis.getAlbumDetails(id)
            _albumData.value = albumDetailResponse


        }
    }




}