package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PdpResolucionSesionOutputAdapterTest {

    private static final String URL = "https://pdp.test/api/v1/session";
    private static final String COOKIE = "SECURITY_BASELINE_SESSION=valor-secreto-de-la-cookie";

    private MockRestServiceServer server;
    private AppLogger logger;
    private PdpResolucionSesionOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        var restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        logger = mock(AppLogger.class);
        var propiedades = new SesionProperties("sesion-pdp", true, "SECURITY_BASELINE_SESSION",
                "https://pdp.test/", false, Duration.ofSeconds(1), Duration.ofSeconds(3),
                new SesionProperties.Cache(Duration.ofSeconds(60), 100));
        adaptador = new PdpResolucionSesionOutputAdapter(restTemplate, propiedades, logger);
    }

    @Test
    void debeRetornarResuelta_cuando200ConSubject() {
        // Arrange
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Cookie", COOKIE))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess("""
                        {"data":{"subject":"sub-1","name":"Ana","email":"ana@uco.edu.co","tenantId":"t-1","extra":1}}
                        """, MediaType.APPLICATION_JSON));

        // Act
        var resolucion = adaptador.resolver(COOKIE);

        // Assert
        assertThat(resolucion).isEqualTo(new ResolucionSesion.Resuelta("sub-1", "Ana", "ana@uco.edu.co", "t-1"));
        server.verify();
    }

    @Test
    void debeRetornarSinSesion_cuando401() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // Act
        var resolucion = adaptador.resolver(COOKIE);

        // Assert
        assertThat(resolucion).isInstanceOf(ResolucionSesion.SinSesion.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 404, 500, 503})
    void debeLanzar503_cuandoRespuestaInesperada(int estado) {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.valueOf(estado)));

        // Act / Assert
        assertThatThrownBy(() -> adaptador.resolver(COOKIE))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
    }

    @Test
    void debeLanzar503_cuandoTimeoutOConexionRechazada() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(request -> {
            throw new IOException("conexion rechazada");
        });

        // Act / Assert
        assertThatThrownBy(() -> adaptador.resolver(COOKIE))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"data\":{\"name\":\"Ana\"}}", "{\"data\":{\"subject\":\" \"}}", "{}"})
    void debeLanzar503_cuando200SinSubject(String cuerpo) {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withSuccess(cuerpo, MediaType.APPLICATION_JSON));

        // Act / Assert
        assertThatThrownBy(() -> adaptador.resolver(COOKIE))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
    }

    @Test
    void debeEnviarCookieYNoLoguearla_cuandoElPdpFalla() {
        // Arrange
        server.expect(requestTo(URL)).andExpect(header("Cookie", COOKIE))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        // Act
        assertThatThrownBy(() -> adaptador.resolver(COOKIE)).isInstanceOf(ProveedorIdentidadNoDisponibleException.class);

        // Assert
        server.verify();
        var argumentosLogueados = mockingDetails(logger).getInvocations().stream()
                .flatMap(invocacion -> java.util.Arrays.stream(invocacion.getArguments()))
                .map(String::valueOf)
                .toList();
        assertThat(argumentosLogueados).isNotEmpty().noneMatch(argumento -> argumento.contains("valor-secreto"));
    }
}
