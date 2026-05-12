package com.arquisoft.shared.validation.util;

/**
 * Utilidades para manipulación y validación de números — Singleton.
 *
 * <p>Centraliza comprobaciones de nulidad, valores por defecto y validaciones
 * de formato numérico usadas en los contextos acotados.</p>
 *
 * <p>Acceso mediante {@link #getUtilNumber()} para operaciones de instancia.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilNumber {

    private static final UtilNumber INSTANCE = new UtilNumber();

    public static final Number ZERO = 0;
    private static final String RE_DOUBLE = "\\d+\\.\\d+";

    private UtilNumber() {}

    /** Retorna la única instancia. */
    public static UtilNumber getUtilNumber() {
        return INSTANCE;
    }

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    /** Retorna {@code true} si {@code numero} es {@code null}. */
    public boolean isNull(final Number numero) {
        return UtilObject.isNull(numero);
    }

    /** Retorna {@code true} si {@code number} es {@code null} o igual a {@link #ZERO}. */
    public boolean isZero(final Number number) {
        return getDefault(number).equals(ZERO);
    }

    // ─── Valor por defecto ────────────────────────────────────────────────────

    /**
     * Retorna {@code number} si no es {@code null}; en caso contrario retorna {@code defaultValue}.
     * Si {@code defaultValue} también es {@code null}, retorna {@link #ZERO}.
     */
    public Number getDefault(final Number number, final Number defaultValue) {
        if (isNull(number)) {
            return isNull(defaultValue) ? ZERO : defaultValue;
        }
        return number;
    }

    /**
     * Retorna {@code number} si no es {@code null}; en caso contrario retorna {@link #ZERO}.
     */
    public Number getDefault(final Number number) {
        return getDefault(number, ZERO);
    }

    // ─── Validación de formato ────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code number} tiene representación de número decimal
     * con parte fraccional (ej. {@code "4.5"}, {@code "10.00"}).
     */
    public boolean isValidDoubleToNote(final Number number) {
        return UtilText.getUtilText().matchPattern(
                UtilText.getUtilText().applyTrim(number.toString()), RE_DOUBLE);
    }
}
