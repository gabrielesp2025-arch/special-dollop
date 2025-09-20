package com.tuapp.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import com.tuapp.data.Order
import java.io.File

fun generateOrderPdf(context: Context, order: Order): File {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 ~ 72dpi
    val page = doc.startPage(pageInfo)
    val c = page.canvas

    val paint = Paint().apply { color = Color.BLACK; textSize = 14f; isAntiAlias = true }
    var y = 40

    fun line(text: String, big: Boolean = false) {
        paint.textSize = if (big) 18f else 14f
        c.drawText(text, 40f, y.toFloat(), paint); y += if (big) 28 else 20
    }

    line("Taller – Presupuesto / OT", true)
    line("OT #${order.id}")
    line("Cliente: ${order.customer.name}")
    line("Vehículo: ${order.vehicle.brand} ${order.vehicle.model} ${order.vehicle.year ?: ""}  ${order.vehicle.plate}")
    order.vehicle.engineCode?.let { line("Motor: $it") }
    y += 10
    line("Servicios:")

    order.services.forEach {
        line("• ${it.description}  — ${"%.2f".format(it.hours)}h × ${"%.2f".format(it.hourlyRate)}€")
    }
    line("Subtotal mano de obra: ${"%.2f".format(order.subtotalServices)}€")
    y += 6
    line("Piezas:")
    order.parts.forEach {
        line("• ${it.code} ${it.description}  — ${it.qty} × ${"%.2f".format(it.unitPrice)}€")
    }
    line("Subtotal piezas: ${"%.2f".format(order.subtotalParts)}€")
    y += 10
    line("TOTAL sin IVA: ${"%.2f".format(order.totalBase)}€", true)
    line("IVA ${"%.1f".format(order.vatPct)}%: ${"%.2f".format(order.totalBase * order.vatPct / 100)}€")
    line("TOTAL con IVA: ${"%.2f".format(order.totalWithVat)}€", true)

    // Firma si existe
    order.customerSignaturePath?.let { path ->
        val f = File(path)
        if (f.exists()) {
            val bm = BitmapFactory.decodeFile(f.absolutePath)
            val sig = Bitmap.createScaledBitmap(bm, 200, 80, true)
            y += 20
            line("Firma del cliente:")
            c.drawBitmap(sig, 40f, y.toFloat(), null)
            y += 100
        }
    }

    // Muestra hasta 3 fotos (una de cada etapa si hay)
    val pics = order.photos.take(3)
    if (pics.isNotEmpty()) {
        line("Fotos:", true); y += 10
        var x = 40
        pics.forEach { p ->
            val f = File(p.path)
            if (f.exists()) {
                val bm = BitmapFactory.decodeFile(f.absolutePath)
                val thumb = Bitmap.createScaledBitmap(bm, 150, 100, true)
                c.drawBitmap(thumb, x.toFloat(), y.toFloat(), null)
                x += 170
            }
        }
        y += 120
    }

    doc.finishPage(page)

    val out = File(context.filesDir, "OT_${order.id}.pdf")
    out.outputStream().use { doc.writeTo(it) }
    doc.close()
    return out
}
