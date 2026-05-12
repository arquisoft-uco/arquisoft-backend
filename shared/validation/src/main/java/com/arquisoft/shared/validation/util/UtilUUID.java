package com.arquisoft.shared.validation.util;

import java.util.UUID;

/**
 * Utilidades para manipulación y validación de {@link UUID} — clase de utilidad estática.
 *
 * <p>Delega la validación de formato de cadena UUID en {@link UtilText#matchPattern}
 * para mantener una única fuente de verdad para el matching de patrones.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilUUID {

    private static final String UUID_RE =
            "[a-f0-9]{8}([-][a-f0-9]{4}){3}[-][a-f0-9]{12}";

    private UtilUUID() {}

    // ─── Generación ───────────────────────────────────────────────────────────

    /** Genera y retorna un nuevo {@link UUID} aleatorio. */
    public static UUID generateNewUUID() {
        return UUID.randomUUID();
    }

    // ─── Validación ───────────────────────────────────────────────────────────

    /**
     * Retorna {@code true} si {@code uuidValue} tiene el formato estándar de UUID
     * (8-4-4-4-12 hex en minúsculas).
     */
    public static boolean uuidStringIsValid(final String uuidValue) {
        return UtilText.matchPattern(uuidValue, UUID_RE);
    }

    // ─── Conversión ───────────────────────────────────────────────────────────

    /**
     * Convierte {@code uuidValue} a {@link UUID} si el formato es válido.
     * Retorna {@code null} si la cadena no es un UUID bien formado.
     */
    public static UUID generateUUIDFromString(final String uuidValue) {
        return uuidStringIsValid(uuidValue) ? UUID.fromString(uuidValue) : null;
    }
}
