package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PepDecisionAccesoOutputAdapterTest {

    private static final String URL = "https://pep.test/internal/v1/embedded-access-decisions";
    private static final String TOKEN = "token-secreto-del-usuario";

    private MockRestServiceServer server;
    private AppLogger logger;
    private RestTemplate restTemplate;
    private final SolicitudAcceso solicitud = new SolicitudAcceso("GET", "/api/fichas-perfil/{id}/items", TOKEN,
            "principal", "corr-1");

    @BeforeEach
    void preparar() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        logger = mock(AppLogger.class);
    }

    private PepDecisionAccesoOutputAdapter adaptador(int reintentos) {
        var propiedades = new PepProperties("https://pep.test/", "arquisoft", "lab", "cred-app", false,
                Duration.ofSeconds(1), Duration.ofSeconds(3), reintentos, Duration.ofMillis(1));
        return new PepDecisionAccesoOutputAdapter(restTemplate, propiedades, logger);
    }

    @Test
    void debeRetornarPermitidaConIdDecision_cuando204() {
        // Arrange
        var cabeceras = new HttpHeaders();
        cabeceras.set("X-Decision-Id", "dec-9");
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NO_CONTENT).headers(cabeceras));

        // Act
        var decision = adaptador(0).decidir(solicitud);

        // Assert
        assertThat(decision).isEqualTo(new DecisionAcceso.Permitida("dec-9"));
    }

    @Test
    void debeRetornarPermitida_cuandoCualquier2xx() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        // Act
        var decision = adaptador(0).decidir(solicitud);

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Permitida.class);
    }

    @Test
    void debeRetornarNoAutenticadaYDenegadaSinReintentar_cuando401o403() {
        // Arrange
        server.expect(ExpectedCount.once(), requestTo(URL)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        server.expect(ExpectedCount.once(), requestTo(URL)).andRespond(withStatus(HttpStatus.FORBIDDEN));
        var adaptador = adaptador(3);

        // Act
        var primera = adaptador.decidir(solicitud);
        var segunda = adaptador.decidir(solicitud);

        // Assert
        assertThat(primera).isInstanceOf(DecisionAcceso.NoAutenticada.class);
        assertThat(segunda).isInstanceOf(DecisionAcceso.Denegada.class);
        server.verify();
    }

    @Test
    void debeLanzar503_cuando500() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        var adaptador = adaptador(0);

        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
    }

    @Test
    void debeLanzar503_cuando404() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        var adaptador = adaptador(0);

        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
    }

    @Test
    void debeLanzar503_cuandoFalloDeTransporte() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(request -> {
            throw new IOException("conexion rechazada");
        });
        var adaptador = adaptador(0);

        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
    }

    @Test
    void debeReintentarYPermitir_cuandoFalloDeTransporteYHayReintentos() {
        // Arrange
        server.expect(ExpectedCount.once(), requestTo(URL)).andRespond(request -> {
            throw new IOException("timeout");
        });
        server.expect(ExpectedCount.once(), requestTo(URL)).andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        server.expect(ExpectedCount.once(), requestTo(URL)).andRespond(withStatus(HttpStatus.NO_CONTENT));

        // Act
        var decision = adaptador(2).decidir(solicitud);

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Permitida.class);
        server.verify();
    }

    @Test
    void debeLanzar503_cuandoSeAgotanLosReintentos() {
        // Arrange
        server.expect(ExpectedCount.times(3), requestTo(URL)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        var adaptador = adaptador(2);

        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        server.verify();
    }

    @Test
    void debeEnviarCuerpoYCabecerasDelContrato_cuandoDecide() {
        // Arrange
        server.expect(requestTo(URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + TOKEN))
                .andExpect(header("X-Application-Credential", "cred-app"))
                .andExpect(header("X-Correlation-Id", "corr-1"))
                .andExpect(header("X-Request-Id", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.applicationName").value("arquisoft"))
                .andExpect(jsonPath("$.environment").value("lab"))
                .andExpect(jsonPath("$.path").value("/api/fichas-perfil/{id}/items"))
                .andExpect(jsonPath("$.method").value("GET"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        // Act
        adaptador(0).decidir(solicitud);

        // Assert
        server.verify();
    }

    @Test
    void debeNoLoguearLaCredencial_cuandoElPepFalla() {
        // Arrange
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        var adaptador = adaptador(0);
        var captor = ArgumentCaptor.forClass(Object.class);

        // Act
        assertThatThrownBy(() -> adaptador.decidir(solicitud));

        // Assert
        verify(logger).error(any(ClaveMensaje.class), captor.capture(), captor.capture());
        assertThat(captor.getAllValues()).noneMatch(valor -> String.valueOf(valor).contains(TOKEN));
    }
}
