package com.emi.wac.data.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import android.content.Context
import android.util.Log
import java.io.IOException

/**
 * Class for parsing JSON files into Kotlin objects.
 */
class JsonParser(private val context: Context) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun <T> parseJson(fileName: String, adapterClass: Class<T>): T? {
        return try {
            val jsonString = context.assets
                .open(fileName)
                .bufferedReader()
                .use { it.readText() }

            moshi.adapter(adapterClass).fromJson(jsonString)
        } catch (e: IOException) {
            Log.e("JsonParser", "Error loading JSON from $fileName", e)
            null
        }
    }
}