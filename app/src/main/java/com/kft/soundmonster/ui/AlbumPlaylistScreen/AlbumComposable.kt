package com.kft.soundmonster.ui.AlbumPlaylistScreen

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kft.soundmonster.SoundMonsterApp
import com.kft.soundmonster.data.models.AlbumDetailsModels.AlbumDetailResponseModel
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.models.SongResponseModels.Result
import com.kft.soundmonster.domain.ViewModels.AlbumModelState
import com.kft.soundmonster.domain.ViewModels.AlbumViewModel
import com.kft.soundmonster.domain.ViewModels.HomeActivityViewModel
import com.kft.soundmonster.domain.ViewModels.TrackState
import com.kft.soundmonster.ui.components.Navigations.HomeNavigationScreen.getPlayerRoute
import com.kft.soundmonster.ui.components.Navigations.LocalNavController
import com.kft.soundmonster.ui.components.TRACK_IMAGE
import com.kft.soundmonster.ui.components.TrackImageComposable
import com.kft.soundmonster.ui.components.getAlbumImage
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants
import com.kft.soundmonster.utils.PaletteUtils.getDominateColor
import com.kft.soundmonster.utils.formatDate
import com.kft.soundmonster.utils.formatTime
import com.kft.soundmonster.utils.getAudioThumbnail
import com.kft.soundmonster.utils.getTimeOfTotalAlbum
import kotlinx.coroutines.flow.StateFlow


@Composable
fun AlbumComposable(id: String) {

    val viewModel = hiltViewModel<AlbumViewModel>()
    val albumDetailState = viewModel.albumData.collectAsStateWithLifecycle()


    LaunchedEffect(null) {
        viewModel.fetchAlbumDetails(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(1f),
        containerColor = Color.Transparent
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (albumDetailState.value) {
                is ApiResult.Loading -> {
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
                }

                is ApiResult.Error -> {
                    Column {
                        Text((albumDetailState.value as ApiResult.Error).message)
                        Button(onClick = { }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    AlbumDetailScreen((albumDetailState.value as ApiResult.Success).data)
                }
            }
        }
    }
}


@Composable
fun AlbumDetailScreen(albumDetailResponseModel: AlbumDetailResponseModel) {

    val navController = LocalNavController.current
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Color>(Color.Black) }
    val currentMusic = (LocalContext.current.applicationContext as SoundMonsterApp).musicService?.getCurrentMusic()?.collectAsStateWithLifecycle()

    LaunchedEffect(null) {


        bitmap = getDominateColor(context,getAlbumImage(albumDetailResponseModel))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Box with the color and gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bitmap) // Use the color as the background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f), // Transparent black at the top
                                Color.Black.copy(alpha = 0.6f), // Slightly opaque black
                                Color.Black.copy(alpha = 0.8f), // Darker black
                                Color.Black.copy(alpha = 1f) // Full black at the bottom
                            )
                        )
                    )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {

                AsyncImage(
                    model = getAlbumImage(albumDetailResponseModel),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(vertical = 30.dp)
                        .size(175.dp)
                        .fillMaxWidth(1f)
                        .border(0.dp, Color.Gray, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)) // Apply rounded corners

                )

                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            albumDetailResponseModel.name,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 30.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "${albumDetailResponseModel.tracks.items.getTimeOfTotalAlbum()} • ${albumDetailResponseModel.release_date.formatDate()}",
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
            }

            items(albumDetailResponseModel.tracks.items) { track ->


                Row(modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "TRACKS",
                            albumDetailResponseModel.tracks.items
                        )

                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "ITEM",
                            albumDetailResponseModel.tracks.items.indexOf(track)
                        )

                        navController.navigate(getPlayerRoute(track))
                    }
                    .fillMaxWidth(1f)) {


//                    TrackImageComposable(track.id, TRACK_IMAGE.SMALL)
//                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.name,
                            color = getColorOfSongName(currentMusic?.value, track.name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,

                            )

                        Text(
                            text = track.artists?.map { it.name }?.joinToString(", ")?:"",
                            modifier = Modifier.wrapContentWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = track.duration_ms.formatTime(), color = Color.Gray)

                }
            }
        }
    }
}


fun getColorOfSongName(currentMusic: ApiResult<Result?>?, name : String) : Color{

    Log.e("SoundMonster", currentMusic.toString())
    if (currentMusic != null && currentMusic is ApiResult.Success) {

        if(currentMusic.data?.name?.length!! > name.length){
            if(currentMusic.data.name.contains(name)){
                return Color.Green
            }
        }
        else if(currentMusic.data.name.length < name.length) {
            if(name.contains(currentMusic.data.name)){
                return Color.Green
            }
        }
        else if(currentMusic.data.name == name){
            return Color.Green
        }
    }
    return Color.White
}
