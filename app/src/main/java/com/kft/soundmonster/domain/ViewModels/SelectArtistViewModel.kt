package com.kft.soundmonster.domain.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.models.SearchArtitstModels.ArtistResponseModel
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.local.SharePref
import com.kft.soundmonster.ui.components.PagingComponent.SelectArtistPagingSource
import com.kft.soundmonster.utils.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@HiltViewModel
class SelectArtistViewModel @Inject constructor(private val spotifyWebRepo: SpotifyWebRepo, private val sharePref : SharePref): ViewModel() {


    private val searchQuery = MutableStateFlow<String?>(null)

    val selectedArtistList = MutableStateFlow<MutableList<Artist>>(mutableListOf<Artist>())

    val artistListFlow = searchQuery
        .debounce(500) // Optional: Debounce to prevent excessive requests
        .distinctUntilChanged() // Prevent unnecessary flow emissions
        .flatMapLatest { query ->
            Pager(
                PagingConfig(pageSize = 20)
            ) {
                SelectArtistPagingSource(
                    spotifyWebRepo = spotifyWebRepo,
                    searchText = query
                )
            }.flow
        }
        .cachedIn(viewModelScope) // Cache the flow to persist data across configuration changes

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }


    fun updateSelectedArtistList(item : Artist, action : String){

        Log.e("SoundMonster", "dataset  changed ${item.name} = ${action}")

        val currentList = selectedArtistList.value.toMutableList()

        if(action == "+"){
            currentList.add(item)
            selectedArtistList.value = currentList
        }
        else{

            currentList.remove(currentList.find { it.id == item.id})
            selectedArtistList.value = currentList

        }
    }

    suspend fun getSingleArtist(id : String): ApiResult<Artist> {
        return spotifyWebRepo.getSingleArtist(id)
    }

    fun setArtistList() : Boolean{
        try {


            sharePref.setSelectedArtist( selectedArtistList.value.map { it.id }.toMutableList())

        }
        catch (e : Exception){
            return false
        }

        return true
    }



    suspend fun getSeveralArtists(ids : List<String>): ApiResult<ArtistResponseModel> {
        return spotifyWebRepo.getSeveralArtist(ids)
    }





}