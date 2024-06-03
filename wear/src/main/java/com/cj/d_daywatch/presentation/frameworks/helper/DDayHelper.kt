package com.cj.d_daywatch.presentation.frameworks.helper

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.cj.d_daywatch.presentation.frameworks.models.DDayDataModel
import com.google.gson.GsonBuilder

class DDayHelper(context: Context) {
    private val gson = GsonBuilder().registerTypeAdapter(Uri::class.java, UriJsonAdapter()).create()
    private val preferences = context.getSharedPreferences("D_DayList", Context.MODE_PRIVATE)

    companion object{
        val dDayList = mutableStateListOf<DDayDataModel>()
    }

    fun getList(){
        dDayList.clear()

        for(key in preferences.all.keys){
            val data = gson.fromJson(preferences.getString(key, null), DDayDataModel::class.java)
            dDayList.add(data)
        }
    }
}