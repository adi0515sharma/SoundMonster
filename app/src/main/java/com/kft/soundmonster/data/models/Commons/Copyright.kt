package com.kft.soundmonster.data.models.Commons

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Copyright(
    val text: String,
    val type: String
) : Parcelable, Serializable