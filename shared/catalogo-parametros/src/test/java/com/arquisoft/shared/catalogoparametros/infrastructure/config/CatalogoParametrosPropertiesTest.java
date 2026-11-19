package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogoParametrosPropertiesTest {

    private static final String TOKEN_ID = "0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11";
    private static final String SECRETO = "secreto-de-prueba-123";

    private static CatalogoParametrosProperties validas(String url, boolean inseguro) {
        return new CatalogoParametrosProperties(true, url, TOKEN_ID, SECRETO,
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
    }

    @Test
    void debeReportarSecretoYUrl_cuandoFaltan() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "", TOKEN_ID, null,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), null);

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.base-url", "catalogo.parametros.secreto");
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
    void debeReportarTokenId_cuandoNoEsUuid() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", "no-es-uuid", SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.token-id");
    }

    @Test
    void debeRecortarSecretoYTokenId_cuandoTraenSaltoDeLineaFinal() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, " https://catalogo.test ", TOKEN_ID + "\r\n",
                SECRETO + "\n", Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());

        // Act
        var problemas = propiedades.problemas(true);

        // Assert
        assertThat(problemas).isEmpty();
        assertThat(propiedades.secreto()).isEqualTo(SECRETO);
        assertThat(propiedades.tokenId()).isEqualTo(TOKEN_ID);
        assertThat(propiedades.baseUrl()).isEqualTo("https://catalogo.test");
    }

    @Test
    void debeReportarSecreto_cuandoContieneCaracterDeControl() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, "https://catalogo.test", TOKEN_ID,
                "secreto\ninterno", Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5),
                Map.of());

        // Act
        var problemas = propiedades.problemas(false);

        // Assert
        assertThat(problemas).containsExactly("catalogo.parametros.secreto");
        assertThat(String.join("|", problemas)).doesNotContain("interno");
    }

    @Test
    void debeOcultarSecretoYTokenId_enToString() {
        // Arrange
        var propiedades = validas("https://catalogo.test", false);

        // Act
        var texto = propiedades.toString();

        // Assert
        assertThat(texto).doesNotContain(SECRETO).doesNotContain(TOKEN_ID);
    }
}
