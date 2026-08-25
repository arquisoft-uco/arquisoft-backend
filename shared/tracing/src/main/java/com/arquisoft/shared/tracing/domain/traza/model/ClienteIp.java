package com.arquisoft.shared.tracing.domain.traza.model;

import com.arquisoft.shared.util.UtilTexto;

import java.util.regex.Pattern;

public final class ClienteIp {

    private static final Pattern CARACTERES_VALIDOS = Pattern.compile("^[\\d.:a-fA-F]{3,45}$");

    private static final String SEPARADOR_IPV4 = ".";

    private static final String SEPARADOR_IPV6 = ":";

    private static final String COMPRESION_IPV6 = "::";

    private static final int GRUPOS_IPV4 = 4;

    private static final int GRUPOS_IPV6 = 8;

    private static final int GRUPOS_IPV6_CONSERVADOS = 3;

    private static final String OCTETO_ANONIMO = "0";

    private static final String GRUPO_ANONIMO = "0";

    private ClienteIp() {}

    public static String paraTraza(final String ip, final boolean anonimizar) {
        if (UtilTexto.esVacioONulo(ip) || !CARACTERES_VALIDOS.matcher(ip).matches()) {
            return TrazaValores.INVALIDO;
        }
        return anonimizar ? anonimizar(ip) : ip;
    }

    private static String anonimizar(final String ip) {
        if (ip.contains(SEPARADOR_IPV6)) {
            return anonimizarIpv6(ip);
        }
        return anonimizarIpv4(ip);
    }

    private static String anonimizarIpv4(final String ip) {
        String[] octetos = ip.split("\\.");
        if (octetos.length != GRUPOS_IPV4) {
            return TrazaValores.INVALIDO;
        }
        octetos[GRUPOS_IPV4 - 1] = OCTETO_ANONIMO;
        return String.join(SEPARADOR_IPV4, octetos);
    }

    private static String anonimizarIpv6(final String ip) {
        String[] grupos = expandir(ip);
        if (grupos == null) {
            return TrazaValores.INVALIDO;
        }
        var prefijo = new StringBuilder();
        for (int i = 0; i < GRUPOS_IPV6_CONSERVADOS; i++) {
            prefijo.append(grupos[i]).append(SEPARADOR_IPV6);
        }
        return prefijo.append(SEPARADOR_IPV6).toString();
    }

    private static String[] expandir(final String ip) {
        int compresion = ip.indexOf(COMPRESION_IPV6);
        if (compresion < 0) {
            String[] grupos = grupos(ip);
            return grupos.length == GRUPOS_IPV6 ? grupos : null;
        }
        String[] cabeza = grupos(ip.substring(0, compresion));
        String[] cola = grupos(ip.substring(compresion + COMPRESION_IPV6.length()));
        int ceros = GRUPOS_IPV6 - cabeza.length - cola.length;
        if (ceros < 0) {
            return null;
        }
        var expandida = new String[GRUPOS_IPV6];
        System.arraycopy(cabeza, 0, expandida, 0, cabeza.length);
        for (int i = 0; i < ceros; i++) {
            expandida[cabeza.length + i] = GRUPO_ANONIMO;
        }
        System.arraycopy(cola, 0, expandida, cabeza.length + ceros, cola.length);
        return expandida;
    }

    private static String[] grupos(final String tramo) {
        return tramo.isEmpty() ? new String[0] : tramo.split(SEPARADOR_IPV6, -1);
    }
}
