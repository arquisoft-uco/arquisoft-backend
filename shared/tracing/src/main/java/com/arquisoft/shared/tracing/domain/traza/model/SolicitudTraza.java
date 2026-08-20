package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;

public record SolicitudTraza(
        OrigenTraza origen,
        String correlacionEntrante,
        String traceparentEntrante,
        String clienteIp,
        String metodoHttp,
        String rutaUri,
        String colaEvento) {

    public static SolicitudTraza paraHttp(final String correlacionEntrante, final String traceparentEntrante,
                                          final String clienteIp, final String metodoHttp, final String rutaUri) {
        return new SolicitudTraza(OrigenTraza.HTTP, correlacionEntrante, traceparentEntrante,
                clienteIp, metodoHttp, rutaUri, UtilTexto.VACIO);
    }

    public static SolicitudTraza paraEvento(final String correlacionEntrante, final String colaEvento) {
        return new SolicitudTraza(OrigenTraza.EVENTO, correlacionEntrante, UtilTexto.VACIO,
                TrazaValores.DESCONOCIDO, UtilTexto.VACIO, UtilTexto.VACIO, colaEvento);
    }

    public static SolicitudTraza paraProgramado() {
        return new SolicitudTraza(OrigenTraza.PROGRAMADO, UtilTexto.VACIO, UtilTexto.VACIO,
                TrazaValores.DESCONOCIDO, UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);
    }

    public SolicitudTraza {
        origen = UtilObjeto.aplicarPorDefecto(origen, OrigenTraza.PROGRAMADO);
    }
}
