package com.arquisoft.shared.util;

import java.util.Optional;

public final class UtilEnum {

    private UtilEnum() {}

    public static <E extends Enum<E>> Optional<E> desde(final Class<E> tipo, final String id) {
        if (UtilObjeto.esNulo(tipo) || UtilTexto.esVacioONulo(id)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Enum.valueOf(tipo, UtilTexto.aplicarTrim(id)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public static <E extends Enum<E>> boolean esValido(final Class<E> tipo, final String id) {
        return desde(tipo, id).isPresent();
    }
}
