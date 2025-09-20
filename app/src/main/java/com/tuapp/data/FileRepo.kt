package com.tuapp.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class FileRepo(private val ctx: Context) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val file: File get() = File(ctx.filesDir, "taller_data.json")

    private var seq = AtomicLong(System.currentTimeMillis())
    private var state = StorageState()

    init { load() }

    @Synchronized
    fun load() {
        if (file.exists()) {
            runCatching { state = json.decodeFromString(file.readText()) }.onFailure { state = StorageState() }
        } else state = StorageState()
    }

    @Synchronized
    fun save() { file.writeText(json.encodeToString(state)) }

    @Synchronized
    fun listOrders(): List<Order> = state.orders.sortedByDescending { it.id }

    @Synchronized
    fun newOrder(): Long {
        val id = seq.incrementAndGet()
        val cust = Customer(id = seq.incrementAndGet(), name = "Cliente Demo")
        val veh = Vehicle(id = seq.incrementAndGet(), customerId = cust.id, brand = "Genérico", model = "Modelo", plate = "0000-XXX")
        val o = Order(id = id, customer = cust, vehicle = veh)
        state = state.copy(orders = state.orders + o)
        save(); return id
    }

    @Synchronized
    fun getOrder(id: Long): Order? = state.orders.find { it.id == id }

    @Synchronized
    fun addService(orderId: Long, description: String, hours: Double, hourlyRate: Double) {
        val o = getOrder(orderId) ?: return
        val s = o.services + ServiceItem(id = seq.incrementAndGet(), description, hours, hourlyRate)
        update(o.copy(services = s))
    }

    @Synchronized
    fun addPart(orderId: Long, code: String, description: String, qty: Int, unitPrice: Double, url: String?) {
        val o = getOrder(orderId) ?: return
        val p = o.parts + PartItem(id = seq.incrementAndGet(), code, description, qty, unitPrice, url)
        update(o.copy(parts = p))
    }

    @Synchronized
    fun updateRates(orderId: Long, vatPct: Double?, hourlyRate: Double?) {
        val o = getOrder(orderId) ?: return
        update(o.copy(
            vatPct = vatPct ?: o.vatPct,
            baseHourlyRate = hourlyRate ?: o.baseHourlyRate
        ))
    }

    @Synchronized
    fun newOrderFromLines(lines: List<Triple<String, Int, Double>>, vatPct: Double): Long {
        val id = seq.incrementAndGet()
        val cust = Customer(id = seq.incrementAndGet(), name = "Cliente Presupuesto")
        val veh = Vehicle(id = seq.incrementAndGet(), customerId = cust.id, brand = "—", model = "—", plate = "—")
        val services = lines.map { (desc, qty, midPrice) ->
            ServiceItem(id = seq.incrementAndGet(), description = desc, hours = 1.0, hourlyRate = (midPrice * qty).coerceAtLeast(0.0))
        }
        val o = Order(id = id, customer = cust, vehicle = veh, services = services, parts = emptyList(), vatPct = vatPct, baseHourlyRate = 35.0)
        state = state.copy(orders = state.orders + o)
        save(); return id
    }

    private fun update(n: Order) {
        state = state.copy(orders = state.orders.map { if (it.id == n.id) n else it })
        save()
    }
}

@kotlinx.serialization.Serializable
private data class StorageState(val orders: List<Order> = emptyList())
