package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoParametrosPropertiesProblemasTest {

    private static final String TOKEN_ID = "0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11";
    private static final String SECRETO = "secreto-de-prueba-123";

    private static CatalogoParametrosProperties con(String url, Duration connect, Duration read, Duration intervalo) {
        return new CatalogoParametrosProperties(true, url, TOKEN_ID, SECRETO, connect, read, false, intervalo,
                Map.of());
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://usuario:clave@catalogo.test", "https://catalogo.test/api?x=1",
            "https://catalogo.test/api#frag", "https:///sin-host", "catalogo.test/api"})
    void debeReportarUrl_cuandoTraeUserinfoQueryFragmentoOCareceDeHost(String url) {
        // Arrange
        var propiedades = con(url, Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMinutes(5));

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.base-url");
    }

    @Test
    void debeReportarTimeoutsEIntervalo_cuandoNoSonPositivos() {
        // Arrange
        var propiedades = con("https://catalogo.test", Duration.ZERO, Duration.ofSeconds(-1), null);

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.connect-timeout",
                "catalogo.parametros.read-timeout", "catalogo.parametros.refresh-interval");
    }

    @Test
    void debeAcumularTodosLosProblemas_enUnaSolaLista() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, null, null, null, Duration.ZERO, Duration.ZERO, true,
                Duration.ZERO, null);

        // Act
        var problemas = propiedades.problemas(true);

        // Assert
        assertThat(problemas).hasSize(7).allMatch(p -> p.startsWith("catalogo.parametros."));
    }

    @Test
    void debeEstarConfigurado_soloConUrlTokenIdYSecreto() {
        // Arrange
        var completa = con("https://catalogo.test", Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMinutes(5));
        var sinToken = new CatalogoParametrosProperties(true, "https://catalogo.test", " ", SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), null);

        // Act / Assert
        assertThat(completa.configurado()).isTrue();
        assertThat(sinToken.configurado()).isFalse();
    }

    @Test
    void debeExponerSoloElOrigen_sinRutaNiCredenciales() {
        // Arrange
        var conPuerto = con("https://catalogo.test:8443/api/v1", Duration.ofSeconds(1), Duration.ofSeconds(3),
                Duration.ofMinutes(5));
        var sinPuerto = con("https://catalogo.test/api/v1", Duration.ofSeconds(1), Duration.ofSeconds(3),
                Duration.ofMinutes(5));
        var invalida = con("ht tp://%%", Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofMinutes(5));

        // Act / Assert
        assertThat(conPuerto.origenBaseUrl()).isEqualTo("https://catalogo.test:8443");
        assertThat(sinPuerto.origenBaseUrl()).isEqualTo("https://catalogo.test");
        assertThat(invalida.origenBaseUrl()).isEmpty();
    }

    @Test
    void debeCopiarLosDefaults_yAceptarNulo() {
        // Arrange
        var origen = new HashMap<String, String>();
        origen.put("PARAM_X", "1");

        // Act
        var propiedades = new CatalogoParametrosProperties(false, null, null, null, Duration.ofSeconds(1),
                Duration.ofSeconds(3), false, Duration.ofMinutes(5), origen);
        origen.put("PARAM_Y", "2");
        var sinDefaults = new CatalogoParametrosProperties(false, null, null, null, Duration.ofSeconds(1),
                Duration.ofSeconds(3), false, Duration.ofMinutes(5), null);

        // Assert
        assertThat(propiedades.defaults()).containsExactly(Map.entry("PARAM_X", "1"));
        assertThat(sinDefaults.defaults()).isEmpty();
    }
}
