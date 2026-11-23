package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ConfiguracionCatalogoParametrosInvalidaException;
import com.arquisoft.shared.message.constant.AppCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorConfiguracionCatalogoParametrosTest {

    private static final String TOKEN_ID = "svc-arquisoft-catalogo-dev";
    private static final String TOKEN_URI = "https://keycloak.test/realms/r/protocol/openid-connect/token";
    private static final String SECRETO = "secreto-de-prueba-123";
    private static final String URL = "https://catalogo.test/api/v1";

    private static CatalogoParametrosProperties propiedades(String url, String clientId, String secreto,
                                                            boolean inseguro, Duration intervalo) {
        return new CatalogoParametrosProperties(true, url, TOKEN_URI, clientId, secreto, Duration.ofSeconds(1),
                Duration.ofSeconds(3), inseguro, intervalo, Map.of());
    }

    private static ValidadorConfiguracionCatalogoParametros validador(CatalogoParametrosProperties propiedades,
                                                                      String perfil) {
        var entorno = new MockEnvironment();
        entorno.setActiveProfiles(perfil);
        return new ValidadorConfiguracionCatalogoParametros(propiedades, entorno);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dev", "prod"})
    void debeAbortar_sinSecreto(String perfil) {
        // Arrange
        var validador = validador(propiedades(URL, TOKEN_ID, " ", false, Duration.ofMinutes(5)), perfil);

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class, e -> {
                    assertThat(e.getCodigoError()).isEqualTo(AppCodes.CatalogoParametros.CONFIGURACION_INVALIDA);
                    assertThat(e.getMessage()).contains("catalogo.parametros.client-secret");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"dev", "prod"})
    void debeAbortar_sinUrlNiClientId(String perfil) {
        // Arrange
        var validador = validador(propiedades(null, "", SECRETO, false, Duration.ofMinutes(5)), perfil);

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.base-url")
                                .contains("catalogo.parametros.client-id"));
    }

    @Test
    void debeAbortar_enProdConHttpInseguro() {
        // Arrange
        var validador = validador(
                propiedades("http://catalogo.test", TOKEN_ID, SECRETO, true, Duration.ofMinutes(5)), "prod");

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.allow-insecure-http"));
    }

    @Test
    void debePermitirHttpInseguro_fueraDeProd() {
        // Arrange
        var validador = validador(
                propiedades("http://localhost:8082/api", TOKEN_ID, SECRETO, true, Duration.ofMinutes(5)), "dev");

        // Act / Assert
        assertThatCode(validador::validar).doesNotThrowAnyException();
    }

    @Test
    void debeAbortar_cuandoUrlInvalidaOSinHttps() {
        // Arrange
        var sinHttps = validador(propiedades("http://catalogo.test", TOKEN_ID, SECRETO, false, Duration.ofMinutes(5)),
                "dev");
        var invalida = validador(propiedades("ht tp://%%", TOKEN_ID, SECRETO, false, Duration.ofMinutes(5)), "dev");

        // Act / Assert
        assertThatThrownBy(sinHttps::validar).isInstanceOf(ConfiguracionCatalogoParametrosInvalidaException.class);
        assertThatThrownBy(invalida::validar).isInstanceOf(ConfiguracionCatalogoParametrosInvalidaException.class);
    }

    @Test
    void debeAbortar_cuandoTokenUriNoEsUnaUrl() {
        // Arrange
        var propiedades = new CatalogoParametrosProperties(true, URL, "keycloak.test/token", TOKEN_ID, SECRETO,
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), Map.of());
        var validador = validador(propiedades, "dev");

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.token-uri"));
    }

    @Test
    void debeAbortar_cuandoIntervaloNoPositivo() {
        // Arrange
        var cero = validador(propiedades(URL, TOKEN_ID, SECRETO, false, Duration.ZERO), "dev");
        var negativo = validador(propiedades(URL, TOKEN_ID, SECRETO, false, Duration.ofSeconds(-1)), "dev");

        // Act / Assert
        assertThatThrownBy(cero::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.refresh-interval"));
        assertThatThrownBy(negativo::validar).isInstanceOf(ConfiguracionCatalogoParametrosInvalidaException.class);
    }

    @Test
    void debeAceptarConfiguracionCompleta_enProd() {
        // Arrange
        var validador = validador(propiedades(URL, TOKEN_ID, SECRETO, false, Duration.ofMinutes(5)), "prod");

        // Act / Assert
        assertThatCode(validador::validar).doesNotThrowAnyException();
    }

    @Test
    void debeNoIncluirValores_enElMensajeDeError() {
        // Arrange
        var urlConCredenciales = "http://usuario:clave-url@catalogo.test/api?q=consulta-secreta";
        var tokenInvalido = "svc\tinvalido-xyz";
        var validador = validador(propiedades(urlConCredenciales, tokenInvalido, SECRETO, true, Duration.ZERO),
                "prod");

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class, e -> {
                    for (var texto : new String[] {e.getMessage(), e.getError().getMessage()}) {
                        assertThat(texto).doesNotContain(SECRETO).doesNotContain(tokenInvalido)
                                .doesNotContain("clave-url").doesNotContain("consulta-secreta")
                                .doesNotContain("catalogo.test");
                    }
                });
    }
}
