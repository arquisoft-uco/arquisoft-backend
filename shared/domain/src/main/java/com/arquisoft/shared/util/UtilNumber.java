package com.arquisoft.shared.util;

public final class UtilNumber {

    private static final Number ZERO = 0;
    private static final String RE_DOUBLE = "\\d+\\.\\d+";

    private UtilNumber() {}

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    public static boolean isZero(final Number number) {
        return getDefault(number).equals(ZERO);
    }

    // ─── Valor por defecto ────────────────────────────────────────────────────

    public static Number getDefault(final Number number, final Number defaultValue) {
        if (UtilObject.isNull(number)) {
            return UtilObject.isNull(defaultValue) ? ZERO : defaultValue;
        }
        return number;
    }

    public static Number getDefault(final Number number) {
        return getDefault(number, ZERO);
    }

    // ─── Validación de formato ────────────────────────────────────────────────

    public static boolean isValidDoubleToNote(final Number number) {
        return UtilText.matchPattern(
                UtilText.applyTrim(number.toString()), RE_DOUBLE);
    }
}
