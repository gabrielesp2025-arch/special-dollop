package com.tuapp.data

import android.content.Context
import java.util.Locale

/**
 * Devuelve el id del drawable según marca+modelo(+año).
 * Busca nombres tipo:
 *   car_seat_ibiza_2018, car_seat_ibiza, car_seat
 */
fun carImageResId(ctx: Context, brand: String?, model: String?, year: Int?): Int {
    fun norm(s: String?) = s?.lowercase(Locale.ROOT)
        ?.replace("á","a")?.replace("é","e")?.replace("í","i")
        ?.replace("ó","o")?.replace("ú","u")?.replace("ü","u")
        ?.replace("ñ","n")?.replace("[^a-z0-9]".toRegex(), "") ?: ""

    val b = norm(brand)
    val m = norm(model)
    val y = year?.toString() ?: ""

    val names = listOf(
        "car_${b}_${m}_${y}",
        "car_${b}_${m}",
        "car_${b}"
    ).filter { it.endsWith("_") .not() && it != "car_" }

    for (n in names) {
        val id = ctx.resources.getIdentifier(n, "drawable", ctx.packageName)
        if (id != 0) return id
    }
    return 0
}
