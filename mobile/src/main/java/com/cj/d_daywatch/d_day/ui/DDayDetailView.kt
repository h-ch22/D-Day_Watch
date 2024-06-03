package com.cj.d_daywatch.d_day.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cj.d_daywatch.R
import com.cj.d_daywatch.d_day.helper.DDayHelper
import com.cj.d_daywatch.d_day.models.DDayAnniversaryListModel
import com.cj.d_daywatch.d_day.models.DDayDataModel
import com.cj.d_daywatch.d_day.models.DeleteDDayAlertModel
import com.cj.d_daywatch.d_day.models.calculateDate
import com.cj.d_daywatch.frameworks.ui.convertIntToColor
import com.cj.d_daywatch.ui.theme.DDayWatchTheme
import com.cj.d_daywatch.ui.theme.gray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDayDetailView(data: DDayDataModel, parent: NavHostController) {
    val dateList = remember {
        mutableStateListOf<LocalDate>()
    }

    val dateFormatter = SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA)

    val helper = DDayHelper()

    val context = LocalContext.current

    var localDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var showConfirmAlert by remember {
        mutableStateOf(false)
    }

    var showAlert by remember {
        mutableStateOf(false)
    }

    var alertModel by remember {
        mutableStateOf<DeleteDDayAlertModel?>(null)
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    DDayWatchTheme {
        Scaffold(
            topBar = {
                LargeTopAppBar(title = { Text(text = data.title) }, navigationIcon = {
                    IconButton(onClick = { parent.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, actions = {
                    if(!showProgress){
                        IconButton(onClick = {
                            showConfirmAlert = true
                        }) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                        }
                    } else{
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                })
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                LaunchedEffect(Unit) {
                    val instant = dateFormatter.parse(data.date)?.toInstant()
                    val zdt = instant?.atZone(ZoneId.systemDefault())
                    localDate = zdt?.toLocalDate()

                    for (i in 0 until 100) {
                        dateList.add(localDate!!.plusDays(i * 100L))
                    }
                }

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                if (data.backgroundType == 0) convertIntToColor(
                                    data.backgroundColor ?: 0
                                ) else Color.Transparent
                            )
                    ) {
                        if (data.image != null && data.backgroundType == 1) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(data.image).build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        ) {
                            Text(
                                text = data.title,
                                color = if (data.textColor == 0) Color.White else Color.Black
                            )

                            AnimatedVisibility(visible = data.date != "") {
                                val diff = calculateDate(data.date, data.setBaseDayToOneDay)
                                Text(
                                    text = if (diff >= 0) "D+${diff}" else "D${diff}",
                                    color = if (data.textColor == 0) Color.White else Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                    }

                    LazyColumn {
                        itemsIndexed(dateList) { index, date ->
                            DDayAnniversaryListModel(
                                title = "${index * 100}${stringResource(id = R.string.TXT_ANNIVERSAY_UNIT)}",
                                date = dateFormatter.format(
                                    Date.from(
                                        date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                ),
                                eventName = data.title
                            )

                            HorizontalDivider(thickness = 0.5.dp, color = gray)

                            if (index == dateList.size - 1) {
                                dateList.add(date.plusDays(100))
                            }
                        }
                    }


                }


                if (showConfirmAlert) {
                    AlertDialog(onDismissRequest = { showConfirmAlert = false }, confirmButton = {
                        TextButton(onClick = {
                            showProgress = true

                            helper.delete(data.id, context){
                                showConfirmAlert = false
                                alertModel = if(it) DeleteDDayAlertModel.SUCCESS else DeleteDDayAlertModel.FAIL
                                showAlert = true
                            }
                        }) {
                            Text(
                                text = stringResource(id = R.string.TXT_OK)
                            )
                        }
                    }, dismissButton = {
                        TextButton(onClick = { showConfirmAlert = false }) {
                            Text(text = stringResource(id = R.string.TXT_CANCEL))
                        }
                    }, icon = {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = null)
                    }, title = {
                        Text(text = stringResource(id = R.string.TXT_DELETE_D_DAY))
                    }, text = {
                        Text(text = stringResource(id = R.string.TXT_CONFIRM_DELETE_D_DAY))
                    })
                }

                if(showAlert){
                    AlertDialog(onDismissRequest = { showAlert = false }, confirmButton = {
                        TextButton(onClick = {
                            if(alertModel == DeleteDDayAlertModel.SUCCESS){
                                showAlert = false
                                parent.popBackStack()
                            } else{
                                showAlert = false
                            }
                        }) {
                            Text(
                                text = stringResource(id = R.string.TXT_OK)
                            )
                        }
                    }, icon = {
                        Icon(imageVector = if(alertModel == DeleteDDayAlertModel.SUCCESS) Icons.Rounded.CheckCircle else Icons.Rounded.Warning, contentDescription = null)
                    }, title = {
                        Text(text = stringResource(id = R.string.TXT_DELETE_D_DAY))
                    }, text = {
                        Text(text = stringResource(id = if(alertModel == DeleteDDayAlertModel.SUCCESS) R.string.TXT_DELETED_SUCCESSFULLY else R.string.TXT_DELETE_ERROR))
                    })
                }
            }
        }
    }
}

@Preview
@Composable
fun DDayDetailViewPreview() {
    DDayDetailView(
        data = DDayDataModel("", "D-Day", "2024. 03. 01.", true, 0, null, 0, 0),
        rememberNavController()
    )
}