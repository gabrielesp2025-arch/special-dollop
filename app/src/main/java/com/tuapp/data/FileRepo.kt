/**
     * Crea una Orden a partir de líneas del tarifario.
     * Cada línea se convierte en un Servicio con 1h y precio = (precioMedio * cantidad).
     * Así el total coincide con el estimado.
     *
     * @param lines lista de Triple(descripcion, cantidad, precioMedioEUR)
     * @return id de la nueva orden
     */
    @Synchronized
    fun newOrderFromLines(lines: List<Triple<String, Int, Double>>): Long {
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
            vatPct = 21.0,
            baseHourlyRate = 35.0
        )
        state = state.copy(orders = state.orders + o)
        save()
        return id
    }
