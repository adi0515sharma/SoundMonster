package com.kft.soundmonster.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kft.soundmonster.R
import com.kft.soundmonster.data.models.Commons.Artist

@Composable
fun SingleArtistComposable(item : Artist, onCancel : ()->Unit){



    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)){
        Box(
            contentAlignment = Alignment.TopEnd,
            ) {
            // Circular Image
            AsyncImage(
                model = getArtistImage(item),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            // Cross Icon
            Image(
                painter = painterResource(R.drawable.baseline_cancel_24),
                contentDescription = "Remove",
                colorFilter = ColorFilter.tint(Color.Green),
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onCancel() }
                    .align(Alignment.TopEnd) // Ensure it's at the top-right
                    .offset(x = 14.dp, y = -10.dp) // Shift icon 4.dp to the right

            )
        }

// Spacer and Text
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = item.name,
            maxLines = 1,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(80.dp),
        )
    }


//    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)){
//        AsyncImage(
//            model = getArtistImage(item),
//            contentDescription = null,
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(50.dp)
//                .clip(CircleShape)
//                .border(2.dp, Color.Gray, CircleShape)
//        )
//        Spacer(modifier = Modifier.height(5.dp))
//        Text(
//            text = item.name,
//            maxLines = 1,
//            color = Color.White,
//            textAlign = TextAlign.Center,
//            modifier = Modifier.width(80.dp),
//
//        )
//    }
}