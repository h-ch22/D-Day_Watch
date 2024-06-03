package com.cj.d_daywatch.presentation.frameworks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.edit
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.dialog.DialogDefaults
import com.cj.d_daywatch.R
import com.cj.d_daywatch.complication.models.COMPLICATION_D_DAY_KEY
import com.cj.d_daywatch.complication.models.COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY
import com.cj.d_daywatch.complication.models.dataStore
import com.cj.d_daywatch.presentation.frameworks.helper.DDayHelper
import com.cj.d_daywatch.presentation.frameworks.models.DDayDataModel
import com.cj.d_daywatch.presentation.frameworks.models.DDayListModel
import com.cj.d_daywatch.presentation.theme.DDayWatchTheme
import com.cj.d_daywatch.presentation.theme.gray
import kotlinx.coroutines.launch


@Composable
fun MainView() {
    val context = LocalContext.current
    val helper = DDayHelper(context)

    var showProgress by remember {
        mutableStateOf(true)
    }

    var showAlert by remember {
        mutableStateOf(false)
    }

    var selectedDDay by remember {
        mutableStateOf<DDayDataModel?>(null)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        helper.getList()

        showProgress = false
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.surface) {
        if (showProgress) {
            CircularProgressIndicator()
        } else {
            if (!DDayHelper.dDayList.isEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(10.dp)
                ) {
                    items(DDayHelper.dDayList) { dDay ->
                        DDayListModel(data = dDay) {
                            selectedDDay = dDay
                            showAlert = true
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = stringResource(id = R.string.TXT_ADD_D_DAY_BY_PHONE),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showAlert) {
            Dialog(onDismissRequest = { showAlert = false },
                properties = DialogProperties(false, false),
                content = {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.TXT_TITLE_OF_CONFIRM_SET_COMPLICATION),
                            color = gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = stringResource(id = R.string.TXT_CONTENTS_OF_CONFIRM_SET_COMPLICATION),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showAlert = false }) {
                                Icon(
                                    imageVector = Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = gray,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(onClick = {
                                if (selectedDDay != null) {
                                    coroutineScope.launch {
                                        context.dataStore.edit {
                                            it[COMPLICATION_D_DAY_KEY] = selectedDDay!!.date
                                            it[COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY] = selectedDDay!!.setBaseDayToOneDay
                                        }
                                    }
                                }

                                showAlert = false
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.primary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                    }
                })
        }
    }
}

@Preview(device = "id:wearos_small_round")
@Composable
fun MainViewPreview() {
    MainView()
}