package com.tuapp.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CarItem(val brand: String, val model: String, val year: Int, val engineCode: String)

object CarCatalog {
    private var cache: List<CarItem>? = null

    fun load(context: Context): List<CarItem> {
        cache?.let { return it }
        val jsonStr = context.assets.open("cars_es.json").bufferedReader().use { it.readText() }
        val list = Json { ignoreUnknownKeys = true }.decodeFromString<List<CarItem>>(jsonStr)
        cache = list
        return list
    }
}
