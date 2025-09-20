package com.tuapp.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CarItem(val brand: String, val model: String, val year: Int, val engineCode: String)

object CarCatalog {
    private var cache: List<CarItem>? = null

    private fun ensure(context: Context): List<CarItem> {
        cache?.let { return it }
        val jsonStr = context.assets.open("cars_es.json").bufferedReader().use { it.readText() }
        val list = Json { ignoreUnknownKeys = true }.decodeFromString<List<CarItem>>(jsonStr)
        cache = list
        return list
    }

    fun brands(context: Context): List<String> =
        ensure(context).map { it.brand }.distinct().sorted()

    fun modelsFor(context: Context, brand: String): List<String> =
        ensure(context).asSequence()
            .filter { it.brand.equals(brand, ignoreCase = true) }
            .map { it.model }
            .distinct()
            .sorted()
            .toList()

    fun yearsFor(context: Context, brand: String, model: String): List<Int> =
        ensure(context).asSequence()
            .filter { it.brand.equals(brand, ignoreCase = true) && it.model.equals(model, ignoreCase = true) }
            .map { it.year }
            .distinct()
            .sorted()
            .toList()

    fun engineCodeFor(context: Context, brand: String, model: String, year: Int): String? =
        ensure(context).firstOrNull {
            it.brand.equals(brand, ignoreCase = true) &&
            it.model.equals(model, ignoreCase = true) &&
            it.year == year
        }?.engineCode
}
