package com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc;

import com.arquisoft.shared.tracing.application.traza.secondaryport.ContextoDiagnosticoOutputPort;
import com.arquisoft.shared.tracing.domain.traza.TrazaDomain;
import com.arquisoft.shared.tracing.domain.traza.model.DetalleOrigenTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SalidaTraza;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MdcContextoDiagnosticoOutputAdapter implements ContextoDiagnosticoOutputPort {

    @Override
    public Map<String, String> capturar() {
        return MDC.getCopyOfContextMap();
    }

    @Override
    public void escribirTraza(final TrazaDomain traza) {
        escribir(TrazaKeys.CORRELACION_ID, traza.getCorrelacionId());
        escribir(TrazaKeys.TRANSACCION_ID, traza.getTransaccionId());
        escribir(TrazaKeys.USUARIO_ID, traza.getUsuarioId());
        escribir(TrazaKeys.ORIGEN, traza.getOrigen().getId());
        escribirDetalle(traza.getDetalle());
        escribir(TrazaKeys.TIEMPO_ENTRADA, formatear(traza.getTiempoEntrada()));
    }

    private void escribirDetalle(final DetalleOrigenTraza detalle) {
        switch (detalle) {
            case DetalleOrigenTraza.DetalleHttpTraza http -> {
                escribir(TrazaKeys.CLIENTE_IP, http.clienteIp());
                escribir(TrazaKeys.METODO_HTTP, http.metodoHttp());
                escribir(TrazaKeys.RUTA_URI, http.rutaUri());
            }
            case DetalleOrigenTraza.DetalleEventoTraza evento -> escribir(TrazaKeys.COLA_EVENTO, evento.colaEvento());
            case DetalleOrigenTraza.DetalleProgramadoTraza programado -> { }
        }
    }

    @Override
    public void escribirUsuario(final String usuarioId) {
        escribir(TrazaKeys.USUARIO_ID, usuarioId);
    }

    @Override
    public void escribirSalida(final SalidaTraza salida) {
        escribir(TrazaKeys.CODIGO_ESTADO, String.valueOf(salida.codigoEstado()));
        escribir(TrazaKeys.DURACION_MS, String.valueOf(salida.duracionMs()));
        escribir(TrazaKeys.TIEMPO_SALIDA, formatear(salida.tiempoSalida()));
    }

    @Override
    public void restaurar(final Map<String, String> contextoPrevio) {
        if (UtilObjeto.esNulo(contextoPrevio)) {
            MDC.clear();
            return;
        }
        MDC.setContextMap(contextoPrevio);
    }

    @Override
    public String leerCorrelacion() {
        return MDC.get(TrazaKeys.CORRELACION_ID);
    }

    @Override
    public String leerTransaccion() {
        return MDC.get(TrazaKeys.TRANSACCION_ID);
    }

    @Override
    public String leerUsuario() {
        return MDC.get(TrazaKeys.USUARIO_ID);
    }

    private void escribir(final String clave, final String valor) {
        if (!UtilTexto.esVacioONulo(valor)) {
            MDC.put(clave, valor);
        }
    }

    private String formatear(final Instant instante) {
        return UtilObjeto.esNulo(instante) ? UtilTexto.VACIO : DateTimeFormatter.ISO_INSTANT.format(instante);
    }
}
