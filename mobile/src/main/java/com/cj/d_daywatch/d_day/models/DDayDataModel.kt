package com.cj.d_daywatch.d_day.models

import android.net.Uri

data class DDayDataModel(
    val id: String,
    val title: String,
    val date: String,
    val setBaseDayToOneDay: Boolean,
    val backgroundType: Int,
    val image: Uri?,
    val backgroundColor: Int?,
    val textColor: Int
)
