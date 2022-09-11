package com.example.testapp1.login.step

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.example.testapp1.BuildConfig
import com.example.testapp1.R
import com.example.testapp1.ServerResponse
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getResponse(url: URL, header: Pair<String, String>? = null): ServerResponse {
    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    if (header != null) {
        urlConnection.addRequestProperty(header.first, header.second)
    }
    val response = try {
        val code = urlConnection.responseCode
        val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
        val body = readStream(inputStream)
        ServerResponse(code, body)
    } catch (e: IOException) {
        ServerResponse(0, e.stackTraceToString())
    } finally {
        urlConnection.disconnect()
    }
    return response
}

fun readStream(inputStream: InputStream): String {
    val baos = ByteArrayOutputStream()
    var i: Int = inputStream.read()
    while (i != -1) {
        baos.write(i)
        i = inputStream.read()
    }
    return baos.toString()
}

fun JSONArray.toJSONObjectList(): List<JSONObject> {
    val list = mutableListOf<JSONObject>()
    for (i in 0 until this.length()) {
        list.add(this.getJSONObject(i))
    }
    return list
}

fun JSONArray.toStringList(): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until this.length()) {
        list.add(this.getString(i))
    }
    return list
}

fun ImageView.setImageFromUrl(imageURL: String): Drawable? {
    val progressBar = AppCompatResources.getDrawable(context, R.drawable.progress_bar)
    val handler = Handler(Looper.getMainLooper())
    handler.post {
        setImageDrawable(progressBar)
    }
    var bitmap: Bitmap? = null
    try {
        val stream = URL(imageURL).openStream()
        bitmap = BitmapFactory.decodeStream(stream)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return if (bitmap == null) {
        val stub = AppCompatResources.getDrawable(context, R.drawable.ic_no_image_48)
        stub!!.setTint(com.google.android.material.R.attr.colorSecondary)
        handler.post {
            setImageDrawable(stub)
        }
        null
    } else {
        val newDrawable = BitmapDrawable(context.resources, bitmap)
        handler.post {
            setImageDrawable(newDrawable)
        }
        newDrawable
    }
}