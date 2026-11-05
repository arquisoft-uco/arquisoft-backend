package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.util.UtilObjeto;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class UrlProveedor {

    private static final String URL_NO_HTTPS = "%s debe ser HTTPS (HTTP solo con allow-insecure-http=true)";
    private static final String URL_CON_PARTES_PROHIBIDAS = "%s no admite userinfo, query ni fragmento";
    private static final String URL_INVALIDA = "%s no es una URI valida";
    private static final String ESQUEMA_HTTPS = "https";
    private static final String ESQUEMA_HTTP = "http";

    private UrlProveedor() {}

    public static List<String> problemas(String propiedad, String url, boolean allowInsecureHttp) {
        try {
            var uri = URI.create(url);
            var problemas = new ArrayList<String>();
            var esHttps = ESQUEMA_HTTPS.equalsIgnoreCase(uri.getScheme());
            var esHttp = ESQUEMA_HTTP.equalsIgnoreCase(uri.getScheme());
            if (!esHttps && !(esHttp && allowInsecureHttp)) {
                problemas.add(URL_NO_HTTPS.formatted(propiedad));
            }
            if (UtilObjeto.noEsNulo(uri.getUserInfo()) || UtilObjeto.noEsNulo(uri.getQuery())
                    || UtilObjeto.noEsNulo(uri.getFragment())) {
                problemas.add(URL_CON_PARTES_PROHIBIDAS.formatted(propiedad));
            }
            return problemas;
        } catch (IllegalArgumentException e) {
            return List.of(URL_INVALIDA.formatted(propiedad));
        }
    }
}
