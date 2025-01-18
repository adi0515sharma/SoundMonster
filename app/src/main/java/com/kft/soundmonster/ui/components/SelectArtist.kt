package com.kft.soundmonster.ui.components

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.kft.soundmonster.data.models.AlbumDetailsModels.AlbumDetailResponseModel
import com.kft.soundmonster.data.models.Commons.Album
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.utils.Constants
import com.kft.soundmonster.utils.formatNumber
import com.kft.soundmonster.utils.isArtistSelected
import kotlinx.parcelize.Parcelize


@Parcelize
data class SelectArtistScreenModel(
    val loading: Boolean = false,
    val data: ArrayList<Artist> = arrayListOf(),
    val error: String? = null
) :
    Parcelable

@Composable
fun SelectArtist(artistListOfItems : LazyPagingItems<Artist>, listOfSelectedArtist : MutableList<Artist>, onClick : (Artist)->Unit) {


    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 8.dp).padding(top = 5.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp), // Adds padding around the grid
        horizontalArrangement = Arrangement.spacedBy(10.dp), // Horizontal spacing between items
        verticalArrangement = Arrangement.spacedBy(10.dp) // Vertical spacing between items

    ) {
        items(artistListOfItems.itemCount) { item ->
            if(artistListOfItems[item] == null) {
                return@items
            }

            ArtistItemComposable(artistListOfItems[item]!!, listOfSelectedArtist, onClick)

        }

        if (artistListOfItems.loadState.hasError) {
            item {
                Column {
                    Text(Constants.API_ERROR_DEFAULT_MESSAGE)
                    Button(onClick = { artistListOfItems.retry() }) {
                        Text("Retry")
                    }
                }
            }
            return@LazyVerticalGrid
        }

    }
}


@Composable
fun ArtistItemComposable(item : Artist, listOfSelectedArtist :MutableList<Artist>, onClick : (Artist)->Unit){

    Column(modifier = Modifier
        .fillMaxWidth(1f)
        .border(width = 2.dp, color = if(!listOfSelectedArtist.isArtistSelected(item.id)) Color.Gray else Color.Green, shape = RoundedCornerShape(15.dp))
        .clickable { onClick(item) }
        .padding(vertical = 15.dp)
        , horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = getArtistImage(item),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = item.name,
            maxLines = 1,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
            )
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "${item.followers?.total?.formatNumber() ?: 0} followers", maxLines = 1, color = Color.White, textAlign = TextAlign.Center)

    }

}


fun getArtistImage(items : Artist? = null) : String{
    if(items == null || items.images?.isEmpty() == true){
        return "https://static.vecteezy.com/system/resources/thumbnails/002/318/271/small/user-profile-icon-free-vector.jpg"
    }

    if(items.images == null){
        return ""
    }
    return items.images[0].url
}


fun getAlbumImage(items : Album? = null) : String{
    if(items == null || items.images.isEmpty()){
        return "https://static.vecteezy.com/system/resources/thumbnails/002/318/271/small/user-profile-icon-free-vector.jpg"
    }

    return items.images[0].url
}

fun getAlbumImage(items : AlbumDetailResponseModel? = null) : String{
    if(items == null || items.images.isEmpty()){
        return "https://static.vecteezy.com/system/resources/thumbnails/002/318/271/small/user-profile-icon-free-vector.jpg"
    }

    return items.images[0].url
}


