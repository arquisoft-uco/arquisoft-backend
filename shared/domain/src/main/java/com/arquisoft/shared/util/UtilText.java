package com.arquisoft.shared.util;

public final class UtilText {

    private static final String EMPTY = "";

    private static final String EMAIL_RE =
            "^[_A-Za-z0-9\\-\\+]+(\\.[_A-Za-z0-9\\-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private UtilText() {}

    // ─── Normalización ────────────────────────────────────────────────────────

    public static String applyTrim(final String text) {
        return UtilObject.isNull(text) ? EMPTY : text.trim();
    }

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    public static boolean isEmptyOrNull(final String text) {
        return applyTrim(text).equals(EMPTY);
    }

    public static boolean matchPattern(final String text, final String pattern) {
        String safeText    = UtilObject.isNull(text) ? EMPTY : text;
        String safePattern = UtilObject.isNull(pattern) ? EMPTY : pattern;
        return safeText.matches(safePattern);
    }

    // ─── Validaciones de formato ──────────────────────────────────────────────

    public static boolean emailStringIsValid(final String emailValue) {
        return matchPattern(emailValue, EMAIL_RE);
    }
}
