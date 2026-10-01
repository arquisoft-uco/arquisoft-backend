package com.arquisoft.shared.util;

public final class UtilObjeto {

    private UtilObjeto() {}

    public static boolean esNulo(final Object object) {
        return object == null;
    }

    public static boolean noEsNulo(final Object object) {
        return !esNulo(object);
    }

    public static <T> T aplicarPorDefecto(final T valor, final T porDefecto) {
        return esNulo(valor) ? porDefecto : valor;
    }
}
