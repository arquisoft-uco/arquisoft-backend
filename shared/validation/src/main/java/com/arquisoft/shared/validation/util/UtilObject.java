package com.arquisoft.shared.validation.util;

/**
 * Utilidades genéricas para comprobaciones sobre objetos — interfaz de métodos estáticos.
 *
 * <p>Centraliza las comprobaciones de nulidad usadas en {@code DomainValidator}
 * y en las validaciones de cada contexto acotado, eliminando el uso directo
 * de {@code == null} disperso en el código de negocio.</p>
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public interface UtilObject {

    /**
     * Retorna {@code true} si {@code object} es {@code null}.
     */
    static <O> boolean isNull(final O object) {
        return object == null;
    }

    /**
     * Retorna {@code object} si no es {@code null}; en caso contrario retorna {@code defaultValue}.
     */
    static <O> O getDefault(final O object, final O defaultValue) {
        return isNull(object) ? defaultValue : object;
    }
}
