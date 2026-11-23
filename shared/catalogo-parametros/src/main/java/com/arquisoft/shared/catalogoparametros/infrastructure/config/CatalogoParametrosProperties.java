package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("catalogo.parametros")
public record CatalogoParametrosProperties(
        boolean enabled,
        String baseUrl,
        String tokenUri,
        String clientId,
        String clientSecret,
        @DefaultValue("1s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout,
        boolean allowInsecureHttp,
        @DefaultValue("PT5M") Duration refreshInterval,
        Map<String, String> defaults) {

    private static final String PREFIJO = "catalogo.parametros.";
    private static final String HTTPS = "https";
    private static final String HTTP = "http";

    public CatalogoParametrosProperties {
        baseUrl = UtilTexto.aplicarTrim(baseUrl);
        tokenUri = UtilTexto.aplicarTrim(tokenUri);
        clientId = UtilTexto.aplicarTrim(clientId);
        clientSecret = UtilTexto.aplicarTrim(clientSecret);
        defaults = Map.copyOf(UtilObjeto.aplicarPorDefecto(defaults, Map.of()));
    }

    public boolean configurado() {
        return !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(baseUrl))
                && !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(tokenUri))
                && !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(clientId))
                && !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(clientSecret));
    }

    public List<String> problemas(boolean produccion) {
        var afectadas = new LinkedHashSet<String>();
        if (!urlValida(baseUrl)) {
            afectadas.add(PREFIJO + "base-url");
        }
        if (!urlValida(tokenUri)) {
            afectadas.add(PREFIJO + "token-uri");
        }
        if (UtilTexto.esVacioONulo(clientId) || contieneCaracterDeControl(clientId)) {
            afectadas.add(PREFIJO + "client-id");
        }
        if (UtilTexto.esVacioONulo(clientSecret) || contieneCaracterDeControl(clientSecret)) {
            afectadas.add(PREFIJO + "client-secret");
        }
        if (noEsPositiva(connectTimeout)) {
            afectadas.add(PREFIJO + "connect-timeout");
        }
        if (noEsPositiva(readTimeout)) {
            afectadas.add(PREFIJO + "read-timeout");
        }
        if (noEsPositiva(refreshInterval)) {
            afectadas.add(PREFIJO + "refresh-interval");
        }
        if (produccion && allowInsecureHttp) {
            afectadas.add(PREFIJO + "allow-insecure-http");
        }
        return List.copyOf(afectadas);
    }

    public String origenBaseUrl() {
        var uri = analizarUrl(baseUrl);
        if (UtilObjeto.esNulo(uri) || UtilTexto.esVacioONulo(uri.getHost())) {
            return UtilTexto.VACIO;
        }
        var puerto = uri.getPort() < 0 ? UtilTexto.VACIO : ":" + uri.getPort();
        return uri.getScheme() + "://" + uri.getHost() + puerto;
    }

    public String origenTokenUri() {
        var uri = analizarUrl(tokenUri);
        if (UtilObjeto.esNulo(uri) || UtilObjeto.esNulo(uri.getScheme()) || UtilTexto.esVacioONulo(uri.getRawAuthority())) {
            return UtilTexto.VACIO;
        }
        return uri.getScheme() + "://" + uri.getRawAuthority();
    }

    public String rutaTokenUri() {
        var uri = analizarUrl(tokenUri);
        if (UtilObjeto.esNulo(uri) || UtilObjeto.esNulo(uri.getRawPath())) {
            return UtilTexto.VACIO;
        }
        return uri.getRawPath();
    }

    private boolean urlValida(String url) {
        var uri = analizarUrl(url);
        if (UtilObjeto.esNulo(uri) || UtilObjeto.esNulo(uri.getScheme()) || UtilTexto.esVacioONulo(uri.getHost())) {
            return false;
        }
        var esquemaPermitido = HTTPS.equalsIgnoreCase(uri.getScheme())
                || HTTP.equalsIgnoreCase(uri.getScheme()) && allowInsecureHttp;
        return esquemaPermitido
                && UtilObjeto.esNulo(uri.getRawUserInfo())
                && UtilObjeto.esNulo(uri.getRawQuery())
                && UtilObjeto.esNulo(uri.getRawFragment());
    }

    private static URI analizarUrl(String url) {
        try {
            return URI.create(UtilTexto.aplicarTrim(url));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Un caracter de control no puede viajar en una cabecera HTTP ni en el formulario del token, y el
    // JDK lo rechaza con una excepcion cuyo mensaje incluye el valor, es decir, el secreto.
    private static boolean contieneCaracterDeControl(String texto) {
        return texto.chars().anyMatch(Character::isISOControl);
    }

    private static boolean noEsPositiva(Duration duracion) {
        return UtilObjeto.esNulo(duracion) || duracion.isZero() || duracion.isNegative();
    }

    @Override
    public String toString() {
        return "CatalogoParametrosProperties[enabled=" + enabled + ", connectTimeout=" + connectTimeout
                + ", readTimeout=" + readTimeout + ", allowInsecureHttp=" + allowInsecureHttp
                + ", refreshInterval=" + refreshInterval + ", defaults=" + defaults.size() + "]";
    }
}
