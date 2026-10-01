package com.arquisoft.seguridad.application.auth.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.AutenticarUsuarioUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.CerrarSesionUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.AutenticacionDomain;
import com.arquisoft.seguridad.domain.auth.SesionDomain;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
        when(autenticarUsuarioUseCase.ejecutar(any(AutenticacionDomain.class))).thenReturn(esperado);

        // Act
        AutenticacionResult resultado =
                new AutenticarUsuarioInteractorImpl(autenticarUsuarioUseCase).ejecutar(command);

        // Assert
        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void debeConstruirElTokenYDelegarEnElUseCase_cuandoRefrescar() {
        // Arrange
        RefrescoTokenResult esperado =
                new RefrescoTokenResult("access", "refresh", 300L, "Bearer", "openid");
        var captor = ArgumentCaptor.forClass(TokenDomain.class);
        when(refrescarTokenUseCase.ejecutar(any(TokenDomain.class))).thenReturn(esperado);

        // Act
        RefrescoTokenResult resultado = new RefrescarTokenInteractorImpl(refrescarTokenUseCase)
                .ejecutar(RefrescarTokenCommand.crear("token-refresco"));

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        verify(refrescarTokenUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getValor()).isEqualTo("token-refresco");
    }

    @Test
    void debeConstruirLaSesionYDelegarEnElUseCase_cuandoCerrarSesion() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);
        var captor = ArgumentCaptor.forClass(SesionDomain.class);

        // Act
        new CerrarSesionInteractorImpl(cerrarSesionUseCase).ejecutar(command);

        // Assert
        verify(cerrarSesionUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getIdentificadorToken()).isEqualTo("jti-123");
        assertThat(captor.getValue().getTiempoVidaRestante()).isEqualTo(120L);
    }

    @Test
    void debeConstruirElTokenYDelegarEnElUseCase_cuandoValidarToken() {
        // Arrange
        ValidacionTokenResult esperado =
                new ValidacionTokenResult.Valida("id-1", "test@example.com");
        var captor = ArgumentCaptor.forClass(TokenDomain.class);
        when(validarTokenUseCase.ejecutar(any(TokenDomain.class))).thenReturn(esperado);

        // Act
        ValidacionTokenResult resultado = new ValidarTokenInteractorImpl(validarTokenUseCase)
                .ejecutar(ValidarTokenCommand.crear("eyJhbGc..."));

        // Assert
        assertThat(resultado).isEqualTo(esperado);
        verify(validarTokenUseCase).ejecutar(captor.capture());
        assertThat(captor.getValue().getValor()).isEqualTo("eyJhbGc...");
    }
}

