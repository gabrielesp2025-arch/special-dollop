package com.tuapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val email: String? = null
)

@Serializable
data class Vehicle(
    val id: Long = 0,
    val customerId: Long,
    val brand: String,
    val model: String,
    val plate: String,
    val year: Int? = null,
    val engineCode: String? = null,
    val vin: String? = null,
    val imagePath: String? = null // ← para guardar ruta local de imagen (opcional)
)

@Serializable
data class ServiceItem(
    val id: Long = 0,
    val description: String,
    val hours: Double,
    val hourlyRate: Double
)

@Serializable
data class PartItem(
    val id: Long = 0,
    val code: String,
    val description: String,
    val qty: Int,
    val unitPrice: Double,
    val url: String? = null // ← renombrado para alinear con FileRepo.addPart
)

@Serializable
enum class PhotoStage { BEFORE, DURING, AFTER }

@Serializable
data class PhotoRef(
    val id: Long = 0,
    val stage: PhotoStage,
    val path: String // ruta interna (filesDir)
)

@Serializable
data class Order(
    val id: Long = 0,
    val customer: Customer,
    val vehicle: Vehicle,
    val vatPct: Double = 21.0,
    val baseHourlyRate: Double = 35.0,
    val services: List<ServiceItem> = emptyList(),
    val parts: List<PartItem> = emptyList(),
    val photos: List<PhotoRef> = emptyList(),
    val customerSignaturePath: String? = null
) {
    val subtotalServices: Double get() = services.sumOf { it.hours * it.hourlyRate }
    val subtotalParts: Double get() = parts.sumOf { it.qty * it.unitPrice }
    val totalBase: Double get() = subtotalServices + subtotalParts
    val totalWithVat: Double get() = totalBase * (1 + vatPct / 100.0)
}
