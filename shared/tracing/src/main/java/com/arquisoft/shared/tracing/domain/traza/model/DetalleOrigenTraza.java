package com.arquisoft.shared.tracing.domain.traza.model;

public sealed interface DetalleOrigenTraza
        permits DetalleOrigenTraza.DetalleHttpTraza,
                DetalleOrigenTraza.DetalleEventoTraza,
                DetalleOrigenTraza.DetalleProgramadoTraza {

    record DetalleHttpTraza(String clienteIp, String metodoHttp, String rutaUri) implements DetalleOrigenTraza {}

    record DetalleEventoTraza(String colaEvento) implements DetalleOrigenTraza {}

    record DetalleProgramadoTraza() implements DetalleOrigenTraza {}
}
