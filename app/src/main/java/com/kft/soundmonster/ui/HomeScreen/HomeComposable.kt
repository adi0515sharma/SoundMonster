package com.kft.soundmonster.ui.HomeScreen

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kft.soundmonster.data.models.Commons.Album
import com.kft.soundmonster.domain.ViewModels.AlbumModel
import com.kft.soundmonster.domain.ViewModels.ArtistDetail
import com.kft.soundmonster.domain.ViewModels.ArtistModel
import com.kft.soundmonster.domain.ViewModels.HomeActivityViewModel
import com.kft.soundmonster.ui.components.Navigations.HomeNavigationScreen.getAlbumItemsRoute
import com.kft.soundmonster.ui.components.Navigations.LocalNavController
import com.kft.soundmonster.ui.components.getAlbumImage
import com.kft.soundmonster.ui.components.getArtistImage
import com.kft.soundmonster.utils.Constants


val HomeLocalNavController = compositionLocalOf<HomeActivityViewModel> {
    error("No NavHostController provided")
}

@Composable
fun HomeComposable(artistList: List<String>) {

    val viewModel = hiltViewModel<HomeActivityViewModel>()
    val artistStateList by viewModel.homeScreenState.collectAsStateWithLifecycle()
    val screenLoading by viewModel.loadingState.collectAsStateWithLifecycle()
    LaunchedEffect(screenLoading){
        if(screenLoading){
            viewModel.addArtistDetail(artistList)
        }
    }


    CompositionLocalProvider(HomeLocalNavController provides viewModel) {

        Scaffold(modifier = Modifier.fillMaxSize(1f),
            containerColor = Color.Transparent) { it ->
            Column(modifier = Modifier.fillMaxSize().padding(it), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

                if (screenLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp) // Set size of the loading view
                            .padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 4.dp // Adjust the thickness
                        )
                    }
                    return@Scaffold
                }

                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    items(artistStateList, key = { it.pos }) {


                        if(it.data is ArtistDetail)
                        {

                            ArtistWithAlbums(it.data.details, it.data.album)

                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ArtistWithAlbums(artistDetail: ArtistModel?, albumModel : AlbumModel?) {


    if(artistDetail != null){
        ArtistComposable(artistDetail){}
    }

    if(albumModel != null){
        AlbumListComposable(albumModel){}
    }
}

@Composable
fun ArtistComposable(artistModel: ArtistModel, onRetry : ()->Unit) {
    if (artistModel.isError) {
//        Column {
//            Text(artistModel.message?:Constants.API_ERROR_DEFAULT_MESSAGE)
//            Button(onClick = { onRetry() }) {
//                Text("Retry")
//            }
//        }
        return
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)){
        AsyncImage(
            model = getArtistImage(artistModel.data),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape) // Clip the image into a circular shape
                .border(width = 1.dp, color = Color.White, shape = CircleShape) // Add a circular white border
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column( modifier = Modifier
            .height(50.dp) ,
            verticalArrangement = Arrangement.SpaceBetween) {
            Text("More Like", color = Color.LightGray)
            Text(artistModel.data?.name?:"Not available", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.W500)
        }
    }


}

@Composable
fun AlbumListComposable(albumModel: AlbumModel, onRetry : ()->Unit){


    if (albumModel.isError) {
//        Column {
//            Text(albumModel.message?:Constants.API_ERROR_DEFAULT_MESSAGE)
//            Button(onClick = { onRetry() }) {
//                Text("Retry")
//            }
//        }
        return
    }

    LazyRow (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)){
        items(albumModel.data?.items?: emptyList()){
            AlbumComposable(it)
        }
    }
}

@Composable
fun AlbumComposable(album : Album){

    val navController = LocalNavController.current

    Column(modifier = Modifier.width(135.dp).clickable {
        navController.navigate(getAlbumItemsRoute(album.id))
    }){
        AsyncImage(
            model = getAlbumImage(album),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(135.dp)
                .fillMaxWidth(1f)
                .border(0.dp, Color.Gray, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp)) // Apply rounded corners

        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(album.name, maxLines = 1, fontWeight = FontWeight.SemiBold, color = Color.White)
        Spacer(modifier = Modifier.height(2.dp))

        Text(text = album.artists.map { it.name }.joinToString (", " ),
            modifier = Modifier.wrapContentWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Color.LightGray
        )
    }
}