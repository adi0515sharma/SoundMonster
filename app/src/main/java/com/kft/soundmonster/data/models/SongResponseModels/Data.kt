package com.kft.soundmonster.data.models.SongResponseModels

data class Data(
    val results: List<Result>,
    val start: Int,
    val total: Int
)