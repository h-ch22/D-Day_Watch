package com.cj.d_daywatch.widget.ui

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent

object D_DayWatch_Widget: GlanceAppWidget(){
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            
        }
    }

}