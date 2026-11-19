package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
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
        String tokenId,
        String secreto,
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
        tokenId = UtilTexto.aplicarTrim(tokenId);
        secreto = UtilTexto.aplicarTrim(secreto);
        defaults = Map.copyOf(UtilObjeto.aplicarPorDefecto(defaults, Map.of()));
    }

    public boolean configurado() {
        return !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(baseUrl))
                && !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(tokenId))
                && !UtilTexto.esVacioONulo(UtilTexto.aplicarTrim(secreto));
    }

    public List<String> problemas(boolean produccion) {
        var afectadas = new LinkedHashSet<String>();
        if (!urlValida()) {
            afectadas.add(PREFIJO + "base-url");
        }
        if (!UtilUUID.uuidValido(UtilTexto.aplicarTrim(tokenId))) {
            afectadas.add(PREFIJO + "token-id");
        }
        if (UtilTexto.esVacioONulo(secreto) || contieneCaracterDeControl(secreto)) {
            afectadas.add(PREFIJO + "secreto");
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
        var uri = analizarUrl();
        if (UtilObjeto.esNulo(uri) || UtilTexto.esVacioONulo(uri.getHost())) {
            return UtilTexto.VACIO;
        }
        var puerto = uri.getPort() < 0 ? UtilTexto.VACIO : ":" + uri.getPort();
        return uri.getScheme() + "://" + uri.getHost() + puerto;
    }

    private boolean urlValida() {
        var uri = analizarUrl();
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

    private URI analizarUrl() {
        try {
            return URI.create(UtilTexto.aplicarTrim(baseUrl));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // Un caracter de control no puede viajar en una cabecera HTTP y el JDK lo rechaza con una
    // excepcion cuyo mensaje incluye el valor de la cabecera, es decir, el secreto.
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
