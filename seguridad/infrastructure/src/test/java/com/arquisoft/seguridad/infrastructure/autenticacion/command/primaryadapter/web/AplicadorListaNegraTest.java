package com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.TokenInvalidadoOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.TokenInvalidadoKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AplicadorListaNegraTest {

    private static final String JTI = "jti-123";

    @Mock
    private TokenInvalidadoOutputPort tokenInvalidadoPort;

    @Mock
    private AppLogger logger;

    private AplicadorListaNegra aplicador;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        aplicador = aplicadorConListaNegra(true);
        request = new MockHttpServletRequest("GET", "/api/fichas-perfil");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private AplicadorListaNegra aplicadorConListaNegra(boolean blacklist) {
        var emisor = new AutenticacionProperties.Emisor("principal", true, "https://idp.test", "", List.of(),
                ModoAutorizacion.PEP, blacklist, new ClaimsIdentidad("sub", "email", "preferred_username", "name", ""));
        return new AplicadorListaNegra(logger, new AutenticacionProperties(List.of(emisor)), tokenInvalidadoPort,
                JsonMapper.builder().build());
    }

    private static BearerAuthenticationToken identidad(String jti) {
        return new BearerAuthenticationToken("principal", "sub-1", null, null, List.of(), jti,
                Instant.now().plusSeconds(60));
    }

    @Test
    void debeResponder401_cuandoElTokenFueRevocado() {
        // Arrange
        var identidad = identidad(JTI);
        SecurityContextHolder.getContext().setAuthentication(identidad);
        when(tokenInvalidadoPort.estaInvalidado(JTI)).thenReturn(true);

        // Act
        var continua = aplicador.aplicar(request, response, identidad);

        // Assert
        assertThat(continua).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(logger).warn(TokenInvalidadoKey.LOG_TOKEN_REVOCADO, JTI, "/api/fichas-perfil");
    }

    @Test
    void debeContinuar_cuandoNoEstaRevocado() {
        // Arrange
        when(tokenInvalidadoPort.estaInvalidado(JTI)).thenReturn(false);

        // Act
        var continua = aplicador.aplicar(request, response, identidad(JTI));

        // Assert
        assertThat(continua).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void debeOmitirLaConsulta_cuandoElEmisorNoUsaListaNegra() {
        // Arrange
        aplicador = aplicadorConListaNegra(false);

        // Act
        var continua = aplicador.aplicar(request, response, identidad(JTI));

        // Assert
        assertThat(continua).isTrue();
        verify(tokenInvalidadoPort, never()).estaInvalidado(anyString());
    }

    @Test
    void debeOmitirLaConsulta_cuandoNoHayIdToken() {
        // Arrange
        var sesion = new SesionAuthenticationToken("sesion-pdp", "sub-1", "Ana", "ana@uco.edu.co", "t-1");

        // Act
        var continuaBearer = aplicador.aplicar(request, response, identidad(null));
        var continuaSesion = aplicador.aplicar(request, response, sesion);

        // Assert
        assertThat(continuaBearer).isTrue();
        assertThat(continuaSesion).isTrue();
        verify(tokenInvalidadoPort, never()).estaInvalidado(anyString());
    }

    @Test
    void debeContinuar_cuandoRedisNoDisponible() {
        // Arrange
        var fallo = new IllegalStateException("redis caido");
        when(tokenInvalidadoPort.estaInvalidado(JTI)).thenThrow(fallo);

        // Act
        var continua = aplicador.aplicar(request, response, identidad(JTI));

        // Assert
        assertThat(continua).isTrue();
        verify(logger).error(TokenInvalidadoKey.LOG_REDIS_NO_DISPONIBLE, fallo, "redis caido");
    }
}
