package com.kft.soundmonster.data.models.AlbumDetailsModels

import android.os.Parcelable
import com.kft.soundmonster.data.models.Commons.Artist
import com.kft.soundmonster.data.models.Commons.Copyright
import com.kft.soundmonster.data.models.Commons.ExternalUrls
import com.kft.soundmonster.data.models.Commons.Image


import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class AlbumDetailResponseModel(
    val album_type: String,
    val artists: List<Artist>,
    val available_markets: List<String>,
    val copyrights: List<Copyright>,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrls,
    val genres: List<String?>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val tracks: Tracks,
    val type: String,
    val uri: String
) : Parcelable, Serializable