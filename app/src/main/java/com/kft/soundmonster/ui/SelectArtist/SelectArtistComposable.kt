package com.kft.soundmonster.ui.SelectArtist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kft.soundmonster.domain.ViewModels.SelectArtistViewModel
import com.kft.soundmonster.ui.components.CustomSearchBar
import com.kft.soundmonster.ui.components.Navigations.LocalNavController
import com.kft.soundmonster.ui.components.Navigations.HomeNavigationScreen.getHomeRoute
import com.kft.soundmonster.ui.components.SelectArtist
import com.kft.soundmonster.ui.components.SingleArtistComposable
import com.kft.soundmonster.utils.isArtistSelected



@Composable
fun SelectArtistComposable(viewModel : SelectArtistViewModel) {
    val navController = LocalNavController.current


    var searchText by rememberSaveable { mutableStateOf("") }

    // Update the ViewModel's query whenever searchText changes
    LaunchedEffect(searchText) {
        viewModel.setSearchQuery(searchText)
    }

    // Collect the cached flow
    val artistListOfItems = viewModel.artistListFlow.collectAsLazyPagingItems()

    val listOfSelectedArtist = viewModel.selectedArtistList.collectAsStateWithLifecycle()


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->


        Column(modifier = Modifier.padding(innerPadding)) {

            CustomSearchBar(searchText) {
                searchText = it
            }


            if (listOfSelectedArtist.value.size > 0) {
                LazyRow(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp)) {
                    items(listOfSelectedArtist.value.size) {

                        SingleArtistComposable(listOfSelectedArtist.value[it]) {
                            viewModel.updateSelectedArtistList(listOfSelectedArtist.value[it], "-")
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {

                SelectArtist(artistListOfItems = artistListOfItems, listOfSelectedArtist.value) {
                    if (listOfSelectedArtist.value.isArtistSelected(it.id)) {

                        viewModel.updateSelectedArtistList(it, "-")

                    } else {
                        viewModel.updateSelectedArtistList(it, "+")

                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .clickable {

                        viewModel.setArtistList()
                        val route = getHomeRoute(viewModel.selectedArtistList.value.map { it.id }.toMutableList())
                        navController.navigate(route) {
                            popUpTo(navController.currentDestination?.id ?: 0) {
                                inclusive = true
                            }
                        }
                    }
                    .background(color = Color.LightGray)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("PROCEED")
            }


        }

    }
}


