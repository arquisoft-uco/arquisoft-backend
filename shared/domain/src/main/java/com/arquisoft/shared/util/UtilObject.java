package com.arquisoft.shared.util;

/**
 * Utilidades genéricas para comprobaciones sobre objetos.
 *
 * <p>Centraliza las comprobaciones de nulidad usadas en {@code DomainValidator}
 * y en las validaciones de cada contexto acotado, eliminando el uso directo
 * de {@code == null} disperso en el código de negocio.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilObject {

    private UtilObject() {}

    /**
     * Retorna {@code true} si {@code object} es {@code null}.
     */
    public static boolean isNull(final Object object) {
        return object == null;
    }
}
