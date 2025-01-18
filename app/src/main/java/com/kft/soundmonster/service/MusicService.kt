package com.kft.soundmonster.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.kft.soundmonster.R
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.data.models.SongResponseModels.Result
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
    @Inject
    lateinit var spotifyWebRepo: SpotifyWebRepo

    private val binder = MusicPlayerBinder()
    private var mediaPlayer: MediaPlayer? = null

    var isPlaying : MutableStateFlow<Boolean?> = MutableStateFlow(false)
    var currentMusic : MutableStateFlow<ApiResult<Result?>?> = MutableStateFlow(null)
    var album : MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    var currentPositionInMillis : MutableStateFlow<Long> = MutableStateFlow(0L)
    var maxDuration : MutableStateFlow<Long> = MutableStateFlow(0L)

    var jobForMusicState : Job?= null
    var jobForMusicTimming : Job? = null
    var jobForMusicFetch : Job? = null
    var currentMusicPostion : MutableStateFlow<Int?> = MutableStateFlow(null)
    var callback : MediaSessionCompat.Callback? = null
    inner class MusicPlayerBinder : Binder() {
        fun setAlbum(list : List<Track>){
            this@MusicService.album.value = list
        }
        fun getCurrentDuration() = this@MusicService.currentPositionInMillis
        fun getCurrentMusic() = this@MusicService.currentMusic

        fun getCurrentMusicState() = this@MusicService.isPlaying

        fun getMaxDuration() = this@MusicService.maxDuration

        fun toggleMusicState() {
            this@MusicService.toggleSongState()
        }

        fun forwardMusic() {
            this@MusicService.forwardSong()
        }

        fun backwardMusic() {
            this@MusicService.backwardSong()
        }

        fun seekTo(pos : Int) {
            this@MusicService.seekToSong(pos)
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            currentMusic.debounce(300).collectLatest { result ->
                when (result) {
                    is ApiResult.Success -> {
                        initializeMediaPlayer()
                    }
                    is ApiResult.Loading -> {
                        withContext(Dispatchers.Main) {
                        }
                    }
                    is ApiResult.Error -> {
//                        withContext(Dispatchers.Main) {
//                            Toast.makeText(this@MusicService, result.message, Toast.LENGTH_LONG).show()
//                        }
                    }
                    else -> {
                        // Handle unexpected states if needed
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            currentMusicPostion.debounce(300).collectLatest { position ->
                if (position == null || position == -1) {
                    return@collectLatest
                }

                // Cancel the previous API call job if it's still running
                jobForMusicFetch?.cancel()
                jobForMusicFetch = launch {
                    spotifyWebRepo.getSong(album.value[position].name).collectLatest { result ->

                        if(result is ApiResult.Success){

                            var data = result.data.copy(
                                name = album.value[position].name,
                                lyricsId = "",
                                releaseDate = ""
                            )
                            currentMusic.value = ApiResult.Success(data)
                        }
                        else{
                            currentMusic.value = result

                        }

                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        intent?.run {
            currentMusicPostion.value = extras?.getInt("pos", -1)
        }

        return START_STICKY

    }
    suspend fun fetchBitmapWithCoil(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val loader = ImageLoader(this@MusicService)
                val request = ImageRequest.Builder(this@MusicService)
                    .data(imageUrl)
                    .allowHardware(false) // Disable hardware bitmaps for notifications
                    .build()

                val result = loader.execute(request)
                if (result is SuccessResult) {
                    result.drawable.toBitmap()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    var session : MediaSessionCompat? = null
    var playbackStateBuilder : PlaybackStateCompat.Builder? = null
    private fun sendNotification(){

        if(currentMusic.value !is ApiResult.Success){
            return
        }
        val item = (currentMusic.value as ApiResult.Success).data

        val style = androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(session?.sessionToken)

        val artistNames = item?.artists?.all?.map { it.name }?.joinToString(",")


        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = fetchBitmapWithCoil(item?.image?.get(2)?.url!!)
            val notification = NotificationCompat.Builder(this@MusicService, Constants.FOREGROUND_NOTIFICATION)
                .setContentTitle(item.name)
                .setStyle(style)
                .setContentText(artistNames)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(bitmap)
                .setOngoing(true)
                .build()


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if(ContextCompat.checkSelfPermission(this@MusicService, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                    startForeground(SystemClock.uptimeMillis().toInt(), notification)
                }
            }
            else{
                startForeground(SystemClock.uptimeMillis().toInt(), notification)
            }
        }
    }


    fun initializeMediaPlayer() {

        if(currentMusic.value !is ApiResult.Success){
            return
        }

        jobForMusicState?.cancel()
        jobForMusicTimming?.cancel()
        maxDuration.value = 0
        currentPositionInMillis.value = 0
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource((currentMusic.value as ApiResult.Success).data?.downloadUrl?.get(2)?.url)
                prepareAsync()
                setOnPreparedListener {
                    onPreparedListener()
                }
                setOnBufferingUpdateListener { mp, percent ->
                    onBufferListener(mp, percent)
                }
                setOnCompletionListener {

                    onCompletionListener(it)
                }
            } catch (e: IOException) {

            }
        }
    }

    private fun onCompletionListener(it : MediaPlayer) {
        isPlaying.value = false
        it.seekTo(0)
    }

    private fun onPreparedListener(){

        jobForMusicState = CoroutineScope(Dispatchers.IO).launch {
            isPlaying.collectLatest {

                if (mediaPlayer?.isPlaying == false && it == true) {
                    mediaPlayer?.start()
                } else if (mediaPlayer?.isPlaying == true && it == false) {
                    mediaPlayer?.pause()
                }

                delay(100)

                withContext(Dispatchers.Main){

                    val state = if(mediaPlayer?.isPlaying == true) PlaybackStateCompat.STATE_PLAYING else  PlaybackStateCompat.STATE_STOPPED
                    val playbackSpeed = 1f
                    playbackStateBuilder?.setState(state, mediaPlayer?.currentPosition?.toLong()?:0L, playbackSpeed)
                    session?.setPlaybackState(playbackStateBuilder?.build())
                }

            }
        }

        jobForMusicTimming = CoroutineScope(Dispatchers.IO).launch {

            while (true){
                try{
                    if(mediaPlayer?.isPlaying == true){
                        currentPositionInMillis.value = mediaPlayer?.currentPosition?.toLong()?:0L
                        delay(100)

                    }
                }
                catch (e : Exception){
                    e.printStackTrace()
                }
            }

        }

        maxDuration.value = mediaPlayer?.duration?.toLong()?:0L
        isPlaying.value = true

        session = MediaSessionCompat(this, "music")
        playbackStateBuilder = PlaybackStateCompat.Builder()
        val stateActions = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        playbackStateBuilder?.setActions(stateActions)

        playbackStateBuilder?.setState(PlaybackStateCompat.STATE_NONE, 0L, 1f)
        session?.setPlaybackState(playbackStateBuilder?.build())

        val mediaMetaData_builder = MediaMetadataCompat.Builder();
        mediaMetaData_builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,maxDuration.value)
        session?.setMetadata(mediaMetaData_builder.build());

        callback = object: MediaSessionCompat.Callback() {
            override fun onPlay() {
                // start playback
                playMusic()
            }

            override fun onPause() {
                // pause playback
                pauseMusic()
            }

            override fun onSkipToPrevious() {
                backwardSong()
            }

            override fun onSkipToNext() {
                // skip to next
                forwardSong()

            }

            override fun onSeekTo(pos: Long) {
                seekToSong(pos.toInt())
            }

            override fun onCustomAction(action: String, extras: Bundle?) {

            }

        }

        session?.setCallback(callback)
        sendNotification()
    }

    private fun onBufferListener(it : MediaPlayer, percent : Int){
        isPlaying.value = if(percent == 0){
            null
        } else{
            it.isPlaying
        }
    }

    fun playMusic(){
        Log.e("SoundMonster", "play music")
        if(mediaPlayer?.isPlaying == false){
            isPlaying.value = true

        }
    }

    fun pauseMusic(){
        Log.e("SoundMonster", "pause music")
        if(mediaPlayer?.isPlaying == true){
            isPlaying.value = false

        }
    }

    fun toggleSongState(){
        isPlaying.value = !isPlaying.value!!
    }

    fun forwardSong(){
        if(currentMusicPostion.value == null || currentMusicPostion.value == -1){
            return
        }

        if(currentMusicPostion.value!! < album.value.size-1){
          currentMusicPostion.value = currentMusicPostion.value!! + 1
        }
        else{
            currentMusicPostion.value = 0
        }
    }

    fun backwardSong(){
        if(currentMusicPostion.value == null || currentMusicPostion.value == -1){
            return
        }

        if(currentMusicPostion.value!! > 0){
            currentMusicPostion.value = currentMusicPostion.value!! - 1
        }
        else{
            currentMusicPostion.value = album.value.size-1
        }
    }

    fun seekToSong(value : Int){
        mediaPlayer?.seekTo(value)
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

}



