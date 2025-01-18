package com.kft.soundmonster

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.kft.soundmonster.data.repo.SpotifyAuthRepo
import com.kft.soundmonster.local.SharePref
import com.kft.soundmonster.service.MusicService
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants
import com.kft.soundmonster.utils.PaletteUtils.getDominateColor
import com.spotify.sdk.android.auth.AuthorizationHandler
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.sdk.android.auth.app.SpotifyAuthHandler
import com.spotify.sdk.android.auth.app.SpotifyNativeAuthUtil
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class SoundMonsterApp : Application() {

    @Inject lateinit var spotifyAuthRepo: SpotifyAuthRepo
    @Inject lateinit var sharePref: SharePref

    var musicService : MusicService.MusicPlayerBinder? = null

    val connection = object  : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = service as MusicService.MusicPlayerBinder

        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate() {
        super.onCreate()

        runBlocking {

            val localAccessToken = sharePref.getAccessToken()

            if(localAccessToken != null){
                return@runBlocking
            }

            val accessToken  = spotifyAuthRepo.getAccessToken()
            if(accessToken is ApiResult.Error){
                Log.e("SoundMonsterApp", accessToken.message)
                return@runBlocking
            }

            sharePref.addAccessToken((accessToken as ApiResult.Success).data.accessToken)


        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.FOREGROUND_NOTIFICATION,
                "Music Player Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, connection, BIND_AUTO_CREATE)

    }


}