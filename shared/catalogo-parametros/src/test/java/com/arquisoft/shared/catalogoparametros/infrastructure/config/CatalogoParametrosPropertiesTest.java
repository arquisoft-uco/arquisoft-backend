package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoParametrosPropertiesTest {

    private static final String TOKEN_URI = "https://keycloak.test/realms/r/protocol/openid-connect/token";
    private static final String CLIENT_ID = "svc-arquisoft-catalogo-dev";
    private static final String SECRETO = "secreto-de-prueba-123";

    private static CatalogoParametrosProperties validas(String url, boolean inseguro) {
        return new CatalogoParametrosProperties(true, url, TOKEN_URI, CLIENT_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), inseguro, Duration.ofMinutes(5), Map.of());
    }

    @Test
    void debeNoReportarProblemas_cuandoConfiguracionValida() {
        // Arrange
        var propiedades = validas("https://catalogo.test/api/v1", false);

        // Act
        var problemas = propiedades.problemas(true);

        // Assert
        assertThat(problemas).isEmpty();
        assertThat(propiedades.configurado()).isTrue();
    }

    @Test
    void debeReportarCredencialesYUrls_cuandoFaltan() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "", null, " ", null,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), null);

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.base-url", "catalogo.parametros.token-uri",
                "catalogo.parametros.client-id", "catalogo.parametros.client-secret");
        assertThat(propiedades.configurado()).isFalse();
    }

    @Test
    void debeReportarUrl_cuandoEsHttpSinPermiso() {
        // Arrange
        var propiedades = validas("http://catalogo.test", false);

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.base-url");
    }

    @Test
    void debeReportarTokenUri_cuandoEsHttpSinPermiso() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", "http://keycloak.test/token",
                CLIENT_ID, SECRETO, Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5),
                Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.token-uri");
    }

    @Test
    void debeAceptarHttp_cuandoSePermiteFueraDeProduccion() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "http://localhost:8083/api",
                "http://localhost:19090/realms/r/protocol/openid-connect/token", CLIENT_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), true, Duration.ofMinutes(5), Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).isEmpty();
    }

    @Test
    void debeSepararOrigenYRuta_delTokenUri() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test",
                " http://localhost:19090/realms/r/protocol/openid-connect/token ", CLIENT_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), true, Duration.ofMinutes(5), Map.of());

        // Act
        var origen = propiedades.origenTokenUri();
        var ruta = propiedades.rutaTokenUri();

        // Assert
        assertThat(origen).isEqualTo("http://localhost:19090");
        assertThat(ruta).isEqualTo("/realms/r/protocol/openid-connect/token");
    }

    @Test
    void debeDevolverVacio_cuandoElTokenUriNoEsUnaUrl() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", "ht tp://%%", CLIENT_ID,
                SECRETO, Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());

        // Act
        var origen = propiedades.origenTokenUri();
        var ruta = propiedades.rutaTokenUri();

        // Assert
        assertThat(origen).isEmpty();
        assertThat(ruta).isEmpty();
    }

    @Test
    void debeRecortarCredenciales_cuandoTraenSaltoDeLineaFinal() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, " https://catalogo.test ", TOKEN_URI + "\n",
                CLIENT_ID + "\r\n", SECRETO + "\n", Duration.ofSeconds(1), Duration.ofSeconds(3), false,
                Duration.ofMinutes(5), Map.of());

        // Act
        var problemas = propiedades.problemas(true);

        // Assert
        assertThat(problemas).isEmpty();
        assertThat(propiedades.clientSecret()).isEqualTo(SECRETO);
        assertThat(propiedades.clientId()).isEqualTo(CLIENT_ID);
        assertThat(propiedades.tokenUri()).isEqualTo(TOKEN_URI);
        assertThat(propiedades.baseUrl()).isEqualTo("https://catalogo.test");
    }

    @Test
    void debeReportarSecreto_cuandoContieneCaracterDeControl() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", TOKEN_URI, CLIENT_ID,
                "secreto\ninterno", Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5),
                Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.client-secret");
        assertThat(String.join("|", problemas)).doesNotContain("interno");
    }

    @Test
    void debeReportarClientId_cuandoContieneCaracterDeControl() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", TOKEN_URI, "svc\tarquisoft",
                SECRETO, Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.client-id");
    }

    @Test
    void debeOcultarCredenciales_enToString() {
        // Arrange
        var propiedades = validas("https://catalogo.test", false);

        // Act
        var texto = propiedades.toString();

        // Assert
        assertThat(texto).doesNotContain(SECRETO).doesNotContain(CLIENT_ID).doesNotContain(TOKEN_URI);
    }
}
