package com.kft.soundmonster.data.models.Commons

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Track(
    val album: Album?,
    val artists: List<Artist>?,
    val available_markets: List<String>?,
    val disc_number: Int,
    val duration_ms: Long,
    val explicit: Boolean,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String,
    var bitmap : Bitmap?=null
) : Parcelable, Serializable

