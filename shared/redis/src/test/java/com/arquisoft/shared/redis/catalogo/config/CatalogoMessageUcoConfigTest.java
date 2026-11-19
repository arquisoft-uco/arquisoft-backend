package com.arquisoft.shared.redis.catalogo.config;

import co.edu.uco.messageucolab.client.api.MessageReader;
import co.edu.uco.messageucolab.client.autoconfigure.MessageUcoLabProperties;
import co.edu.uco.seguridad.pep.starter.m2m.ServiceIdentityProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.messageuco.MessageUcoProperties;
import com.arquisoft.shared.redis.catalogo.messageuco.ProcesadorAvisosMessageUco;
import com.arquisoft.shared.redis.catalogo.messageuco.SincronizadorCatalogoMessageUco;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.unit.DataSize;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Lo que se exige al elegir la fuente messageuco.
 *
 * <p>Ninguno de los dos starters valida al arrancar: sin esto, una identidad mal configurada solo
 * se notaría como un aviso en cada sincronización, mientras el catálogo sigue con textos viejos.
 */
@ExtendWith(MockitoExtension.class)
class CatalogoMessageUcoConfigTest {

    private static final String URL = "http://localhost:8000";
    private static final URI TOKEN_URI =
            URI.create("http://keycloak.localhost:19090/realms/security-baseline/protocol/openid-connect/token");
    private static final String CLIENT_ID = "svc-prueba-arquisoft-dev";
    private static final String CLIENT_SECRET = "secreto-de-prueba";

    @Mock
    private MessageReader lector;

    @Mock
    private StringRedisTemplate plantilla;

    @Mock
    private AppLogger logger;

    @Mock
    private ObjectProvider<ServiceIdentityProperties> identidad;

    @Mock
    private ProcesadorAvisosMessageUco procesador;

    @Mock
    private SincronizadorCatalogoMessageUco sincronizador;

    @Mock
    private GestorTraza gestorTraza;

    private final CatalogoMessageUcoConfig config = new CatalogoMessageUcoConfig();

    @Test
    @DisplayName("crea el sincronizador cuando la URL y la identidad están completas")
    void debeCrearSincronizador_cuandoConfiguracionCompleta() {
        // Arrange
        when(identidad.getIfAvailable()).thenReturn(identidad(CLIENT_SECRET));

        // Act
        var creado = config.sincronizadorCatalogoMessageUco(lector, conexion(URL), identidad, plantilla, logger);

        // Assert
        assertThat(creado).isNotNull();
    }

    @Test
    @DisplayName("aborta nombrando messageucolab.url cuando falta la URL")
    void debeAbortar_cuandoFaltaUrl() {
        // Act & Assert
        assertThatThrownBy(() -> config.sincronizadorCatalogoMessageUco(
                lector, conexion(" "), identidad, plantilla, logger))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("messageucolab.url");
    }

    @Test
    @DisplayName("aborta nombrando la identidad cuando su bean no existe (enabled=false)")
    void debeAbortar_cuandoIdentidadDeshabilitada() {
        // Arrange
        when(identidad.getIfAvailable()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> config.sincronizadorCatalogoMessageUco(
                lector, conexion(URL), identidad, plantilla, logger))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("security.pep.service-identity");
    }

    @Test
    @DisplayName("aborta nombrando la identidad cuando le falta el secreto")
    void debeAbortar_cuandoIdentidadIncompleta() {
        // Arrange
        when(identidad.getIfAvailable()).thenReturn(identidad(""));

        // Act & Assert
        assertThatThrownBy(() -> config.sincronizadorCatalogoMessageUco(
                lector, conexion(URL), identidad, plantilla, logger))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("client-secret");
    }

    @Test
    @DisplayName("aborta nombrando la propiedad de Pulsar cuando falta su dirección")
    void debeAbortar_cuandoFaltaPulsar() {
        // Arrange
        var sinPulsar = new MessageUcoProperties(DataSize.ofMegabytes(16),
                new MessageUcoProperties.Eventos(" ", "persistent://public/default/message-events",
                        "arquisoft-catalogo", Duration.ofSeconds(30), Duration.ofMinutes(1), Duration.ofSeconds(2), 20));

        // Act & Assert
        assertThatThrownBy(() -> config.escuchaAvisosMessageUco(
                procesador, sincronizador, sinPulsar, identidad, gestorTraza, logger))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("arquisoft.catalogo.messageuco.eventos.url");
    }

    @Test
    @DisplayName("arma la escucha con el client-id de la identidad cuando todo está configurado")
    void debeCrearEscucha_cuandoConfiguracionCompleta() {
        // Arrange
        when(identidad.getIfAvailable()).thenReturn(identidad(CLIENT_SECRET));
        var propiedades = new MessageUcoProperties(DataSize.ofMegabytes(16),
                new MessageUcoProperties.Eventos("pulsar://localhost:6800", "persistent://public/default/message-events",
                        "arquisoft-catalogo", Duration.ofSeconds(30), Duration.ofMinutes(1), Duration.ofSeconds(2), 20));

        // Act
        var escucha = config.escuchaAvisosMessageUco(
                procesador, sincronizador, propiedades, identidad, gestorTraza, logger);

        // Assert
        assertThat(escucha.isRunning()).isFalse();
    }

    private static MessageUcoLabProperties conexion(String url) {
        return new MessageUcoLabProperties(url, Duration.ofSeconds(30));
    }

    private static ServiceIdentityProperties identidad(String secreto) {
        return new ServiceIdentityProperties(TOKEN_URI, CLIENT_ID, secreto, null, null);
    }
}
