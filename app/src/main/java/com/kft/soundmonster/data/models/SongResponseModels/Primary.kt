package com.kft.soundmonster.data.models.SongResponseModels

data class Primary(
    val id: String,
    val image: List<Image>,
    val name: String,
    val role: String,
    val type: String,
    val url: String
)