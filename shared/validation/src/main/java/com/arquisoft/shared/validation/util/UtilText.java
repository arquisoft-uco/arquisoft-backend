package com.arquisoft.shared.validation.util;

/**
 * Utilidades de texto para validación de dominio — Singleton.
 *
 * <p>Centraliza operaciones de normalización y validación de cadenas usadas
 * en {@code DomainValidator} y en las validaciones de cada contexto acotado.</p>
 *
 * <p>Acceso mediante {@link #getUtilText()} para operaciones de instancia
 * ({@link #applyTrim}, {@link #isEmpty}, {@link #matchPattern}). Los métodos
 * de validación de formatos concretos (ej. {@link #emailStringIsValid}) se
 * exponen también como estáticos para mayor comodidad en el caller.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilText {

    private static final UtilText INSTANCE = new UtilText();

    /** Cadena vacía usada como valor neutro en normalizaciones. */
    public static final String EMPTY = "";

    private static final String EMAIL_RE =
            "^[_A-Za-z0-9\\-\\+]+(\\.[_A-Za-z0-9\\-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private UtilText() {}

    /** Retorna la única instancia. */
    public static UtilText getUtilText() {
        return INSTANCE;
    }

    // ─── Normalización ────────────────────────────────────────────────────────

    /**
     * Retorna {@code text} sin espacios extremos.
     * Si {@code text} es {@code null} retorna {@link #EMPTY}.
     */
    public String applyTrim(final String text) {
        return text == null ? EMPTY : text.trim();
    }

    // ─── Comprobaciones ───────────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code text} es {@code null}, vacío o solo espacios.
     */
    public boolean isEmpty(final String text) {
        return applyTrim(text).equals(EMPTY);
    }

    /**
     * Retorna {@code true} si {@code text} coincide con {@code pattern}.
     * Trata {@code null} en cualquiera de los dos argumentos como cadena vacía.
     */
    public boolean matchPattern(final String text, final String pattern) {
        String safeText    = text    == null ? EMPTY : text;
        String safePattern = pattern == null ? EMPTY : pattern;
        return safeText.matches(safePattern);
    }

    // ─── Validaciones de formato ──────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code emailValue} tiene formato de correo electrónico válido.
     */
    public static boolean emailStringIsValid(final String emailValue) {
        return getUtilText().matchPattern(emailValue, EMAIL_RE);
    }
}
