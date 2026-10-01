package com.arquisoft.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class UtilColeccion {

    private UtilColeccion() {}

    public static boolean esVaciaONula(final Collection<?> coleccion) {
        return UtilObjeto.esNulo(coleccion) || coleccion.isEmpty();
    }

    public static <T> List<T> aplicarPorDefecto(final Collection<T> coleccion) {
        return esVaciaONula(coleccion)
                ? List.of()
                : Collections.unmodifiableList(new ArrayList<>(coleccion));
    }

    public static <T> Optional<T> primerDuplicado(final Collection<T> coleccion) {
        if (esVaciaONula(coleccion)) {
            return Optional.empty();
        }
        Set<T> visitados = new HashSet<>();
        return coleccion.stream()
                .filter(elemento -> !visitados.add(elemento))
                .findFirst();
    }
}
