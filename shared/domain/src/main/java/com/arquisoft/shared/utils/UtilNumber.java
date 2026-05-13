package com.arquisoft.shared.utils;

/**
 * Utilidades para manipulación y validación de números.
 *
 * <p>Centraliza comprobaciones de nulidad, valores por defecto y validaciones
 * de formato numérico usadas en los contextos acotados.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilNumber {

    private static final Number ZERO = 0;
    private static final String RE_DOUBLE = "\\d+\\.\\d+";

    private UtilNumber() {}

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    /** Retorna {@code true} si {@code number} es {@code null} o igual a {@link #ZERO}. */
    public static boolean isZero(final Number number) {
        return getDefault(number).equals(ZERO);
    }

    // ─── Valor por defecto ────────────────────────────────────────────────────

    /**
     * Retorna {@code number} si no es {@code null}; en caso contrario retorna {@code defaultValue}.
     * Si {@code defaultValue} también es {@code null}, retorna {@link #ZERO}.
     */
    public static Number getDefault(final Number number, final Number defaultValue) {
        if (UtilObject.isNull(number)) {
            return UtilObject.isNull(defaultValue) ? ZERO : defaultValue;
        }
        return number;
    }

    /**
     * Retorna {@code number} si no es {@code null}; en caso contrario retorna {@link #ZERO}.
     */
    public static Number getDefault(final Number number) {
        return getDefault(number, ZERO);
    }

    // ─── Validación de formato ────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code number} tiene representación de número decimal
     * con parte fraccional (ej. {@code "4.5"}, {@code "10.00"}).
     */
    public static boolean isValidDoubleToNote(final Number number) {
        return UtilText.matchPattern(
                UtilText.applyTrim(number.toString()), RE_DOUBLE);
    }
}
