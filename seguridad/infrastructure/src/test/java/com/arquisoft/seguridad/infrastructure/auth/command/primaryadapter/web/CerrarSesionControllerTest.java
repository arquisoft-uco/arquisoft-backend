package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.CerrarSesionInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CerrarSesionControllerTest {

    @Mock
    private CerrarSesionInteractor cerrarSesionInteractor;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CerrarSesionController controller;

    @Test
    void debeRetornar200YCalcularTtl_cuandoJwtNoHaExpirado() {
        // Arrange
        var jwt = jwtCon(Instant.now().plusSeconds(3600));
        ArgumentCaptor<TokenSesionCommand> captor = ArgumentCaptor.forClass(TokenSesionCommand.class);

        // Act
        var response = controller.ejecutar(jwt);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMensaje()).isNotBlank();
        verify(cerrarSesionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().identificadorToken()).isEqualTo("jti-123");
        assertThat(captor.getValue().tiempoVidaRestante()).isPositive();
    }

    @Test
    void debeEnviarTtlCero_cuandoJwtYaExpiro() {
        // Arrange
        var jwt = jwtCon(Instant.now().minusSeconds(60));
        ArgumentCaptor<TokenSesionCommand> captor = ArgumentCaptor.forClass(TokenSesionCommand.class);

        // Act
        controller.ejecutar(jwt);

        // Assert
        verify(cerrarSesionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().tiempoVidaRestante()).isZero();
    }

    private Jwt jwtCon(Instant expiracion) {
        return Jwt.withTokenValue("token-prueba")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .jti("jti-123")
                .expiresAt(expiracion)
                .issuedAt(Instant.now().minusSeconds(120))
                .build();
    }
}
