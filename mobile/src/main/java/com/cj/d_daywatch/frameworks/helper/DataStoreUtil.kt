package com.cj.d_daywatch.frameworks.helper

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cj.d_daywatch.d_day.models.DDayDataModel
import com.cj.d_daywatch.userManagement.models.AuthInfoModel
import kotlinx.coroutines.flow.map

const val DATASTORE_NAME = "DDAY_WATCH_AUTH_DATASTORE"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreUtil(private val context : Context) {
    companion object{
        val AUTH_EMAIL = stringPreferencesKey("AUTH_EMAIL")
        val AUTH_PASSWORD = stringPreferencesKey("AUTH_PASSWORD")
        val AUTH_CREDENTIAL = stringPreferencesKey("AUTH_CREDENTIAL")
    }

    suspend fun saveToDataStore(email : String?, password : String?, token: String?){
        context.dataStore.edit{
            it[AUTH_EMAIL] = email ?: ""
            it[AUTH_PASSWORD] = password ?: ""
            it[AUTH_CREDENTIAL] = token ?: ""
        }
    }

    fun getFromDataStore() = context.dataStore.data.map{
        AuthInfoModel(
            email = it[AUTH_EMAIL] ?: "",
            password = it[AUTH_PASSWORD] ?: "",
            credential = it[AUTH_CREDENTIAL] ?: ""
        )
    }

    suspend fun clearDataStore() = context.dataStore.edit{
        it.clear()
    }
}