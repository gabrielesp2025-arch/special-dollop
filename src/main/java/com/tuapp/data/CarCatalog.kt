package com.tuapp.data

import android.content.Context
import java.text.Normalizer
import java.util.Locale

// Construye el nombre del recurso drawable a partir de marca/modelo/año
fun carImageResName(brand: String?, model: String?, year: Int?): String? {
    if (brand.isNullOrBlank() || model.isNullOrBlank() || year == null) return null
    fun norm(s: String) = Normalizer.normalize(s.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "") // quita tildes
        .replace("[^a-z0-9]+".toRegex(), "_") // no letras -> _
        .trim('_')
    val b = norm(brand)
    val m = norm(model)
    return "${b}_${m}_${year}"
}

// Devuelve el id del drawable; 0 si no existe
fun carImageResId(context: Context, brand: String?, model: String?, year: Int?): Int {
    val name = carImageResName(brand, model, year) ?: return 0
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}
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

    fun brands(context: Context): List<String> =
        load(context).map { it.brand }.distinct().sorted()

    fun modelsForBrand(context: Context, brand: String): List<String> =
        load(context).filter { it.brand == brand }.map { it.model }.distinct().sorted()

    fun yearsFor(context: Context, brand: String, model: String): List<Int> =
        load(context).filter { it.brand == brand && it.model == model }
            .map { it.year }.distinct().sorted()

    fun engineCodeFor(context: Context, brand: String, model: String, year: Int): String? =
        load(context).firstOrNull { it.brand == brand && it.model == model && it.year == year }?.engineCode
}
