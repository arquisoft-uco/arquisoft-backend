package com.arquisoft.shared.util;

public final class UtilTexto {

    public static final String VACIO = "";

    private static final String PATRON_CORREO =
            "^[_A-Za-z0-9\\-\\+]+(\\.[_A-Za-z0-9\\-]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String ARROBA = "@";
    private static final String OCULTO = "***";

    private UtilTexto() {}

    public static String aplicarTrim(final String texto) {
        return UtilObjeto.esNulo(texto) ? VACIO : texto.trim();
    }

    public static boolean esVacioONulo(final String texto) {
        return aplicarTrim(texto).equals(VACIO);
    }

    public static boolean coincidePatron(final String texto, final String patron) {
        String textoSeguro  = UtilObjeto.esNulo(texto) ? VACIO : texto;
        String patronSeguro = UtilObjeto.esNulo(patron) ? VACIO : patron;
        return textoSeguro.matches(patronSeguro);
    }

    public static boolean correoValido(final String correo) {
        return coincidePatron(correo, PATRON_CORREO);
    }
    public static String enmascararCorreo(final String correo) {
        String seguro = aplicarTrim(correo);
        int arroba = seguro.indexOf(ARROBA);
        if (arroba < 1) {
            return seguro.isEmpty() ? VACIO : OCULTO;
        }
        return seguro.charAt(0) + OCULTO + seguro.substring(arroba);
    }
}
