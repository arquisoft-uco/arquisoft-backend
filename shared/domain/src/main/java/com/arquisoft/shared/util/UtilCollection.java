package com.arquisoft.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class UtilCollection {

    private UtilCollection() {}

    public static boolean isEmptyOrNull(final Collection<?> collection) {
        return UtilObject.isNull(collection) || collection.isEmpty();
    }

    public static <T> List<T> applyDefault(final Collection<T> collection) {
        return isEmptyOrNull(collection)
                ? List.of()
                : Collections.unmodifiableList(new ArrayList<>(collection));
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
