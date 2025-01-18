package com.kft.soundmonster.data.models.Commons

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize

data class Image(
    val height: Int,
    val url: String,
    val width: Int
): Parcelable, Serializable