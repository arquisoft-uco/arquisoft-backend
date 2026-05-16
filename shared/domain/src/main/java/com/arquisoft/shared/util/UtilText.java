package com.arquisoft.shared.util;

/**
 * Utilidades de texto para validación de dominio.
 *
 * <p>Centraliza operaciones de normalización y validación de cadenas usadas
 * en {@code DomainValidator} y en las validaciones de cada contexto acotado.</p>
 *
 * ({@link #applyTrim}, {@link #isEmptyOrNull}, {@link #matchPattern}).
 * Los métodos de validación de formatos concretos (ej. {@link #emailStringIsValid})
 * se exponen también como estáticos para mayor comodidad en el caller.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilText {

    /** Cadena vacía usada como valor neutro en normalizaciones. */
    private static final String EMPTY = "";

    private static final String EMAIL_RE =
            "^[_A-Za-z0-9\\-\\+]+(\\.[_A-Za-z0-9\\-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private UtilText() {}

    // ─── Normalización ────────────────────────────────────────────────────────

    /**
     * Retorna {@code text} sin espacios extremos.
     * Si {@code text} es {@code null} retorna {@link #EMPTY}.
     */
    public static String applyTrim(final String text) {
        return UtilObject.isNull(text) ? EMPTY : text.trim();
    }

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code text} es {@code null}, vacío o solo espacios.
     */
    public static boolean isEmptyOrNull(final String text) {
        return applyTrim(text).equals(EMPTY);
    }

    /**
     * Retorna {@code true} si {@code text} coincide con {@code pattern}.
     * Trata {@code null} en cualquiera de los dos argumentos como cadena vacía.
     */
    public static boolean matchPattern(final String text, final String pattern) {
        String safeText    = UtilObject.isNull(text) ? EMPTY : text;
        String safePattern = UtilObject.isNull(pattern) ? EMPTY : pattern;
        return safeText.matches(safePattern);
    }

    // ─── Validaciones de formato ──────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code emailValue} tiene formato de correo electrónico válido.
     */
    public static boolean emailStringIsValid(final String emailValue) {
        return matchPattern(emailValue, EMAIL_RE);
    }
}
