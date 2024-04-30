package com.cj.d_daywatch.d_day.models

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.cj.d_daywatch.R
import com.cj.d_daywatch.ui.theme.gray
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@Composable
fun DDayAnniversaryListModel(title: String, date: String, eventName: String){
    val diff = calculateDate(date, false)
    val context = LocalContext.current
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()
    val snackBarTxt = stringResource(id = R.string.TXT_CALENDAR_ADDED)
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
        
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top, modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(text = date, color = gray, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (diff >= 0) "D+${diff}" else "D${diff}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                FilledTonalButton(onClick = {
                    when(PackageManager.PERMISSION_GRANTED){
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_CALENDAR
                        ) -> {

                        }

                        else -> {
                            launcher.launch(Manifest.permission.WRITE_CALENDAR)
                        }
                    }

                    try {
                        val dateFormatter = SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA)
                        val startDate = dateFormatter.parse(date)
                        val timeInMillis = startDate?.time

                        val endDate = dateFormatter.parse(date)
                        val endTimeInMillis = endDate?.time

                        val values = ContentValues().apply{
                            put(CalendarContract.Events.DTSTART, timeInMillis)

                            if (endTimeInMillis != null) {
                                put(CalendarContract.Events.DTEND, endTimeInMillis + 8.64e+7)
                            }

                            put(CalendarContract.Events.TITLE, "${eventName}: $title")
                            put(CalendarContract.Events.CALENDAR_ID, 3)
                            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
                        }

                        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

                        Toast.makeText(context, snackBarTxt, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                }) {
                    Icon(imageVector = Icons.Rounded.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = stringResource(id = R.string.TXT_ADD_TO_CALENDAR), fontSize = 12.sp)
                }
            }

        }

    }
}

@Preview
@Composable
fun DDayAnniversaryListModelPreview(){
    DDayAnniversaryListModel(title = "D+100", date = "2024. 01. 01.", eventName = "Event")
}