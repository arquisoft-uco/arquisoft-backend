package com.arquisoft.shared.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class UtilCollection {

    private UtilCollection() {}

    public static boolean isEmptyOrNull(final Collection<?> collection) {
        return UtilObject.isNull(collection) || collection.isEmpty();
    }

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
