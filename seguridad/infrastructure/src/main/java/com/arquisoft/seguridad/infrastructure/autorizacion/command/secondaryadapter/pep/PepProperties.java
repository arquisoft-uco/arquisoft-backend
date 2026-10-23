package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep;

import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("security.authorization.pep")
public record PepProperties(
        @DefaultValue("") String url,
        @DefaultValue("") String applicationName,
        @DefaultValue("") String environment,
        @DefaultValue("") String credential,
        @DefaultValue("false") boolean allowInsecureHttp,
        @DefaultValue("1s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout,
        @DefaultValue("0") int maxRetries,
        @DefaultValue("200ms") Duration retryBackoff) {

    private static final String FALTA_URL = "falta security.authorization.pep.url";
    private static final String FALTA_APPLICATION_NAME = "falta security.authorization.pep.application-name";
    private static final String FALTA_ENVIRONMENT = "falta security.authorization.pep.environment";
    private static final String FALTA_CREDENCIAL = "falta la credencial de la aplicacion ante el PEP";
    private static final String URL_NO_HTTPS =
            "security.authorization.pep.url debe ser HTTPS (HTTP solo con allow-insecure-http=true)";
    private static final String URL_CON_PARTES_PROHIBIDAS =
            "security.authorization.pep.url no admite userinfo, query ni fragmento";
    private static final String URL_INVALIDA = "security.authorization.pep.url no es una URI valida";
    private static final String ESQUEMA_HTTPS = "https";
    private static final String ESQUEMA_HTTP = "http";

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        if (url.isBlank()) {
            problemas.add(FALTA_URL);
        } else {
            problemas.addAll(problemasDeUrl());
        }
        if (applicationName.isBlank()) {
            problemas.add(FALTA_APPLICATION_NAME);
        }
        if (environment.isBlank()) {
            problemas.add(FALTA_ENVIRONMENT);
        }
        if (credential.isBlank()) {
            problemas.add(FALTA_CREDENCIAL);
        }
        return problemas;
    }

    private List<String> problemasDeUrl() {
        try {
            var uri = URI.create(url);
            var problemas = new ArrayList<String>();
            var esHttps = ESQUEMA_HTTPS.equalsIgnoreCase(uri.getScheme());
            var esHttp = ESQUEMA_HTTP.equalsIgnoreCase(uri.getScheme());
            if (!esHttps && !(esHttp && allowInsecureHttp)) {
                problemas.add(URL_NO_HTTPS);
            }
            if (UtilObjeto.noEsNulo(uri.getUserInfo()) || UtilObjeto.noEsNulo(uri.getQuery())
                    || UtilObjeto.noEsNulo(uri.getFragment())) {
                problemas.add(URL_CON_PARTES_PROHIBIDAS);
            }
            return problemas;
        } catch (IllegalArgumentException e) {
            return List.of(URL_INVALIDA);
        }
    }

    @Override
    public String toString() {
        return "PepProperties[url=" + url + ", applicationName=" + applicationName + ", environment=" + environment
                + ", credential=***, allowInsecureHttp=" + allowInsecureHttp + "]";
    }
}
