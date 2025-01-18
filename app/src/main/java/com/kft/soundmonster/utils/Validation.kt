package com.kft.soundmonster.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.models.Commons.Track
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.formatNumber(): String {


    return when {
        this >= 1_000_000 -> String.format("%dM", this / 1_000_000)
        this >= 1_000 -> String.format("%dK", this / 1_000)
        else -> this.toString()
    }
}


fun MutableList<Artist>.isArtistSelected(id : String) : Boolean{
    Log.e("SoundMonster", (this.find { it.id == id } != null).toString())
    return this.find { it.id == id } != null
}

fun List<Track>.getTimeOfTotalAlbum()  : String{
    var time : Long = 0
    this.forEach {
        time += it.duration_ms
    }
    return time.formatTime()
}
fun Long.formatTime(): String {
    val hours = this / 3600000
    val minutes = (this % 3600000) / 60000

    return when {
        hours > 0 && minutes > 0 -> "${hours} h ${minutes} min"
        hours > 0 -> "${hours} h"
        minutes > 0 -> "${minutes} min"
        else -> "0min" // Optional, for when both are 0
    }
}

fun String.formatDate(): String {

    val inputFormatter = SimpleDateFormat("yyyy-M-d", Locale.ENGLISH)
    val outputFormatter = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

    val parsedDate = inputFormatter.parse(this)
    return outputFormatter.format(parsedDate!!)



}

fun formatTimeForPlayer(millis: Long): String {
    val minutes = (millis / 1000) / 60
    val seconds = (millis / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}


fun getAudioThumbnail(audioFilePath: String): Bitmap? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(audioFilePath)
        // Extract embedded picture (album art) as a byte array
        val artworkBytes = retriever.embeddedPicture
        if (artworkBytes != null) {
            // Convert the byte array into a Bitmap
            BitmapFactory.decodeByteArray(artworkBytes, 0, artworkBytes.size)
        } else {
            null // No artwork embedded in the audio file
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release() // Release the retriever resources
    }
}