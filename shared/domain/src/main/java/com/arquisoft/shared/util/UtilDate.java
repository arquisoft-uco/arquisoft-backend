package com.arquisoft.shared.util;

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

    private static final String DATE_RE = "\\d{4}-\\d{2}-\\d{2}";

    private UtilDate() {}

    // ─── Generación ───────────────────────────────────────────────────────────

    /** Retorna la fecha actual del sistema. */
    public static LocalDate generateNewFechaNow() {
        return LocalDate.now();
    }

    // ─── Validación ───────────────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code dateValue} tiene el formato {@code yyyy-MM-dd}.
     * Retorna {@code false} si {@code dateValue} es {@code null}.
     */
    public static boolean dateStringIsValid(final String dateValue) {
        return !UtilObject.isNull(dateValue) && UtilText.matchPattern(dateValue, DATE_RE);
    }

    // ─── Conversión ───────────────────────────────────────────────────────────

    /**
     * Convierte {@code fechaValue} a {@link LocalDate} si el formato es válido.
     * Retorna null si la cadena no tiene el formato esperado.
     */
    public static LocalDate generateFechaFromString(final String fechaValue) {
        return dateStringIsValid(fechaValue)
                ? LocalDate.parse(fechaValue, DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
    }
}
