package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.regex.Pattern;

public final class RutaUri {

    private static final Pattern CONTROL = Pattern.compile("[\r\n\t\0]");

    private static final String REEMPLAZO = "_";

    private RutaUri() {}

    public static String sanear(final String path) {
        if (UtilTexto.esVacioONulo(path)) {
            return TrazaValores.DESCONOCIDO;
        }
        return CONTROL.matcher(path).replaceAll(REEMPLAZO);
    }
}
