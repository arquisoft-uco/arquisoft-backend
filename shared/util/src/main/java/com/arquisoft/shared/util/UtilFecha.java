package com.arquisoft.shared.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class UtilFecha {

    private static final String PATRON_FECHA = "\\d{4}-\\d{2}-\\d{2}";

    private UtilFecha() {}

    public static LocalDate generarFechaActual() {
        return LocalDate.now();
    }

    public static Instant generarInstanteActual() {
        return Instant.now();
    }

    public static boolean fechaValida(final String fecha) {
        return !UtilObjeto.esNulo(fecha) && UtilTexto.coincidePatron(fecha, PATRON_FECHA);
    }

    public static LocalDate parsearFechaDesdeTexto(final String fecha) {
        return fechaValida(fecha)
                ? LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
    }
}
