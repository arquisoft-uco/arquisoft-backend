package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.infrastructure.config.security.UrlProveedor;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("security.authentication.session.usuarios")
public record UsuarioSesionProperties(
        @DefaultValue("") String url,
        @DefaultValue("false") boolean allowInsecureHttp,
        @DefaultValue("1s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout,
        @DefaultValue("PT5M") Duration cacheTtl,
        @DefaultValue("") String tokenUrl,
        @DefaultValue("") String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue("/api/usuarios/por-correo") String porCorreoPath,
        @DefaultValue(UsuarioSesionProperties.PROVEEDOR_POR_DEFECTO) String proveedor) {

    public static final String PROVEEDOR_POR_DEFECTO = "legado";

    private static final String PROPIEDAD_URL = "security.authentication.session.usuarios.url";
    private static final String PROPIEDAD_TOKEN_URL = "security.authentication.session.usuarios.token-url";
    private static final String FALTA_URL = "falta " + PROPIEDAD_URL;
    private static final String FALTA_TOKEN_URL = "falta " + PROPIEDAD_TOKEN_URL;
    private static final String FALTA_CLIENT_ID = "falta security.authentication.session.usuarios.client-id";
    private static final String FALTA_CLIENT_SECRET = "falta security.authentication.session.usuarios.client-secret";
    private static final String FALTA_PROVEEDOR = "falta security.authentication.session.usuarios.proveedor";
    private static final String TTL_NEGATIVO = "security.authentication.session.usuarios.cache-ttl no puede ser negativo";

    public boolean cacheHabilitada() {
        return !cacheTtl.isZero() && !cacheTtl.isNegative();
    }

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        agregarProblemasUrl(problemas, PROPIEDAD_URL, url, FALTA_URL);
        agregarProblemasUrl(problemas, PROPIEDAD_TOKEN_URL, tokenUrl, FALTA_TOKEN_URL);
        if (UtilTexto.esVacioONulo(clientId)) {
            problemas.add(FALTA_CLIENT_ID);
        }
        if (UtilTexto.esVacioONulo(clientSecret)) {
            problemas.add(FALTA_CLIENT_SECRET);
        }
        if (UtilTexto.esVacioONulo(proveedor)) {
            problemas.add(FALTA_PROVEEDOR);
        }
        if (cacheTtl.isNegative()) {
            problemas.add(TTL_NEGATIVO);
        }
        return problemas;
    }

    private void agregarProblemasUrl(List<String> problemas, String propiedad, String valor, String falta) {
        if (UtilTexto.esVacioONulo(valor)) {
            problemas.add(falta);
        } else {
            problemas.addAll(UrlProveedor.problemas(propiedad, valor, allowInsecureHttp));
        }
    }

    @Override
    public String toString() {
        return "UsuarioSesionProperties[url=" + url + ", tokenUrl=" + tokenUrl + ", clientId=" + clientId
                + ", cacheTtl=" + cacheTtl
                + ", proveedor=" + proveedor + "]";
    }
}
