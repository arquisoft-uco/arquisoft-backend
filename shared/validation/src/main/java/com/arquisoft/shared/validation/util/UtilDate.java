package com.arquisoft.shared.validation.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para manipulación y validación de fechas — clase de utilidad estática.
 *
 * <p>Centraliza operaciones de fecha usadas en los contextos acotados:
 * validación de formato, conversión desde cadena y obtención de valor por defecto.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilDate {

    public static final String DEFAULT_DATE_AS_STRING = "1970-01-01";
    public static final LocalDate DEFAULT_DATE =
            LocalDate.parse(DEFAULT_DATE_AS_STRING, DateTimeFormatter.ISO_LOCAL_DATE);

    private static final String DATE_RE = "\\d{4}-\\d{2}-\\d{2}";

    private UtilDate() {}

    // ─── Generación ───────────────────────────────────────────────────────────

    /** Retorna la fecha actual del sistema. */
    public static LocalDate generateNewFecha() {
        return LocalDate.now();
    }

    // ─── Validación ───────────────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code dateValue} tiene el formato {@code yyyy-MM-dd}.
     * Retorna {@code false} si {@code dateValue} es {@code null}.
     */
    public static boolean dateStringIsValid(final String dateValue) {
        return !UtilObject.isNull(dateValue) && UtilText.getUtilText().matchPattern(dateValue, DATE_RE);
    }

    // ─── Conversión ───────────────────────────────────────────────────────────

    /**
     * Convierte {@code fechaValue} a {@link LocalDate} si el formato es válido.
     * Retorna {@link #DEFAULT_DATE} si la cadena no tiene el formato esperado.
     */
    public static LocalDate generateFechaFromString(final String fechaValue) {
        return dateStringIsValid(fechaValue)
                ? LocalDate.parse(fechaValue, DateTimeFormatter.ISO_LOCAL_DATE)
                : DEFAULT_DATE;
    }

    // ─── Valor por defecto ────────────────────────────────────────────────────

    /**
     * Retorna {@code dateValue} si no es {@code null}; en caso contrario retorna {@link #DEFAULT_DATE}.
     */
    public static LocalDate getDefault(final LocalDate dateValue) {
        return UtilObject.isNull(dateValue) ? DEFAULT_DATE : dateValue;
    }

    /**
     * Retorna {@code true} si {@code dateValue} es {@code null} o igual a {@link #DEFAULT_DATE}.
     */
    public static boolean isDefaultDateOrNull(final LocalDate dateValue) {
        return UtilObject.isNull(dateValue) || dateValue.isEqual(DEFAULT_DATE);
    }
}
