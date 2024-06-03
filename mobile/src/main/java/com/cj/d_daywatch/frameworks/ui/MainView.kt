package com.cj.d_daywatch.frameworks.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cj.d_daywatch.R
import com.cj.d_daywatch.d_day.helper.DDayHelper
import com.cj.d_daywatch.d_day.models.DDayDataModel
import com.cj.d_daywatch.d_day.models.DDayListModel
import com.cj.d_daywatch.d_day.ui.AddDDayView
import com.cj.d_daywatch.d_day.ui.DDayDetailView
import com.cj.d_daywatch.frameworks.models.ADD_D_DAY
import com.cj.d_daywatch.frameworks.models.DETAILS
import com.cj.d_daywatch.frameworks.models.HOME
import com.cj.d_daywatch.frameworks.models.SETTINGS
import com.cj.d_daywatch.settings.ui.SettingsView
import com.cj.d_daywatch.ui.theme.DDayWatchTheme
import com.cj.d_daywatch.ui.theme.dDayBG1
import com.cj.d_daywatch.ui.theme.dDayBG2
import com.cj.d_daywatch.ui.theme.dDayBG3
import com.cj.d_daywatch.ui.theme.dDayBG4
import com.cj.d_daywatch.ui.theme.dDayBG5
import com.cj.d_daywatch.userManagement.helper.UserManagement
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MainView() {
    val navController = rememberNavController()
    val helper = DDayHelper()
    val context = LocalContext.current

    var showProgress by remember {
        mutableStateOf(true)
    }

    var showAlert by remember {
        mutableStateOf(false)
    }

    var selectedDDay by remember {
        mutableStateOf<DDayDataModel?>(null)
    }

    BackHandler {
        Runtime.getRuntime().runFinalization()
    }

    NavHost(navController = navController, startDestination = HOME) {
        composable(ADD_D_DAY) {
            AddDDayView(parent = navController)
        }

        composable(DETAILS){
            selectedDDay?.let { DDayDetailView(data = it, parent = navController) }
        }

        composable(SETTINGS){
            SettingsView(parent = navController)
        }

        composable(HOME) {
            DDayWatchTheme {
                LaunchedEffect(key1 = true) {
                    helper.getDDayList(context) { queryResult ->
                        showProgress = false

                        if (!queryResult) {
                            showAlert = true
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_main),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                                Text(
                                    text = stringResource(id = R.string.app_name).uppercase(
                                        Locale.ROOT
                                    )
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(onClick = {
                                    navController.navigate(SETTINGS) {
                                        popUpTo(HOME) {
                                            inclusive = false
                                        }
                                    }
                                }) {
                                    Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(DDayHelper.dDayList){ item ->
                                    DDayListModel(data = item, onClickStartSource = {
                                        selectedDDay = item
                                        navController.navigate(DETAILS){
                                            popUpTo(HOME){
                                                inclusive = false
                                            }
                                        }
                                    })
                                }
                            }
                        }

                        FloatingActionButton(onClick = {
                            navController.navigate(ADD_D_DAY) {
                                popUpTo(HOME) {
                                    inclusive = false
                                }
                            }
                        }, modifier = Modifier.align(Alignment.BottomEnd)) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                        }
                    }

                }

                if (showAlert) {
                    AlertDialog(onDismissRequest = { showAlert = false }, confirmButton = {
                        TextButton(
                            onClick = { showAlert = false }) {
                            Text(text = stringResource(id = R.string.TXT_OK))
                        }
                    }, icon = {
                        Icon(imageVector = Icons.Rounded.Warning, contentDescription = null)
                    }, title = {
                        Text(text = stringResource(id = R.string.TXT_ERROR))
                    }, text = {
                        Text(text = stringResource(id = R.string.TXT_GET_DATA_ERROR))
                    })
                }
            }
        }
    }
}

@Preview
@Composable
fun MainViewPreview() {
    MainView()
}