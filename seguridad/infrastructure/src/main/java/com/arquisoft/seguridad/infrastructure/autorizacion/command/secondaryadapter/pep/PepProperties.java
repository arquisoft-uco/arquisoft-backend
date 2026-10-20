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

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        if (url.isBlank()) {
            problemas.add("falta security.authorization.pep.url");
        } else {
            problemas.addAll(problemasDeUrl());
        }
        if (applicationName.isBlank()) {
            problemas.add("falta security.authorization.pep.application-name");
        }
        if (environment.isBlank()) {
            problemas.add("falta security.authorization.pep.environment");
        }
        if (credential.isBlank()) {
            problemas.add("falta la credencial de la aplicacion ante el PEP");
        }
        return problemas;
    }

    private List<String> problemasDeUrl() {
        try {
            var uri = URI.create(url);
            var problemas = new ArrayList<String>();
            var esHttps = "https".equalsIgnoreCase(uri.getScheme());
            var esHttp = "http".equalsIgnoreCase(uri.getScheme());
            if (!esHttps && !(esHttp && allowInsecureHttp)) {
                problemas.add("security.authorization.pep.url debe ser HTTPS (HTTP solo con allow-insecure-http=true)");
            }
            if (UtilObjeto.noEsNulo(uri.getUserInfo()) || UtilObjeto.noEsNulo(uri.getQuery())
                    || UtilObjeto.noEsNulo(uri.getFragment())) {
                problemas.add("security.authorization.pep.url no admite userinfo, query ni fragmento");
            }
            return problemas;
        } catch (IllegalArgumentException e) {
            return List.of("security.authorization.pep.url no es una URI valida");
        }
    }

    @Override
    public String toString() {
        return "PepProperties[url=" + url + ", applicationName=" + applicationName + ", environment=" + environment
                + ", credential=***, allowInsecureHttp=" + allowInsecureHttp + "]";
    }
}
