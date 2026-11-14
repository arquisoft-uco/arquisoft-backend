package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProteccionCsrf {

    public static final String CABECERA_XSRF = "X-XSRF-TOKEN";

    private static final Set<String> METODOS_SEGUROS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final OrigenesPermitidos origenesPermitidos;

    public enum Resultado {
        PERMITIDA,
        SIN_CABECERA_XSRF,
        SIN_ORIGEN,
        ORIGEN_NO_PERMITIDO
    }

    public static boolean esMetodoSeguro(String metodo) {
        return METODOS_SEGUROS.contains(UtilTexto.aplicarTrim(metodo).toUpperCase());
    }

    public Resultado evaluar(String metodo, String xsrf, String origin, String referer) {
        if (esMetodoSeguro(metodo)) {
            return Resultado.PERMITIDA;
        }
        if (UtilTexto.esVacioONulo(xsrf)) {
            return Resultado.SIN_CABECERA_XSRF;
        }
        var origen = UtilTexto.esVacioONulo(origin) ? origenDeReferer(referer) : origin;
        if (UtilTexto.esVacioONulo(origen)) {
            return Resultado.SIN_ORIGEN;
        }
        return origenesPermitidos.contiene(origen) ? Resultado.PERMITIDA : Resultado.ORIGEN_NO_PERMITIDO;
    }

    private static String origenDeReferer(String referer) {
        if (UtilTexto.esVacioONulo(referer)) {
            return UtilTexto.VACIO;
        }
        try {
            var uri = URI.create(UtilTexto.aplicarTrim(referer));
            if (UtilTexto.esVacioONulo(uri.getScheme()) || UtilTexto.esVacioONulo(uri.getHost())) {
                return UtilTexto.VACIO;
            }
            var puerto = uri.getPort() < 0 ? UtilTexto.VACIO : ":" + uri.getPort();
            return uri.getScheme() + "://" + uri.getHost() + puerto;
        } catch (IllegalArgumentException e) {
            return UtilTexto.VACIO;
        }
    }
}
