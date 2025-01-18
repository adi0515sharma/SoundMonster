package com.kft.soundmonster.utils

import android.R.attr
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.kft.soundmonster.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object PaletteUtils {

    suspend fun getDominateColor(context: Context, imageUrl: String): Color {
        return withContext(Dispatchers.IO) {
            try {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false) // Prevent hardware bitmaps if you need to manipulate the bitmap
                    .build()

                val result = loader.execute(request)
                if (result is SuccessResult) {
                    val bitmap = (result.drawable as BitmapDrawable).bitmap

                    Color(Palette.from(bitmap).generate().getDominantColor( ContextCompat.getColor(context, R.color.black)))


                } else {
                    Color.Black
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Color.Black
            }
        }
    }


}