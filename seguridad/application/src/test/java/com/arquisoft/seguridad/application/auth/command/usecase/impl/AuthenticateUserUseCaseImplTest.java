package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.AuthenticateUserUseCase;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImplTest {

    @Mock
    private AuthenticationOutputPort authenticationOutputPort;

    @InjectMocks
    private AuthenticateUserUseCaseImpl authenticateUserUseCase;

    @Test
    void debeRetornarCredenciales_cuandoAutenticacionEsExitosa() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand("test@example.com", "secreto");
        when(authenticationOutputPort.autenticar("test@example.com", "secreto"))
                .thenReturn(CredencialesSesion.de("access-token", "refresh-token", 300L, "Bearer", "openid"));

        // Act
        AuthenticateUserUseCase.AuthResult resultado = authenticateUserUseCase.ejecutar(command);

        // Assert
        assertThat(resultado.accessToken()).isEqualTo("access-token");
        assertThat(resultado.refreshToken()).isEqualTo("refresh-token");
        assertThat(resultado.expiresIn()).isEqualTo(300L);
        assertThat(resultado.tokenType()).isEqualTo("Bearer");
        assertThat(resultado.scope()).isEqualTo("openid");
    }

    @Test
    void debeDelegarEnElPuerto_cuandoEjecutar() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand("otro@example.com", "clave");
        when(authenticationOutputPort.autenticar("otro@example.com", "clave"))
                .thenReturn(CredencialesSesion.de("a", "r", 60L, "Bearer", ""));

        // Act
        authenticateUserUseCase.ejecutar(command);

        // Assert
        verify(authenticationOutputPort).autenticar("otro@example.com", "clave");
    }

    @Test
    void debePropagarExcepcion_cuandoCredencialesSonInvalidas() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand("test@example.com", "mala");
        when(authenticationOutputPort.autenticar("test@example.com", "mala"))
                .thenThrow(new AuthenticationException("Credenciales invalidas"));

        // Act / Assert
        assertThatThrownBy(() -> authenticateUserUseCase.ejecutar(command))
                .isInstanceOf(AuthenticationException.class);
    }
}
