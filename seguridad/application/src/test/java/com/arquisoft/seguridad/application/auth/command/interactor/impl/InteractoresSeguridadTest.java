package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.AuthenticateUserUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.LogoutUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.RefreshTokenUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidateTokenUseCase;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractoresSeguridadTest {

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private RefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private LogoutUseCase logoutUseCase;

    @Mock
    private ValidateTokenUseCase validateTokenUseCase;

    @DisplayName("El interactor de seguridad no declara transacción: el contexto no tiene DataSource")
    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {
            AuthenticateUserInteractorImpl.class,
            RefreshTokenInteractorImpl.class,
            LogoutInteractorImpl.class,
            ValidateTokenInteractorImpl.class
    })
    void debeNoDeclararTransaccion_cuandoEsInteractorDeSeguridad(Class<?> interactor) {
        // Arrange
        Method ejecutar = Arrays.stream(interactor.getDeclaredMethods())
                .filter(m -> "ejecutar".equals(m.getName()) && !m.isBridge())
                .findFirst()
                .orElseThrow();

        // Act
        Transactional transaccion = ejecutar.getAnnotation(Transactional.class);

        // Assert — seguridad se apoya en Keycloak y Redis, no hay unidad de trabajo JPA
        assertThat(transaccion)
                .as("%s no debe delimitar transacción", interactor.getSimpleName())
                .isNull();
    }

    @Test
    void debeDelegarEnElUseCase_cuandoAutenticar() {
        // Arrange
        AuthenticateUserCommand command = new AuthenticateUserCommand("test@example.com", "secreto");
        AuthenticateUserUseCase.AuthResult esperado =
                new AuthenticateUserUseCase.AuthResult("access", "refresh", 300L, "Bearer", "openid");
        when(authenticateUserUseCase.ejecutar(command)).thenReturn(esperado);

        // Act
        AuthenticateUserUseCase.AuthResult resultado =
                new AuthenticateUserInteractorImpl(authenticateUserUseCase).ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoRefrescar() {
        // Arrange
        RefreshTokenUseCase.RefreshResult esperado =
                new RefreshTokenUseCase.RefreshResult("access", "refresh", 300L, "Bearer", "openid");
        when(refreshTokenUseCase.ejecutar("token-refresco")).thenReturn(esperado);

        // Act
        RefreshTokenUseCase.RefreshResult resultado =
                new RefreshTokenInteractorImpl(refreshTokenUseCase).ejecutar("token-refresco");

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoCerrarSesion() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);

        // Act
        new LogoutInteractorImpl(logoutUseCase).ejecutar(command);

        // Assert
        verify(logoutUseCase).ejecutar(command);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoValidarToken() {
        // Arrange
        TokenAggregate token = TokenAggregate.de("eyJhbGc...");
        ValidateTokenUseCase.ValidationResult esperado =
                new ValidateTokenUseCase.ValidationResult(true, "id-1", "test@example.com", "Token valido");
        when(validateTokenUseCase.ejecutar(token)).thenReturn(esperado);

        // Act
        ValidateTokenUseCase.ValidationResult resultado =
                new ValidateTokenInteractorImpl(validateTokenUseCase).ejecutar(token);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }
}
