package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.Optional;
import java.util.regex.Pattern;

public final class Traceparent {

    private static final Pattern FORMATO =
            Pattern.compile("[0-9a-f]{2}-([0-9a-f]{32})-[0-9a-f]{16}-[0-9a-f]{2}");

    private static final String VERSION = "00";

    private static final String BANDERA_MUESTREADO = "01";

    private static final String SEPARADOR = "-";

    private Traceparent() {}

    public static Optional<String> extraerTraceId(final String cabecera) {
        if (UtilTexto.esVacioONulo(cabecera)) {
            return Optional.empty();
        }
        var coincidencia = FORMATO.matcher(cabecera);
        if (!coincidencia.matches()) {
            return Optional.empty();
        }
        String traceId = coincidencia.group(1);
        return IdentificadorTraza.esFormaW3C(traceId) ? Optional.of(traceId) : Optional.empty();
    }

    public static Optional<String> emitir(final String correlacionId, final String transaccionId) {
        if (!IdentificadorTraza.esFormaW3C(correlacionId) || UtilTexto.esVacioONulo(transaccionId)) {
            return Optional.empty();
        }
        return Optional.of(VERSION + SEPARADOR + correlacionId + SEPARADOR
                + transaccionId + SEPARADOR + BANDERA_MUESTREADO);
    }
}
