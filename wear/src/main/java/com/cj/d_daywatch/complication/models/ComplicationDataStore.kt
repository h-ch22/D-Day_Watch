package com.cj.d_daywatch.complication.models

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "D_Day_Watch_Complication_Store")
val COMPLICATION_D_DAY_KEY = stringPreferencesKey("COMPLICATION_D_DAY_ID")
val COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY = booleanPreferencesKey("COMPLICATION_D_DAY_SET_FIRST_DAY_AS_ONE_DAY")
