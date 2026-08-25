package com.arquisoft.shared.util;

public final class UtilNumero {

    private static final Number CERO = 0;
    private static final String PATRON_DECIMAL = "\\d+\\.\\d+";

    private UtilNumero() {}

    public static boolean esCero(final Number numero) {
        return obtenerPorDefecto(numero).equals(CERO);
    }

    public static Number obtenerPorDefecto(final Number numero, final Number valorPorDefecto) {
        if (UtilObjeto.esNulo(numero)) {
            return UtilObjeto.esNulo(valorPorDefecto) ? CERO : valorPorDefecto;
        }
        return numero;
    }

    public static Number obtenerPorDefecto(final Number numero) {
        return obtenerPorDefecto(numero, CERO);
    }

    public static boolean formatoDecimalValido(final Number numero) {
        return UtilTexto.coincidePatron(
                UtilTexto.aplicarTrim(numero.toString()), PATRON_DECIMAL);
    }
}
