package com.arquisoft.shared.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class UtilFecha {

    public static final Instant VACIO = Instant.EPOCH;

    private static final String PATRON_FECHA = "\\d{4}-\\d{2}-\\d{2}";

    private UtilFecha() {}

    public static LocalDate generarFechaActual() {
        return LocalDate.now();
    }

    public static Instant generarInstanteActual() {
        return Instant.now();
    }

    public static boolean fechaValida(final String fecha) {
        return !UtilObjeto.esNulo(fecha)
                && UtilTexto.coincidePatron(fecha, PATRON_FECHA)
                && existeEnCalendario(fecha);
    }

    public static LocalDate parsearFechaDesdeTexto(final String fecha) {
        return fechaValida(fecha)
                ? LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
    }

    private static boolean existeEnCalendario(final String fecha) {
        try {
            LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException excepcion) {
            return false;
        }
    }
}
