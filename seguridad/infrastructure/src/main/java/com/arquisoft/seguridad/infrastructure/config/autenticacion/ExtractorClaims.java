package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import java.util.List;
import java.util.Map;

public final class ExtractorClaims {

    private static final String SEPARADOR_RUTA = "\\.";

    private ExtractorClaims() {}

    public static Object valor(Map<String, Object> claims, String ruta) {
        if (UtilObjeto.esNulo(claims) || UtilTexto.esVacioONulo(ruta)) {
            return null;
        }
        Object actual = claims;
        for (var segmento : ruta.split(SEPARADOR_RUTA)) {
            if (actual instanceof Map<?, ?> mapa) {
                actual = mapa.get(segmento);
            } else {
                return null;
            }
        }
        return actual;
    }

    public static String texto(Map<String, Object> claims, String ruta) {
        return valor(claims, ruta) instanceof String texto ? texto : null;
    }

    public static List<String> lista(Map<String, Object> claims, String ruta) {
        if (valor(claims, ruta) instanceof List<?> elementos) {
            return elementos.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }
}
