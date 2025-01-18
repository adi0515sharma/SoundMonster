package com.kft.soundmonster.data.models.Commons

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize

data class Followers(
    val href: String?,
    val total: Long
): Parcelable, Serializable