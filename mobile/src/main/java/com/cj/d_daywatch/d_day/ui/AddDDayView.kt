package com.cj.d_daywatch.d_day.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.cj.d_daywatch.d_day.models.AddDDayAlertModel
import com.cj.d_daywatch.d_day.models.DDayBackgroundTypeModel
import com.cj.d_daywatch.frameworks.ui.convertMillisToDate
import com.cj.d_daywatch.ui.theme.DDayWatchTheme
import com.cj.d_daywatch.ui.theme.dDayBG1
import com.cj.d_daywatch.ui.theme.dDayBG2
import com.cj.d_daywatch.ui.theme.dDayBG3
import com.cj.d_daywatch.ui.theme.dDayBG4
import com.cj.d_daywatch.ui.theme.dDayBG5
import com.cj.d_daywatch.ui.theme.gray
import com.cj.d_daywatch.ui.theme.white
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun calculateForPreview(selectedDate: String, setOneDay: Boolean): Long {
    val dateFormatter = SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA)
    val current = dateFormatter.parse(
        dateFormatter.format(Date())
    )

    val selected = dateFormatter.parse(selectedDate)

    if (current != null && selected != null) {
        val diff = current.time - selected.time

        return if (!setOneDay) TimeUnit.DAYS.convert(
            diff, TimeUnit.MILLISECONDS
        ) else TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1
    }

    return 0L
}

fun getAddDDayAlertContents(
    alertType: AddDDayAlertModel,
    context: Context,
    messageType: Int
): String {
    return when (alertType) {
        AddDDayAlertModel.SUCCESS -> if (messageType == 0) context.resources.getString(R.string.TXT_DONE) else context.resources.getString(
            R.string.TXT_UPLOADED
        )

        AddDDayAlertModel.EMPTY_FIELD -> if (messageType == 0) context.resources.getString(R.string.TXT_EMPTY_FIELD) else context.resources.getString(
            R.string.TXT_EMPTY_FIELD_CONTENTS
        )

        AddDDayAlertModel.FAIL -> if (messageType == 0) context.resources.getString(R.string.TXT_ERROR) else context.resources.getString(
            R.string.TXT_ERROR_UPLOAD
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDDayView(parent: NavHostController) {
    var title by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    var calculateFirstDayAsOne by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showProgress by remember {
        mutableStateOf(false)
    }

    var dDayBackgroundType by remember {
        mutableStateOf(DDayBackgroundTypeModel.COLOR)
    }

    var selectedColor by remember {
        mutableIntStateOf(0)
    }

    var selectedTextColor by remember {
        mutableStateOf("#FFFFFF")
    }

    var showAlert by remember {
        mutableStateOf(false)
    }

    var showConfirmDialog by remember {
        mutableStateOf(false)
    }

    var alertType by remember {
        mutableStateOf<AddDDayAlertModel?>(null)
    }

    val colors = listOf(dDayBG1, dDayBG2, dDayBG3, dDayBG4, dDayBG5)
    val scrollState = rememberScrollState()
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) {
            selectedImage = it
        }
    val context = LocalContext.current
    val helper = DDayHelper()

    DDayWatchTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(topBar = {
                LargeTopAppBar(title = { Text(stringResource(id = R.string.TXT_ADD_D_DAY)) },
                    navigationIcon = {
                        IconButton(onClick = { parent.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.Close, contentDescription = null
                            )
                        }
                    })
            }) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(it)
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(text = stringResource(id = R.string.TXT_TITLE_OF_D_DAY))
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Title, contentDescription = null)
                        },
                        trailingIcon = {
                            if (title.isNotEmpty()) {
                                IconButton(onClick = { title = "" }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Cancel,
                                        contentDescription = null
                                    )
                                }
                            }

                        })

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.TXT_DATE_OF_D_DAY),
                        fontSize = 12.sp,
                        color = gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (date != "") date else stringResource(id = R.string.TXT_NO_D_DAY_SELECTED))

                        Spacer(modifier = Modifier.weight(1f))

                        TextButton(onClick = { showDatePicker = true }) {
                            Text(text = stringResource(id = R.string.TXT_SELECT))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.TXT_D_DAY_BACKGROUND_TYPE),
                        fontSize = 12.sp,
                        color = gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(selected = dDayBackgroundType == DDayBackgroundTypeModel.COLOR,
                            onClick = {
                                dDayBackgroundType = DDayBackgroundTypeModel.COLOR
                            },
                            label = { Text(text = stringResource(id = R.string.TXT_COLOR)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                            })

                        Spacer(modifier = Modifier.width(10.dp))

                        FilterChip(selected = dDayBackgroundType == DDayBackgroundTypeModel.IMAGE,
                            onClick = {
                                dDayBackgroundType = DDayBackgroundTypeModel.IMAGE
                            },
                            label = { Text(text = stringResource(id = R.string.TXT_IMAGE)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Rounded.Check, contentDescription = null)
                            })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AnimatedVisibility(visible = dDayBackgroundType == DDayBackgroundTypeModel.IMAGE) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(id = R.string.TXT_SELECT_IMAGE),
                                fontSize = 12.sp,
                                color = gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }) {
                                Text(text = stringResource(id = R.string.TXT_SELECT))
                            }
                        }
                    }

                    AnimatedVisibility(visible = dDayBackgroundType == DDayBackgroundTypeModel.COLOR) {
                        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                            Text(
                                text = stringResource(id = R.string.TXT_SELECT_COLOR),
                                fontSize = 12.sp,
                                color = gray,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (i in 0 until 5) {
                                    Button(
                                        onClick = { selectedColor = i },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors[i]
                                        ),
                                        modifier = Modifier.size(50.dp),
                                        contentPadding = PaddingValues(1.dp)
                                    ) {
                                        AnimatedVisibility(visible = selectedColor == i){
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = white
                                            )
                                        }
                                    }

                                    if (i != 4) {
                                        Spacer(modifier = Modifier.weight(0.25f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.TXT_SELECT_TEXT_COLOR),
                        fontSize = 12.sp,
                        color = gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { selectedTextColor = "#FFFFFF" },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier
                                .size(50.dp)
                                .border(1.dp, Color.Black, CircleShape),
                            contentPadding = PaddingValues(1.dp)
                        ) {
                            AnimatedVisibility(visible = selectedTextColor == "#FFFFFF"){
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(50.dp))

                        Button(
                            onClick = { selectedTextColor = "#000000" },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            modifier = Modifier.size(50.dp),
                            contentPadding = PaddingValues(1.dp)
                        ) {
                            AnimatedVisibility(visible = selectedTextColor == "#000000"){
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                RoundedCornerShape(15.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Text(text = stringResource(id = R.string.TXT_CALCULATE_FIRST_DAY_TO_ONE))

                        Spacer(modifier = Modifier.weight(1f))

                        Switch(checked = calculateFirstDayAsOne,
                            onCheckedChange = { calculateFirstDayAsOne = it },
                            thumbContent = {
                                Icon(
                                    imageVector = if (calculateFirstDayAsOne) Icons.Rounded.Check else Icons.Rounded.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(id = R.string.TXT_PREVIEW),
                        fontSize = 12.sp,
                        color = gray,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(10.dp)
                            .border(1.dp, gray, RoundedCornerShape(15.dp))
                            .background(
                                if (dDayBackgroundType == DDayBackgroundTypeModel.COLOR) colors[selectedColor] else Color.Transparent,
                                RoundedCornerShape(15.dp)
                            )
                    ) {
                        if (selectedImage != null && dDayBackgroundType == DDayBackgroundTypeModel.IMAGE) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImage).build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .clip(RoundedCornerShape(15.dp)),
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
                            Text(text = title, color = if(selectedTextColor == "#FFFFFF") Color.White else Color.Black)

                            AnimatedVisibility(visible = date != ""){
                                val diff = calculateForPreview(date, calculateFirstDayAsOne)
                                Text(
                                    text = if (diff >= 0) "D+${diff}" else "D${diff}",
                                    color = if(selectedTextColor == "#FFFFFF") Color.White else Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExtendedFloatingActionButton(onClick = {
                            if (!showProgress) {
                                if (title == "" || date == "" || (dDayBackgroundType == DDayBackgroundTypeModel.IMAGE && selectedImage == null)) {
                                    alertType = AddDDayAlertModel.EMPTY_FIELD
                                    showAlert = true
                                } else {
                                    showConfirmDialog = true
                                }
                            }
                        }) {
                            if (!showProgress) {
                                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                                Text(text = stringResource(id = R.string.TXT_ADD_D_DAY))
                            } else {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = stringResource(id = R.string.TXT_ADDING_D_DAY))
                            }
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text(stringResource(id = R.string.TXT_OK))
                                }
                            }) {
                            val datePickerState = rememberDatePickerState()

                            if (datePickerState.selectedDateMillis != null) {
                                date = convertMillisToDate(datePickerState.selectedDateMillis!!)
                            }

                            DatePicker(state = datePickerState)
                        }
                    }

                    if (showAlert) {
                        AlertDialog(onDismissRequest = { showAlert = false }, confirmButton = {
                            TextButton(onClick = {
                                showAlert = false
                            }) {
                                Text(text = stringResource(id = R.string.TXT_OK))
                            }
                        }, title = {
                            Text(text = getAddDDayAlertContents(alertType!!, context, 0))
                        }, text = {
                            Text(text = getAddDDayAlertContents(alertType!!, context, 1))
                        }, icon = {
                            Icon(
                                imageVector = if (alertType == AddDDayAlertModel.SUCCESS) Icons.Rounded.CheckCircle else Icons.Rounded.Warning,
                                contentDescription = null
                            )
                        })
                    }

                    if (showConfirmDialog) {
                        AlertDialog(onDismissRequest = { showConfirmDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    showProgress = true
                                    showConfirmDialog = false
                                    
                                    helper.addDDay(title, date, if(dDayBackgroundType == DDayBackgroundTypeModel.COLOR) 0 else 1, if(dDayBackgroundType == DDayBackgroundTypeModel.IMAGE) null else selectedColor, if(selectedTextColor == "#FFFFFF") 0 else 1, if(dDayBackgroundType == DDayBackgroundTypeModel.COLOR) null else selectedImage, calculateFirstDayAsOne){ uploadResult ->
                                        showProgress = false

                                        alertType = if(uploadResult) AddDDayAlertModel.SUCCESS else AddDDayAlertModel.FAIL
                                        showAlert = true
                                    }
                                }) {
                                    Text(text = stringResource(id = R.string.TXT_OK))
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmDialog = false }) {
                                    Text(text = stringResource(id = R.string.TXT_CANCEL))
                                }
                            },
                            title = {
                                Text(text = stringResource(id = R.string.TXT_ADD_D_DAY))
                            },
                            text = {
                                Text(text = stringResource(id = R.string.TXT_CONFIRM_UPLOAD_D_DAY))
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = null
                                )
                            })
                    }
                }
            }
        }
    }
}

@Preview(device = "spec:width=1080px,height=2340px,dpi=440")
@Composable
fun AddDDayViewPreview() {
    AddDDayView(rememberNavController())
}