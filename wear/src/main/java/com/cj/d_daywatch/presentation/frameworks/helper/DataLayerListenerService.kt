package com.cj.d_daywatch.presentation.frameworks.helper

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val TAG = "DataLayerSample"

class DataLayerListenerService : WearableListenerService(), DataClient.OnDataChangedListener {
    val client = Wearable.getMessageClient(this)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onDataChanged(p0: DataEventBuffer) {
        super.onDataChanged(p0)

        p0.forEach{
            Log.d("DataLayerListenerService", "Received: ${it.dataItem.uri}")
        }
    }
}