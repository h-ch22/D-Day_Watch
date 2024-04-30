package com.cj.d_daywatch.presentation.frameworks.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.cj.d_daywatch.presentation.frameworks.helper.DataLayerListenerService
import com.cj.d_daywatch.presentation.frameworks.ui.MainView
import com.cj.d_daywatch.presentation.theme.dDayBG1
import com.cj.d_daywatch.presentation.theme.dDayBG2
import com.cj.d_daywatch.presentation.theme.dDayBG3
import com.cj.d_daywatch.presentation.theme.dDayBG4
import com.cj.d_daywatch.presentation.theme.dDayBG5
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener, CapabilityClient.OnCapabilityChangedListener {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
    private val WEAR_CAPABILITY = "wear"
    private val MOBILE_CAPABILITY = "mobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataClient.addListener(this)
        capabilityClient.addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)

        lifecycleScope.launch {
            try{
                val node = getCapabilitiesForReachableNodes()
                    .filterValues { MOBILE_CAPABILITY in it || WEAR_CAPABILITY in it }.keys

                Log.d("MainActivity", "Connected to $node")
            } catch (exception: Exception) {
                Log.d("MainActivity", "Querying nodes failed: $exception")
            }
        }

        setContent {
            MainView()
        }
    }

    private suspend fun getCapabilitiesForReachableNodes(): Map<Node, Set<String>> =
        capabilityClient.getAllCapabilities(CapabilityClient.FILTER_REACHABLE)
            .await()
            .flatMap { (capability, capabilityInfo) ->
                capabilityInfo.nodes.map { it to capability }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )
            .mapValues { it.value.toSet() }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        val imagesToLoad = mutableListOf<Pair<Uri, Asset>>()

        dataEventBuffer.use { buffer ->
            buffer.forEach { dataEvent ->
                val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap

                Log.d("MainActivity", "Data Received: ${dataEvent.dataItem.uri}")
                val uri = dataEvent.dataItem.uri

                if (uri.toString().contains("image")) {
                    val id = uri.toString().split("image/")[1]

                    val asset = dataMap.getAsset("IMG_$id")
                    Log.d("MainActivity", "Asset for ID $id: ${asset != null}")

                    if (asset != null) {
                        imagesToLoad.add(uri to asset)
                    } else{
                        Log.d("MainActivity", "Asset is null.")
                    }
                } else if(uri.toString().contains("shared_prefs")){
                    DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.apply{
                        val xmlContent = getString("sharedPrefsContent")

                        if(xmlContent != null){
                            processXml(xmlContent)
                        } else{
                            Log.d("MainActivity", "XML content is null.")
                        }
                    }
                }
            }
        }

        if (imagesToLoad.isNotEmpty()) {
            processImages(imagesToLoad)
        }
    }

    private fun processXml(xmlContent: String){
        val dir = File("/data/data/com.cj.d_daywatch/shared_prefs/")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "D_DayList.xml")
        try {
            FileOutputStream(file).use { fos ->
                fos.write(xmlContent.toByteArray())
                Log.d("FileWrite", "Successfully wrote XML to $file")
            }
        } catch (e: Exception) {
            Log.e("FileWriteError", "Failed to write XML file", e)
        }
    }

    private fun processImages(imagesToLoad: List<Pair<Uri, Asset>>) {
        lifecycleScope.launch(Dispatchers.IO) {
            imagesToLoad.forEach { (uri, asset) ->
                val image = loadBitmap(asset)
                if (image != null) {
                    val id = uri.toString().split("image/")[1]
                    Log.d("MainActivity", id)
                    bitmapToFile(image, id)
                }
            }
        }
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {

    }

    private suspend fun loadBitmap(asset: Asset?): Bitmap? {
        if (asset == null) return null
        val response =
            Wearable.getDataClient(this).getFdForAsset(asset).await()
        return response.inputStream.use { inputStream ->
            withContext(Dispatchers.IO) {
                BitmapFactory.decodeStream(inputStream)
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap , saveName: String) {
        val saveDir = "/data/data/com.cj.d_daywatch/"
        val file = File(saveDir)
        if (!file.exists()) file.mkdirs()

        val fileName = "$saveName.png"
        val tempFile = File(saveDir, fileName)

        var out: OutputStream? = null
        try {
            if (tempFile.createNewFile()) {
                out = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

        } finally {
            out?.close()
        }
    }
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