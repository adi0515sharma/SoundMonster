package com.kft.soundmonster.domain.ViewModels


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kft.soundmonster.data.models.SongResponseModels.Result
import com.kft.soundmonster.data.models.SongResponseModels.SongResponseModels
import com.kft.soundmonster.data.repo.SpotifyWebRepo
import com.kft.soundmonster.service.MusicService
import com.kft.soundmonster.ui.components.getAlbumImage
import com.kft.soundmonster.utils.ApiResult
import com.kft.soundmonster.utils.PaletteUtils.getDominateColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

//
//@HiltViewModel
//class SongScreenViewModel @Inject constructor(val spotifyWebRepo: SpotifyWebRepo) : ViewModel(){
//
//    var musicPlayerService: MutableStateFlow<MusicService?> = MutableStateFlow(null)
//    var isBound : MutableStateFlow<Boolean> = MutableStateFlow(false)
//
//    var isPlaying : MutableStateFlow<Boolean?> = MutableStateFlow(false)
//    var currentPos : MutableStateFlow<Long> = MutableStateFlow(0L)
//
//    private var _currentSong : MutableStateFlow<ApiResult<Result>> = MutableStateFlow(ApiResult.Loading)
//    val currentSong : StateFlow<ApiResult<Result>> = _currentSong
//
//    init {
//        viewModelScope.launch(Dispatchers.IO){
//
//            currentSong.collectLatest {
//                if(it !is ApiResult.Success){
//                    return@collectLatest
//                }
//
//                musicPlayerService.value?.initializeMediaPlayer(it.data)
//            }
//
//        }
//
//        // Collect isPlaying state when musicPlayerService is non-null
//        viewModelScope.launch {
//            musicPlayerService.filterNotNull().flatMapLatest { service ->
//                service.isPlaying
//            }.collectLatest { isPlayingValue ->
//                isPlaying.value = isPlayingValue
//            }
//        }
//
//        // Collect currentPositionInMillis when musicPlayerService is non-null
//        viewModelScope.launch {
//            musicPlayerService.filterNotNull().flatMapLatest { service ->
//                service.currentPositionInMillis
//            }.collectLatest { position ->
//                Log.e("SoundMonster", position.toString())
//                currentPos.value = position
//            }
//        }
//    }
//
//    public fun createService(context: Context){
//        Intent(context, MusicService::class.java).also { intent ->
//            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
//        }
//    }
//
//
//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as MusicService.MusicPlayerBinder
//            musicPlayerService.value = binder.getService()
//
//            isBound.value = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            musicPlayerService.value = null
//            isBound.value = false
//        }
//    }
//
//    fun toggleSongState(){
//        if(!isBound.value){
//            return
//        }
//        musicPlayerService.value?.toggleSongState()
//    }
//
//
//    fun forwardSong(){
//        if(!isBound.value){
//            return
//        }
//        musicPlayerService.value?.forwardSong()
//    }
//
//
//
//    fun backwardSong(){
//        if(!isBound.value){
//            return
//        }
//        musicPlayerService.value?.backwardSong()
//    }
//
//
//    fun seekToSong(value : Int){
//        if(!isBound.value){
//            return
//        }
//        musicPlayerService.value?.seekToSong(value)
//    }
//
//    fun fetchSong(name : String){
//
//        viewModelScope.launch{
//            spotifyWebRepo.getSong(name).collectLatest {
//                _currentSong.value = it
//            }
//
//        }
//    }
//
//    fun disposeScreen(context: Context){
//        if (!isBound.value) {
//            return
//        }
//        context.unbindService(serviceConnection)
//        isBound.value = false
//    }
//
//
//
//}