package com.arquisoft.seguridad.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UrlProveedorTest {

    private static final String PROPIEDAD = "security.x.url";

    @Test
    void debeAceptar_cuandoEsHttpsOHttpPermitido() {
        // Act
        var https = UrlProveedor.problemas(PROPIEDAD, "https://pdp.test:8443/base", false);
        var httpPermitido = UrlProveedor.problemas(PROPIEDAD, "http://localhost:8080", true);

        // Assert
        assertThat(https).isEmpty();
        assertThat(httpPermitido).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://pdp.test", "ftp://pdp.test", "pdp.test"})
    void debeRechazar_cuandoNoEsHttpsYHttpNoEstaPermitido(String url) {
        // Act
        var problemas = UrlProveedor.problemas(PROPIEDAD, url, false);

        // Assert
        assertThat(problemas).singleElement().asString().contains(PROPIEDAD).contains("HTTPS");
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://usuario:clave@pdp.test", "https://pdp.test?a=1", "https://pdp.test#frag"})
    void debeRechazar_cuandoTraeUserinfoQueryOFragmento(String url) {
        // Act
        var problemas = UrlProveedor.problemas(PROPIEDAD, url, false);

        // Assert
        assertThat(problemas).singleElement().asString().contains(PROPIEDAD).contains("userinfo");
    }

    @Test
    void debeRechazarSinLanzar_cuandoLaUrlNoEsUnaUri() {
        // Act
        var problemas = UrlProveedor.problemas(PROPIEDAD, "https://pdp test", false);

        // Assert
        assertThat(problemas).singleElement().asString().contains(PROPIEDAD).contains("URI");
    }
}
