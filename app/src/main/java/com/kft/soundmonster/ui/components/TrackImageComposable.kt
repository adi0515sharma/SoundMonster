package com.kft.soundmonster.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.getAudioThumbnail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class TRACK_IMAGE{
    LARGE,
    MEDIUM,
    SMALL
}
@Composable
fun TrackImageComposable(id : String, size : TRACK_IMAGE){

    var loading by rememberSaveable { mutableStateOf(true) }
    var bitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(id){

        launch(Dispatchers.IO){
            bitmap = getAudioThumbnail("https://yank.g3v.co.uk/track/${id}")
            loading = false
        }


    }



    if(loading){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(if(size == TRACK_IMAGE.SMALL) 50.dp else if(size == TRACK_IMAGE.MEDIUM) 100.dp else 300.dp) // Set size of the loading view
                .padding(8.dp)
        ) {
            CircularProgressIndicator(
                strokeWidth = 4.dp // Adjust the thickness
            )
        }
        return
    }


    if(bitmap == null){
        return
    }


    Image(
        bitmap = bitmap?.asImageBitmap()!!,
        contentDescription = "Bitmap Image",
        modifier = Modifier
            .size(if(size == TRACK_IMAGE.SMALL) 50.dp else if(size == TRACK_IMAGE.MEDIUM) 100.dp else 300.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp)), // Applying rounded corners, // Adjust this as needed
        contentScale = ContentScale.Crop // Adjust scaling
    )

}