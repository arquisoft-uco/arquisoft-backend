package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.UUID;
import java.util.regex.Pattern;

public final class IdentificadorTraza {

    public static final int LONGITUD_CORRELACION = 32;

    public static final int LONGITUD_TRANSACCION = 16;

    private static final Pattern HEX_MINUSCULA = Pattern.compile("[0-9a-f]+");

    private static final String CORRELACION_NULA = "0".repeat(LONGITUD_CORRELACION);

    private IdentificadorTraza() {}

    public static String nuevaCorrelacion() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String nuevaTransaccion() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, LONGITUD_TRANSACCION);
    }

    public static boolean esFormaW3C(final String correlacionId) {
        return !UtilTexto.esVacioONulo(correlacionId)
                && correlacionId.length() == LONGITUD_CORRELACION
                && HEX_MINUSCULA.matcher(correlacionId).matches()
                && !CORRELACION_NULA.equals(correlacionId);
    }
}
