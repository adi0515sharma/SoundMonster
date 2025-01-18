package com.kft.soundmonster.data.models.AlbumDetailsModels


import android.os.Parcelable
import com.kft.soundmonster.data.models.Commons.Track
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Tracks(
    val href: String,
    val items: List<Track>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
) : Parcelable, Serializable