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

    private static final String TOKEN_ID = "0b3f5c1e-5d1a-4c0a-9a55-2f7a3f2d9b11";
    private static final String SECRETO = "secreto-de-prueba-123";
    private static final String URL = "https://catalogo.test/api/v1";

    private static CatalogoParametrosProperties propiedades(String url, String tokenId, String secreto,
                                                            boolean inseguro, Duration intervalo) {
        return new CatalogoParametrosProperties(true, url, tokenId, secreto, Duration.ofSeconds(1),
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
                    assertThat(e.getMessage()).contains("catalogo.parametros.secreto");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"dev", "prod"})
    void debeAbortar_sinUrlNiTokenId(String perfil) {
        // Arrange
        var validador = validador(propiedades(null, "", SECRETO, false, Duration.ofMinutes(5)), perfil);

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.base-url")
                                .contains("catalogo.parametros.token-id"));
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
    void debeAbortar_cuandoTokenIdNoEsUuid() {
        // Arrange
        var validador = validador(propiedades(URL, "no-es-uuid", SECRETO, false, Duration.ofMinutes(5)), "dev");

        // Act / Assert
        assertThatThrownBy(validador::validar)
                .isInstanceOfSatisfying(ConfiguracionCatalogoParametrosInvalidaException.class,
                        e -> assertThat(e.getMessage()).contains("catalogo.parametros.token-id"));
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
        var tokenInvalido = "token-invalido-xyz";
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
