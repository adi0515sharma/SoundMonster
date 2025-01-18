package com.kft.soundmonster.ui.components.PagingComponent

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants



class SelectArtistPagingSource constructor(
    private val spotifyWebRepo: SpotifyWebRepo,
    private val searchText : String?
): PagingSource<Int, Artist>() {
    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            // Get the closest page to the anchor position
            state.closestPageToPosition(anchorPosition)?.let { closestPage ->
                // Calculate the offset for refreshing
                closestPage.prevKey?.plus(20) ?: closestPage.nextKey?.minus(20)
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        return try {
            val offset = params.key ?: 0
            val response = spotifyWebRepo.getArtistList(query = searchText, offset = offset)
            if(response is ApiResult.Error){
                return LoadResult.Error(response.throwable ?: Exception(Constants.API_ERROR_DEFAULT_MESSAGE))

            }


            val result = (response as ApiResult.Success).data.artists.items
            return LoadResult.Page(
                data = result,
                prevKey = if (offset == 0) null else offset - 20,
                nextKey = if (result.isNullOrEmpty()) null else offset + 20,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }
}