package com.kft.soundmonster.ui.components.Navigations

import android.os.Parcelable
import com.kft.soundmonster.data.models.Commons.Artist
import kotlinx.parcelize.Parcelize


@Parcelize
data class ScreenName(val route: String, val arguments: List<Artist> = emptyList()) : Parcelable
