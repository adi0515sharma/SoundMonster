package com.kft.soundmonster.data.models.SearchArtitstModels

import com.kft.soundmonster.data.models.Commons.Artist

data class Artists(
    val href: String,
    val items: List<Artist>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)