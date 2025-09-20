package com.tuapp.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CarItem(
    val brand: String,
    val model: String,
    val year: Int,
    val engineCode: String,
    val imageDrawable: String? = null // nombre del drawable sin extensión
)

object CarCatalog {
    private var cache: List<CarItem>? = null

    private fun load(context: Context): List<CarItem> {
        cache?.let { return it }
        val jsonStr = context.assets.open("cars_es.json").bufferedReader().use { it.readText() }
        val list = Json { ignoreUnknownKeys = true }.decodeFromString<List<CarItem>>(jsonStr)
        cache = list
        return list
    }

    fun brands(context: Context): List<String> =
        load(context).map { it.brand }.distinct().sorted()

    fun modelsForBrand(context: Context, brand: String): List<String> =
        load(context).filter { it.brand == brand }.map { it.model }.distinct().sorted()

    fun yearsFor(context: Context, brand: String, model: String): List<Int> =
        load(context).filter { it.brand == brand && it.model == model }
            .map { it.year }.distinct().sorted()

    fun engineCodeFor(context: Context, brand: String, model: String, year: Int): String? =
        load(context).firstOrNull { it.brand == brand && it.model == model && it.year == year }?.engineCode

    fun imageDrawableFor(context: Context, brand: String, model: String, year: Int): String? =
        load(context).firstOrNull { it.brand == brand && it.model == model && it.year == year }?.imageDrawable
}
