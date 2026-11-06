package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep;

import com.arquisoft.seguridad.infrastructure.config.security.UrlProveedor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

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
    private static final String PROPIEDAD_URL = "security.authorization.pep.url";

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
        return UrlProveedor.problemas(PROPIEDAD_URL, url, allowInsecureHttp);
    }

    @Override
    public String toString() {
        return "PepProperties[url=" + url + ", applicationName=" + applicationName + ", environment=" + environment
                + ", credential=***, allowInsecureHttp=" + allowInsecureHttp + "]";
    }
}
