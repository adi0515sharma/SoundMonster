package com.kft.soundmonster.data.models.ArtistAlbumModels

import android.os.Parcelable
import com.kft.soundmonster.data.models.Commons.Album
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class AlbumResponseModel(
    val href: String,
    val items: List<Album>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: String?,
    val total: Int
) : Parcelable, Serializable