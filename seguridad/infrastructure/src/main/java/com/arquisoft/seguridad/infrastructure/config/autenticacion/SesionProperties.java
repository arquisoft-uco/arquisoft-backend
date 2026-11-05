package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.infrastructure.config.security.UrlProveedor;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("security.authentication.session")
public record SesionProperties(
        @DefaultValue("sesion-pdp") String id,
        @DefaultValue("false") boolean enabled,
        @DefaultValue("SECURITY_BASELINE_SESSION") String cookieName,
        @DefaultValue("") String pdpUrl,
        @DefaultValue("false") boolean allowInsecureHttp,
        @DefaultValue("1s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout,
        @DefaultValue Cache cache) {

    private static final String PROPIEDAD_URL = "security.authentication.session.pdp-url";
    private static final String FALTA_ID = "falta security.authentication.session.id";
    private static final String FALTA_COOKIE = "falta security.authentication.session.cookie-name";
    private static final String FALTA_URL = "falta " + PROPIEDAD_URL;

    public record Cache(
            @DefaultValue("PT60S") Duration ttl,
            @DefaultValue("10000") int maxSize) {

        public boolean habilitada() {
            return !ttl.isZero() && !ttl.isNegative() && maxSize > 0;
        }
    }

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        if (UtilTexto.esVacioONulo(id)) {
            problemas.add(FALTA_ID);
        }
        if (UtilTexto.esVacioONulo(cookieName)) {
            problemas.add(FALTA_COOKIE);
        }
        if (UtilTexto.esVacioONulo(pdpUrl)) {
            problemas.add(FALTA_URL);
        } else {
            problemas.addAll(UrlProveedor.problemas(PROPIEDAD_URL, pdpUrl, allowInsecureHttp));
        }
        return problemas;
    }
}
