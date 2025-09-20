/**
     * Crea una Orden a partir de líneas del tarifario.
     * Cada línea se convierte en un Servicio con 1h y precio = (precioMedio * cantidad).
     * @param lines lista de Triple(descripcion, cantidad, precioMedioEUR)
     * @param vatPct IVA a aplicar en la orden (por ejemplo 21.0)
     * @return id de la nueva orden
     */
    @Synchronized
    fun newOrderFromLines(lines: List<Triple<String, Int, Double>>, vatPct: Double): Long {
        val id = seq.incrementAndGet()
        val cust = Customer(id = seq.incrementAndGet(), name = "Cliente Presupuesto")
        val veh = Vehicle(id = seq.incrementAndGet(), customerId = cust.id, brand = "—", model = "—", plate = "—")

        val services = lines.map { (desc, qty, midPrice) ->
            ServiceItem(
                id = seq.incrementAndGet(),
                description = desc,
                hours = 1.0,
                hourlyRate = (midPrice * qty).coerceAtLeast(0.0)
            )
        }

        val o = Order(
            id = id,
            customer = cust,
            vehicle = veh,
            services = services,
            parts = emptyList(),
            vatPct = vatPct,      // 👈 guardamos el IVA elegido en el Tarifario
            baseHourlyRate = 35.0
        )
        state = state.copy(orders = state.orders + o)
        save()
        return id
    }
