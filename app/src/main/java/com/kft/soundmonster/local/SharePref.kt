package com.kft.soundmonster.local

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharePref (contexts: Context){


    val ACCESS_TOKEN = "ACCESS_TOKEN"
    val SELECTED_ARTIST = "SELECTED_ARTIST"

    var sharedPreferences : SharedPreferences = contexts.getSharedPreferences("SpotifyPreference", Context.MODE_PRIVATE)


    fun addAccessToken(value : String){
        val editor = sharedPreferences.edit()
        editor.putString(ACCESS_TOKEN, value) // Storing a string
        editor.apply() // Apply changes asynchronously
    }


    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN, null)


    fun setSelectedArtist(value : List<String>){
        val editor = sharedPreferences.edit()

        editor.putStringSet(SELECTED_ARTIST, value.toSet()) // Storing a string
        editor.apply() // Apply changes asynchronously
    }


    fun getSelectedArtist() : MutableSet<String>? {
        return sharedPreferences.getStringSet(SELECTED_ARTIST, emptySet())
    }



}