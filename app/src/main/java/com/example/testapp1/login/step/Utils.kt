package com.example.testapp1.login.step

import com.example.testapp1.ServerResponse
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun getResponse(url: URL): ServerResponse {
    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
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