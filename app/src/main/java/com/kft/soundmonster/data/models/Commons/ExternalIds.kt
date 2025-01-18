package com.kft.soundmonster.data.models.Commons


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class ExternalIds(
    val isrc: String
) : Parcelable, Serializable