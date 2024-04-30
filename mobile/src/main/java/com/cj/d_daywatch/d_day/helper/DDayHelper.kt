package com.cj.d_daywatch.d_day.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.cj.d_daywatch.d_day.models.DDayDataModel
import com.cj.d_daywatch.frameworks.helper.AES256Util
import com.cj.d_daywatch.frameworks.helper.UriJsonAdapter
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI
import java.nio.channels.FileChannel
import java.time.Instant

class DDayHelper {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        var dDayList = mutableStateListOf<DDayDataModel>()
    }

    private fun addDDay(data: DDayDataModel) {
        dDayList.add(data)
    }

    private fun addPrefs(data: DDayDataModel, context: Context, id: String) {
        val gson = GsonBuilder().registerTypeAdapter(Uri::class.java, UriJsonAdapter()).create()

        val json = gson.toJson(data)
        val preferences = context.getSharedPreferences("D_DayList", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(id, json)
        editor.apply()
    }

    fun addDDay(
        title: String,
        date: String,
        backgroundType: Int,
        backgroundColor: Int?,
        textColor: Int,
        image: Uri?,
        setBaseDateToOneDay: Boolean,
        completion: (Boolean) -> Unit
    ) {
        db.collection("D_Day").document(auth.currentUser?.uid ?: "").set(hashMapOf("null" to null))
            .addOnCompleteListener {
                db.collection("D_Day").document(auth.currentUser?.uid ?: "").collection("List").add(
                    hashMapOf(
                        "title" to AES256Util.encrypt(title),
                        "date" to AES256Util.encrypt(date),
                        "setBaseDateToOneDay" to setBaseDateToOneDay,
                        "backgroundType" to backgroundType,
                        "backgroundColor" to backgroundColor,
                        "textColor" to textColor
                    )
                ).addOnCompleteListener { dbResult ->
                    if (dbResult.isSuccessful) {
                        if (backgroundType == 1 && image != null) {
                            storage.reference.child("d_day/images/${auth.currentUser?.uid ?: ""}/${dbResult.result.id}.png")
                                .putFile(image).addOnSuccessListener {
                                    completion(true)
                                }.addOnFailureListener { storageException ->
                                    storageException.printStackTrace()
                                    completion(false)
                                }
                        } else {
                            completion(true)
                        }

                    } else {
                        completion(false)
                    }
                }.addOnFailureListener { dbException ->
                    dbException.printStackTrace()
                    completion(false)
                }
            }
    }

    private fun sendImageToWear(img: File, context: Context, id: String){
        val asset = BitmapFactory.decodeFile(img.absolutePath).toAsset()
        val client = Wearable.getDataClient(context)

        try{
            val request = PutDataMapRequest.create("/image/$id").apply{
                dataMap.putAsset("IMG_$id", asset)
                dataMap.putLong("time", Instant.now().epochSecond)
            }.asPutDataRequest()
                .setUrgent()

            val result = client.putDataItem(request)
            Log.d("DDayHelper", "Image saved: $result")
        } catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun readXmlFromFile(context: Context): String?{
        val filePath = "/data/data/com.cj.d_daywatch/shared_prefs/D_DayList.xml"
        return try {
            File(filePath).readText()
        } catch (e: Exception) {
            Log.e("FileReadError", "Failed to read file", e)
            null
        }
    }

    private fun sendDataToWear(context: Context){
        val xmlString = readXmlFromFile(context)
        xmlString?.let {
            val request = PutDataMapRequest.create("/data/shared_prefs").apply {
                dataMap.putString("sharedPrefsContent", xmlString)
                dataMap.putLong("timestamp", System.currentTimeMillis())
            }.asPutDataRequest().setUrgent()

            val dataClient: DataClient = Wearable.getDataClient(context)
            dataClient.putDataItem(request).addOnSuccessListener {
                Log.d("DataTransfer", "Successfully sent XML to wearable")
            }.addOnFailureListener {
                Log.e("DataTransfer", "Failed to send XML", it)
            }
        } ?: Log.e("DataTransfer", "XML string is null, not sending")
    }

    private fun Bitmap.toAsset(): Asset =
        ByteArrayOutputStream().use { byteStream ->
            compress(Bitmap.CompressFormat.PNG, 100, byteStream)
            Asset.createFromBytes(byteStream.toByteArray())
        }

    fun getDDayList(context: Context, completion: (Boolean) -> Unit) {
        dDayList.clear()
        val gson = GsonBuilder().registerTypeAdapter(Uri::class.java, UriJsonAdapter()).create()
        val preferences = context.getSharedPreferences("D_DayList", Context.MODE_PRIVATE)

        db.collection("D_Day").document(auth.currentUser?.uid ?: "").collection("List").get()
            .addOnSuccessListener { queryResult ->
                if (!queryResult.isEmpty) {
                    for (document in queryResult.documents) {
                        val docId = document.id
                        val value = preferences.getString(docId, null)

                        if (value == null) {
                            val title = AES256Util.decrypt(document.get("title") as? String ?: "")
                            val date = AES256Util.decrypt(document.get("date") as? String ?: "")
                            val setBaseDateToOneDay =
                                document.get("setBaseDateToOneDay") as? Boolean ?: false
                            val backgroundType = document.get("backgroundType") as? Long ?: 0L
                            val backgroundColor = document.get("backgroundColor") as? Long
                            val textColor = document.get("textColor") as? Long ?: 0L

                            val destPath = "/data/data/com.cj.d_daywatch/${docId}.png"
                            val destFile = File(destPath)

                            if (backgroundType == 1L) {
                                storage.reference.child("d_day/images/${auth.currentUser?.uid ?: ""}/${docId}.png").getFile(destFile).addOnSuccessListener { _ ->
                                    val data = DDayDataModel(
                                        docId,
                                        title,
                                        date,
                                        setBaseDateToOneDay,
                                        backgroundType.toInt(),
                                        Uri.fromFile(destFile),
                                        backgroundColor?.toInt(),
                                        textColor.toInt()
                                    )

                                    addPrefs(data, context, docId)

                                    sendImageToWear(destFile, context, docId)

                                    addDDay(
                                        data
                                    )
                                }.addOnFailureListener { storageException ->
                                    storageException.printStackTrace()
                                }
                            } else {
                                val data = DDayDataModel(
                                    docId,
                                    title,
                                    date,
                                    setBaseDateToOneDay,
                                    backgroundType.toInt(),
                                    null,
                                    backgroundColor?.toInt(),
                                    textColor.toInt()
                                )

                                addPrefs(data, context, docId)

                                addDDay(
                                    data
                                )
                            }
                        } else {
                            val data = gson.fromJson(value, DDayDataModel::class.java)
                            addDDay(
                                data
                            )

                            if(data.image != null){
                                data.image.path?.let { File(it) }
                                    ?.let { sendImageToWear(it, context, docId) }
                            }
                        }
                    }

                    sendDataToWear(context)
                } else {
                    sendDataToWear(context)
                    completion(true)
                    return@addOnSuccessListener
                }

                completion(true)
                return@addOnSuccessListener
            }.addOnFailureListener { queryException ->
                queryException.printStackTrace()
                completion(false)
                return@addOnFailureListener
            }
    }

    fun delete(id: String, context: Context, completion: (Boolean) -> Unit){
        db.collection("D_Day").document(auth.currentUser?.uid ?: "").collection("List").document(id).delete().addOnCompleteListener {
            if(it.isSuccessful){
                val preferences = context.getSharedPreferences("D_DayList", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.remove(id)
                editor.apply()

                val file = File("/data/data/com.cj.d_daywatch/${id}.png")

                if(file.exists()){
                    file.delete()
                }

                completion(true)
                return@addOnCompleteListener
            } else{
                completion(false)
                return@addOnCompleteListener
            }
        }.addOnFailureListener {
            it.printStackTrace()
            completion(false)
            return@addOnFailureListener
        }
    }
}