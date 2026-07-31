package com.arquisoft.shared.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Utilidades null-safe para colecciones — clase de utilidad estática.
 *
 * <p>Java puro — sin dependencias de Spring ni Jakarta.</p>
 */
public final class UtilCollection {

    private UtilCollection() {}

    /** Retorna {@code true} si la colección es {@code null} o no tiene elementos. */
    public static boolean isEmptyOrNull(final Collection<?> collection) {
        return UtilObject.isNull(collection) || collection.isEmpty();
    }

    /**
     * Retorna el primer elemento repetido de la colección, en orden de iteración.
     * Fuente única de detección de duplicados para validadores de dominio y aplicación.
     *
     * @return {@link Optional} con el primer duplicado, o vacío si no hay repetidos
     *         (incluye colección {@code null} o vacía).
     */
    public static <T> Optional<T> firstDuplicate(final Collection<T> collection) {
        if (isEmptyOrNull(collection)) {
            return Optional.empty();
        }
        Set<T> visitados = new HashSet<>();
        return collection.stream()
                .filter(elemento -> !visitados.add(elemento))
                .findFirst();
    }
}
