package com.cj.d_daywatch.frameworks.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import com.cj.d_daywatch.frameworks.helper.UriJsonAdapter
import com.cj.d_daywatch.ui.theme.dDayBG1
import com.cj.d_daywatch.ui.theme.dDayBG2
import com.cj.d_daywatch.ui.theme.dDayBG3
import com.cj.d_daywatch.ui.theme.dDayBG4
import com.cj.d_daywatch.ui.theme.dDayBG5
import com.google.gson.GsonBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy. MM. dd.", Locale.KOREA)
    return formatter.format(Date(millis))
}

fun convertIntToColor(code: Int): Color {
    return when(code){
        0 -> dDayBG1
        1 -> dDayBG2
        2 -> dDayBG3
        3 -> dDayBG4
        4 -> dDayBG5
        else -> dDayBG1
    }
}