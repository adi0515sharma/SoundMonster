package com.kft.soundmonster.ui.PlayerComposable

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.kft.soundmonster.R
import com.kft.soundmonster.SoundMonsterApp
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.models.SongResponseModels.Result
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.formatTimeForPlayer
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FullScreenPlayerComposable() {

    val musicService = (LocalContext.current.applicationContext as SoundMonsterApp).musicService
    val currentMusic = musicService?.getCurrentMusic()?.collectAsStateWithLifecycle()
    val currentDuration = musicService?.getCurrentDuration()?.collectAsStateWithLifecycle()
    val currentMusicState = musicService?.getCurrentMusicState()?.collectAsStateWithLifecycle()
    val currentMusicMaxDuration = musicService?.getMaxDuration()?.collectAsStateWithLifecycle()


    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) {


        Column (
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){


            if(currentMusic?.value == null){
                return@Column
            }
            when(currentMusic.value){
                is ApiResult.Loading->{
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

                is ApiResult.Success ->{
                    Box(modifier = Modifier.fillMaxSize()) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 10.dp, horizontal = 16.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text((currentMusic.value as ApiResult.Success<Result>).data.name, color = Color.White, textAlign = TextAlign.Center)

                            AsyncImage(
                                model = (currentMusic.value as ApiResult.Success<Result>).data.image[2].url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(vertical = 30.dp)
                                    .size(300.dp)
                                    .fillMaxWidth(1f)
                                    .border(0.dp, Color.Gray, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp)) // Apply rounded corners

                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                ) {
                                    Slider(
                                        value = currentDuration?.value?.toFloat()?:0f,
                                        onValueChange = {

                                            musicService.seekTo(it.toInt())
                                        },
                                        valueRange = 0f..(currentMusicMaxDuration?.value?:0).toFloat(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.Green,
                                            activeTrackColor = Color.White,
                                            inactiveTrackColor = Color.LightGray
                                        )
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = formatTimeForPlayer(currentDuration?.value?:0),
                                            color = Color.White
                                        )
                                        Text(text = formatTimeForPlayer(currentMusicMaxDuration?.value?:0), color = Color.White)
                                    }
                                }



                                Spacer(modifier = Modifier.height(15.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Image(
                                        painter = painterResource(R.drawable.baseline_skip_previous_24),
                                        contentDescription = "10 sec previous",
                                        modifier = Modifier.size(45.dp).clickable {
                                            musicService.backwardMusic()
                                        }
                                    )

                                    Spacer(modifier = Modifier.width(20.dp))

                                    if (currentMusicState?.value == null) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(65.dp) // Set size of the loading view
                                                .padding(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                strokeWidth = 4.dp, // Adjust the thickness
                                                color = Color.White
                                            )
                                        }
                                    } else {
                                        Image(
                                            painter = painterResource(if (currentMusicState.value == true) R.drawable.baseline_pause_circle_24 else R.drawable.baseline_play_circle_24),
                                            contentDescription = "play pause button",
                                            modifier = Modifier.size(65.dp).clickable {
                                                if (currentMusicState.value == null) {
                                                    return@clickable
                                                }

                                                musicService.toggleMusicState()

                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))

                                    Image(
                                        painter = painterResource(R.drawable.baseline_skip_next_24),
                                        contentDescription = "10 sec forward",
                                        modifier = Modifier.size(45.dp).clickable {
                                            musicService.forwardMusic()
                                        }
                                    )

                                }

                            }


                        }
                    }
                }

                else -> {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp) // Set size of the loading view
                            .padding(8.dp)
                    ) {
                        Text(text = (currentMusic.value as ApiResult.Error).message, color = Color.White)
                    }
                }
            }
        }



    }

}