package com.arquisoft.shared.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class UtilNumero {

    public static final int CERO = 0;
    public static final BigDecimal CERO_DECIMAL = BigDecimal.ZERO;

    private UtilNumero() {}

    public static boolean esCero(final Number numero) {
        return UtilObjeto.esNulo(numero) || esFinito(numero) && comoDecimal(numero).signum() == CERO;
    }

    public static boolean tieneParteDecimal(final Number numero) {
        return UtilObjeto.noEsNulo(numero) && esFinito(numero)
                && comoDecimal(numero).stripTrailingZeros().scale() > CERO;
    }

    public static <T extends Number> T obtenerPorDefecto(final T numero, final T valorPorDefecto) {
        return UtilObjeto.aplicarPorDefecto(numero, valorPorDefecto);
    }

    public static int obtenerPorDefecto(final Integer numero) {
        return obtenerPorDefecto(numero, CERO);
    }

    public static long obtenerPorDefecto(final Long numero) {
        return obtenerPorDefecto(numero, Long.valueOf(CERO));
    }

    public static double obtenerPorDefecto(final Double numero) {
        return obtenerPorDefecto(numero, Double.valueOf(CERO));
    }

    public static BigDecimal obtenerPorDefecto(final BigDecimal numero) {
        return obtenerPorDefecto(numero, CERO_DECIMAL);
    }

    private static boolean esFinito(final Number numero) {
        return !(numero instanceof Double || numero instanceof Float) || Double.isFinite(numero.doubleValue());
    }

    private static BigDecimal comoDecimal(final Number numero) {
        return switch (numero) {
            case BigDecimal decimal -> decimal;
            case BigInteger entero -> new BigDecimal(entero);
            case Double real -> BigDecimal.valueOf(real);
            case Float real -> new BigDecimal(Float.toString(real));
            default -> BigDecimal.valueOf(numero.longValue());
        };
    }
}
