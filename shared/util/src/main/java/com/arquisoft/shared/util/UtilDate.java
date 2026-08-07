package com.arquisoft.shared.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class UtilDate {

    private static final String DATE_RE = "\\d{4}-\\d{2}-\\d{2}";

    private UtilDate() {}

    // ─── Generación ───────────────────────────────────────────────────────────

    public static LocalDate generateCurrentDate() {
        return LocalDate.now();
    }

    public static Instant generateNewInstantNow() {
        return Instant.now();
    }

    // ─── Validación ───────────────────────────────────────────────────────────

    public static boolean dateStringIsValid(final String dateValue) {
        return !UtilObject.isNull(dateValue) && UtilText.matchPattern(dateValue, DATE_RE);
    }

    // ─── Conversión ───────────────────────────────────────────────────────────

    public static LocalDate parseDateFromString(final String dateValue) {
        return dateStringIsValid(dateValue)
                ? LocalDate.parse(dateValue, DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
    }
}
