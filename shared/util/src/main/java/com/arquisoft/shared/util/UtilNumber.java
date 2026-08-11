package com.arquisoft.shared.util;

public final class UtilNumber {

    private static final Number ZERO = 0;
    private static final String RE_DOUBLE = "\\d+\\.\\d+";

    private UtilNumber() {}

    public static boolean isZero(final Number number) {
        return getDefault(number).equals(ZERO);
    }

    public static Number getDefault(final Number number, final Number defaultValue) {
        if (UtilObject.isNull(number)) {
            return UtilObject.isNull(defaultValue) ? ZERO : defaultValue;
        }
        return number;
    }

    public static Number getDefault(final Number number) {
        return getDefault(number, ZERO);
    }

    public static boolean isValidDecimalFormat(final Number number) {
        return UtilText.matchPattern(
                UtilText.applyTrim(number.toString()), RE_DOUBLE);
    }
}
