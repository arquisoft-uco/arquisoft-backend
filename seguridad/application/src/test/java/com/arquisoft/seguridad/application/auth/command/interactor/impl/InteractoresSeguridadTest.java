package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.AutenticarUsuarioUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.CerrarSesionUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
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
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Mock
    private RefrescarTokenUseCase refrescarTokenUseCase;

    @Mock
    private CerrarSesionUseCase cerrarSesionUseCase;

    @Mock
    private ValidarTokenUseCase validarTokenUseCase;

    @DisplayName("El interactor de seguridad no declara transacción: el contexto no tiene DataSource")
    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {
            AutenticarUsuarioInteractorImpl.class,
            RefrescarTokenInteractorImpl.class,
            CerrarSesionInteractorImpl.class,
            ValidarTokenInteractorImpl.class
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
        AutenticarUsuarioCommand command = new AutenticarUsuarioCommand("test@example.com", "secreto");
        AutenticacionResult esperado =
                new AutenticacionResult("access", "refresh", 300L, "Bearer", "openid");
        when(autenticarUsuarioUseCase.ejecutar(command)).thenReturn(esperado);

        // Act
        AutenticacionResult resultado =
                new AutenticarUsuarioInteractorImpl(autenticarUsuarioUseCase).ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoRefrescar() {
        // Arrange
        RefrescoTokenResult esperado =
                new RefrescoTokenResult("access", "refresh", 300L, "Bearer", "openid");
        when(refrescarTokenUseCase.ejecutar("token-refresco")).thenReturn(esperado);

        // Act
        RefrescoTokenResult resultado =
                new RefrescarTokenInteractorImpl(refrescarTokenUseCase).ejecutar("token-refresco");

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoCerrarSesion() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);

        // Act
        new CerrarSesionInteractorImpl(cerrarSesionUseCase).ejecutar(command);

        // Assert
        verify(cerrarSesionUseCase).ejecutar(command);
    }

    @Test
    void debeDelegarEnElUseCase_cuandoValidarToken() {
        // Arrange
        TokenDomain token = TokenDomain.de("eyJhbGc...");
        ValidacionTokenResult esperado =
                new ValidacionTokenResult(true, "id-1", "test@example.com", "Token valido");
        when(validarTokenUseCase.ejecutar(token)).thenReturn(esperado);

        // Act
        ValidacionTokenResult resultado =
                new ValidarTokenInteractorImpl(validarTokenUseCase).ejecutar(token);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }
}
