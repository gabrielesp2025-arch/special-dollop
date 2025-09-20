package com.tuapp.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CarItem(
    val brand: String,
    val model: String,
    val year: Int,
    val engineCode: String
)

object CarCatalog {
    private var cache: List<CarItem>? = null

    /** Carga la lista de coches desde assets/cars_es.json */
    fun load(context: Context): List<CarItem> {
        cache?.let { return it }
        val jsonStr = context.assets.open("cars_es.json").bufferedReader().use { it.readText() }
        val list = Json { ignoreUnknownKeys = true }
            .decodeFromString<List<CarItem>>(jsonStr)
        cache = list
        return list
    }

    /** Devuelve todas las marcas disponibles */
    fun brands(context: Context): List<String> =
        load(context).map { it.brand }.distinct().sorted()

    /** Devuelve modelos según marca */
    fun models(context: Context, brand: String): List<String> =
        load(context).filter { it.brand == brand }.map { it.model }.distinct().sorted()

    /** Devuelve años según marca y modelo */
    fun years(context: Context, brand: String, model: String): List<Int> =
        load(context).filter { it.brand == brand && it.model == model }
            .map { it.year }
            .distinct()
            .sorted()

    /** Busca el código de motor según marca, modelo y año */
    fun engineCodeFor(context: Context, brand: String, model: String, year: Int): String? =
        load(context).find { it.brand == brand && it.model == model && it.year == year }?.engineCode
}
