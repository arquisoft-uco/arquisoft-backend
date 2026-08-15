package com.arquisoft.shared.util;

public final class UtilObjeto {

    private UtilObjeto() {}

    public static boolean esNulo(final Object object) {
        return object == null;
    }

    /**
     * Devuelve {@code valor}, o {@code porDefecto} si el primero es nulo.
     *
     * <p>Equivalente objetual de {@link UtilTexto#aplicarTrim} y
     * {@link UtilColeccion#aplicarPorDefecto}: existe para que el reemplazo de un nulo por su
     * valor neutro se escriba una sola vez y no como un ternario repetido en cada llamador.
     *
     * @param valor      valor a comprobar
     * @param porDefecto valor neutro a usar cuando {@code valor} es nulo
     * @param <T>        tipo del valor
     * @return {@code valor} si no es nulo, {@code porDefecto} en caso contrario
     */
    public static <T> T aplicarPorDefecto(final T valor, final T porDefecto) {
        return esNulo(valor) ? porDefecto : valor;
    }
}
