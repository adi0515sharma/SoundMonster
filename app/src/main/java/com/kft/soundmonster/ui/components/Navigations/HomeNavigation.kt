package com.kft.soundmonster.ui.components.Navigations

import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.kft.soundmonster.SoundMonsterApp
import com.kft.soundmonster.data.models.Commons.Track
import com.kft.soundmonster.domain.ViewModels.SelectArtistViewModel
import com.kft.soundmonster.local.SharePref
import com.kft.soundmonster.service.MusicService
import com.kft.soundmonster.ui.AlbumPlaylistScreen.AlbumComposable
import com.kft.soundmonster.ui.HomeScreen.HomeComposable
import com.kft.soundmonster.ui.PlayerComposable.FullScreenPlayerComposable
import com.kft.soundmonster.ui.SelectArtist.SelectArtistComposable


val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavHostController provided")
}

@Composable
fun HomeNavigation(navController: NavHostController, sharePref: SharePref) {


    var initialRoute by rememberSaveable { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        val currentList = sharePref.getSelectedArtist()?: emptyList()
        if(currentList.isEmpty()){
            initialRoute = HomeNavigationScreen.SELECT_ARTIST
        }
        else{
            initialRoute = HomeNavigationScreen.HOME
        }
    }
    if(initialRoute == null){
        return
    }
    CompositionLocalProvider(LocalNavController provides navController) {

        NavHost(
            modifier = Modifier.background(Color.Black),
            navController = navController,
            startDestination = initialRoute!!
        ) {
            composable(
                HomeNavigationScreen.SELECT_ARTIST
            ) {
                val viewModel = hiltViewModel<SelectArtistViewModel>()

                SelectArtistComposable(viewModel)
            }
            composable(HomeNavigationScreen.HOME) { backStackEntry->
                val artistList : List<String> = sharePref.getSelectedArtist()?.toMutableList()?: listOf<String>()
                HomeComposable(artistList)
            }

            composable(HomeNavigationScreen.ALBUM_ITEMS) { backStackEntry->

                val id = backStackEntry.arguments?.getString("id") ?: ""
                AlbumComposable(id)

            }

            composable(HomeNavigationScreen.PLAYER_ROUTE) { backStackEntry->

                val tracks = navController.previousBackStackEntry?.savedStateHandle?.get<List<Track>>("TRACKS") ?: return@composable
                val item = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("ITEM") ?: return@composable
                val context = LocalContext.current

                LaunchedEffect(tracks) {
                    (context.applicationContext as SoundMonsterApp).musicService?.setAlbum(tracks)
                }

                LaunchedEffect(item) {
                    val intent = Intent(context, MusicService::class.java)
                        .apply {

                            val bundle = Bundle().apply {
                                putInt("pos", item)
                            }
                            putExtras(bundle)
                        }
                    context.startService(intent)
                    context.bindService(intent, (context.applicationContext as SoundMonsterApp).connection, BIND_AUTO_CREATE)
                }


                FullScreenPlayerComposable()

            }

        }
    }


}


object HomeNavigationScreen {
    val SELECT_ARTIST = "SELECT_ARTIST"
    val HOME = "HOME/{artistList}"
    val ALBUM_ITEMS = "ALBUM_ITEMS/{id}"
    val PLAYER_ROUTE = "PLAYER_ROUTE"


    fun getHomeRoute(artistList: MutableList<String>) : String{
        val newHome = HOME.replace("{artistList}", Gson().toJson(artistList))
        return newHome
    }

    fun getAlbumItemsRoute(id : String) : String{
        var route = ALBUM_ITEMS
        route = route.replace("{id}", id)
        return route
    }

    fun getPlayerRoute(track : Track) : String{
//        var route = PLAYER_ROUTE
//        route = route.replace("{data}", Gson().toJson(track))
        return PLAYER_ROUTE
    }

}
