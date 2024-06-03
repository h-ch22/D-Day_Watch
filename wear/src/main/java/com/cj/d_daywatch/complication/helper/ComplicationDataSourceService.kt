package com.cj.d_daywatch.complication.helper

import android.content.ComponentName
import android.graphics.drawable.Icon
import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.cj.d_daywatch.R
import com.cj.d_daywatch.complication.models.COMPLICATION_D_DAY_KEY
import com.cj.d_daywatch.complication.models.COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY
import com.cj.d_daywatch.complication.models.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ComplicationDataSourceService : SuspendingComplicationDataSourceService() {
    private val TAG = "ComplicationDataSourceService"

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {
        super.onComplicationActivated(complicationInstanceId, type)
        Log.d(TAG, "Complication Activated: $complicationInstanceId")
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        val dDayText = calculateDate("2021. 12. 24.", true)

        return when(type){
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = dDayText.toString()).build(),
                    contentDescription = PlainComplicationText.Builder(text = "Short D-Day Counter.")
                        .build()
                ).setTapAction(null).build()
            }

            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = 0f,
                    min = 0f,
                    max= 0f,
                    contentDescription = PlainComplicationText.Builder(text = "D-Day Watch").build()
                ).setText(PlainComplicationText.Builder(dDayText.toString()).build()).setTapAction(null).build()
            }

            ComplicationType.LONG_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = dDayText.toString()).build(),
                    contentDescription = PlainComplicationText.Builder(text = "Long D-Day Counter.")
                        .build()
                ).setTapAction(null).build()
            }

            else -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = dDayText.toString()).build(),
                    contentDescription = PlainComplicationText.Builder(text = "Short D-Day Counter.")
                        .build()
                ).setTapAction(null).build()
            }
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        val date = applicationContext.dataStore.data
            .map {
                it[COMPLICATION_D_DAY_KEY] ?: ""
            }
            .first()

        val setFirstDayAsOneDay = applicationContext.dataStore.data
            .map {
                it[COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY] ?: false
            }
            .first()

        val icon = Icon.createWithResource(this, R.drawable.ic_complication)

        val dDayText = calculateDate(date, setFirstDayAsOneDay)

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = dDayText.toString()).build(),
                contentDescription = PlainComplicationText.Builder(text = "Short D-Day Counter.")
                    .build()
            ).setMonochromaticImage(
                MonochromaticImage.Builder(
                    icon
                ).build()
            ).build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = dDayText.toString()).build(),
                contentDescription = PlainComplicationText.Builder(text = "Long D-Day Counter.")
                    .build()
            )
                .setMonochromaticImage(
                    MonochromaticImage.Builder(
                        icon
                    ).build()
                )
                .build()

            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = dDayText.toFloat(),
                    min = 0f,
                    max= dDayText.toFloat() * 2,
                    contentDescription = PlainComplicationText.Builder(text = "D-Day Watch").build()
                )
                    .setMonochromaticImage(
                        MonochromaticImage.Builder(
                            icon
                        ).build()
                    )
                    .setText(PlainComplicationText.Builder(dDayText.toString()).build()).setTapAction(null).build()
            }

            else -> {
                Log.d(TAG, "Unknown type of complication: ${request.complicationType}")
                null
            }
        }
    }

    private fun calculateDate(selectedDate: String, setOneDay: Boolean): Long {
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

}