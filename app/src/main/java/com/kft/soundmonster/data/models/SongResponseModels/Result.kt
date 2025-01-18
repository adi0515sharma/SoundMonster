package com.kft.soundmonster.data.models.SongResponseModels

data class Result(
    val album: Album,
    val artists: Artists,
    val copyright: String,
    val downloadUrl: List<DownloadUrl>,
    val duration: Int,
    val explicitContent: Boolean,
    val hasLyrics: Boolean,
    val id: String,
    val image: List<Image>,
    val label: String,
    val language: String,
    val lyricsId: Any,
    var name: String,
    val playCount: Int,
    val releaseDate: Any,
    val type: String,
    val url: String,
    val year: String
)