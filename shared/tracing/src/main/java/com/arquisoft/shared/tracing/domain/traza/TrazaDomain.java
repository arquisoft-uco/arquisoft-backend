package com.arquisoft.shared.tracing.domain.traza;

import com.arquisoft.shared.tracing.domain.traza.model.ClienteIp;
import com.arquisoft.shared.tracing.domain.traza.model.CorrelacionEntrante;
import com.arquisoft.shared.tracing.domain.traza.model.DetalleOrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.IdentificadorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.OrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.RutaUri;
import com.arquisoft.shared.tracing.domain.traza.model.SalidaTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.domain.traza.model.Traceparent;
import com.arquisoft.shared.tracing.domain.traza.model.TrazaValores;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilTexto;

import java.time.Instant;
import java.util.Optional;

public final class TrazaDomain {

    private String correlacionId;
    private String transaccionId;
    private String transaccionPadreId;
    private String usuarioId;
    private DetalleOrigenTraza detalle;
    private OrigenTraza origen;
    private Instant tiempoEntrada;
    private Instant tiempoSalida;
    private long duracionMs;
    private int codigoEstado;

    private long inicioNano;

    private TrazaDomain() {}

    public static TrazaDomain crear(final SolicitudTraza solicitud, final boolean anonimizarIp) {
        var traza = new TrazaDomain();
        traza.asignarCorrelacion(solicitud);
        traza.setTransaccionId(IdentificadorTraza.nuevaTransaccion());
        traza.setTransaccionPadreId(padreDe(solicitud));
        traza.setUsuarioId(usuarioSemilla(solicitud.origen()));
        traza.setDetalle(detalleDe(solicitud, anonimizarIp));
        traza.setOrigen(solicitud.origen());
        traza.setTiempoEntrada(UtilFecha.generarInstanteActual());
        traza.setInicioNano(System.nanoTime());
        return traza;
    }

    public SalidaTraza registrarSalida(final int nuevoCodigoEstado) {
        setCodigoEstado(nuevoCodigoEstado);
        setTiempoSalida(UtilFecha.generarInstanteActual());
        setDuracionMs((System.nanoTime() - getInicioNano()) / 1_000_000L);
        return new SalidaTraza(getCodigoEstado(), getTiempoSalida(), getDuracionMs());
    }

    public Optional<String> traceparenteSaliente() {
        return Traceparent.emitir(getCorrelacionId(), getTransaccionId());
    }

    private void asignarCorrelacion(final SolicitudTraza solicitud) {
        setCorrelacionId(CorrelacionEntrante.validar(solicitud.correlacionEntrante())
                .or(() -> Traceparent.extraerTraceId(solicitud.traceparentEntrante()))
                .orElseGet(IdentificadorTraza::nuevaCorrelacion));
    }

    private static String padreDe(final SolicitudTraza solicitud) {
        return switch (solicitud.origen()) {
            case HTTP -> Traceparent.extraerParentId(solicitud.traceparentEntrante()).orElse(UtilTexto.VACIO);
            case EVENTO -> UtilTexto.aplicarTrim(solicitud.transaccionPadreEntrante());
            case PROGRAMADO -> UtilTexto.VACIO;
        };
    }

    private static String usuarioSemilla(final OrigenTraza origen) {
        return switch (origen) {
            case PROGRAMADO -> TrazaValores.SISTEMA;
            case EVENTO -> TrazaValores.EVENTO;
            case HTTP -> TrazaValores.ANONIMO;
        };
    }

    private static DetalleOrigenTraza detalleDe(final SolicitudTraza solicitud, final boolean anonimizarIp) {
        return switch (solicitud.origen()) {
            case HTTP -> new DetalleOrigenTraza.DetalleHttpTraza(
                    ClienteIp.paraTraza(solicitud.clienteIp(), anonimizarIp),
                    UtilTexto.aplicarTrim(solicitud.metodoHttp()),
                    rutaDe(solicitud));
            case EVENTO -> new DetalleOrigenTraza.DetalleEventoTraza(colaDe(solicitud));
            case PROGRAMADO -> new DetalleOrigenTraza.DetalleProgramadoTraza();
        };
    }

    private static String rutaDe(final SolicitudTraza solicitud) {
        return UtilTexto.esVacioONulo(solicitud.rutaUri())
                ? UtilTexto.VACIO
                : RutaUri.sanear(solicitud.rutaUri());
    }

    private static String colaDe(final SolicitudTraza solicitud) {
        return UtilTexto.esVacioONulo(solicitud.colaEvento())
                ? TrazaValores.DESCONOCIDO
                : UtilTexto.aplicarTrim(solicitud.colaEvento());
    }

    public String getCorrelacionId() {
        return correlacionId;
    }

    private void setCorrelacionId(final String correlacionId) {
        this.correlacionId = correlacionId;
    }

    public String getTransaccionId() {
        return transaccionId;
    }

    private void setTransaccionId(final String transaccionId) {
        this.transaccionId = transaccionId;
    }

    public String getTransaccionPadreId() {
        return transaccionPadreId;
    }

    private void setTransaccionPadreId(final String transaccionPadreId) {
        this.transaccionPadreId = transaccionPadreId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    private void setUsuarioId(final String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public DetalleOrigenTraza getDetalle() {
        return detalle;
    }

    private void setDetalle(final DetalleOrigenTraza detalle) {
        this.detalle = detalle;
    }

    public OrigenTraza getOrigen() {
        return origen;
    }

    private void setOrigen(final OrigenTraza origen) {
        this.origen = origen;
    }

    public Instant getTiempoEntrada() {
        return tiempoEntrada;
    }

    private void setTiempoEntrada(final Instant tiempoEntrada) {
        this.tiempoEntrada = tiempoEntrada;
    }

    public Instant getTiempoSalida() {
        return tiempoSalida;
    }

    private void setTiempoSalida(final Instant tiempoSalida) {
        this.tiempoSalida = tiempoSalida;
    }

    public long getDuracionMs() {
        return duracionMs;
    }

    private void setDuracionMs(final long duracionMs) {
        this.duracionMs = duracionMs;
    }

    public int getCodigoEstado() {
        return codigoEstado;
    }

    private void setCodigoEstado(final int codigoEstado) {
        this.codigoEstado = codigoEstado;
    }

    private long getInicioNano() {
        return inicioNano;
    }

    private void setInicioNano(final long inicioNano) {
        this.inicioNano = inicioNano;
    }
}
