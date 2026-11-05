package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SesionPropertiesTest {

    private static SesionProperties conUrl(String url, boolean inseguro, String cookie, String authorization) {
        return new SesionProperties("sesion-pdp", true, authorization, cookie, url, inseguro, Duration.ofSeconds(1),
                Duration.ofSeconds(3), new SesionProperties.Cache(Duration.ofSeconds(60), 100));
    }

    @Test
    void debeTenerDefaultsApagados_cuandoNoSeConfiguraNada() {
        // Arrange
        var binder = new Binder(new MapConfigurationPropertySource(Map.of()));

        // Act
        var propiedades = binder.bindOrCreate("security.authentication.session", SesionProperties.class);

        // Assert
        assertThat(propiedades.enabled()).isFalse();
        assertThat(propiedades.id()).isEqualTo("sesion-pdp");
        assertThat(propiedades.authorization()).isEqualTo("pep");
        assertThat(propiedades.cookieName()).isEqualTo("SECURITY_BASELINE_SESSION");
        assertThat(propiedades.pdpUrl()).isEmpty();
        assertThat(propiedades.allowInsecureHttp()).isFalse();
        assertThat(propiedades.connectTimeout()).isEqualTo(Duration.ofSeconds(1));
        assertThat(propiedades.readTimeout()).isEqualTo(Duration.ofSeconds(3));
        assertThat(propiedades.cache().ttl()).isEqualTo(Duration.ofSeconds(60));
        assertThat(propiedades.cache().maxSize()).isEqualTo(10000);
        assertThat(propiedades.cache().habilitada()).isTrue();
    }

    @Test
    void debeReportarUrlVaciaHttpInseguraCookieVaciaOAuthorizationDistintoDePep() {
        // Act
        var sinUrl = conUrl("", false, "SECURITY_BASELINE_SESSION", "pep").problemas();
        var http = conUrl("http://pdp.test", false, "SECURITY_BASELINE_SESSION", "pep").problemas();
        var sinCookie = conUrl("https://pdp.test", false, "", "pep").problemas();
        var otroModo = conUrl("https://pdp.test", false, "SECURITY_BASELINE_SESSION", "role-table").problemas();
        var valida = conUrl("http://pdp.test", true, "SECURITY_BASELINE_SESSION", "pep").problemas();
        var ttlCero = new SesionProperties.Cache(Duration.ZERO, 100);

        // Assert
        assertThat(sinUrl).hasSize(1).first().asString().contains("pdp-url");
        assertThat(http).hasSize(1).first().asString().contains("HTTPS");
        assertThat(sinCookie).hasSize(1).first().asString().contains("cookie-name");
        assertThat(otroModo).hasSize(1).first().asString().contains("authorization");
        assertThat(valida).isEmpty();
        assertThat(ttlCero.habilitada()).isFalse();
    }
}
