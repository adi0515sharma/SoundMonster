package com.kft.soundmonster.data.models.AlbumDetailsModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class ExternalIds(
    val upc: String
) : Parcelable, Serializable