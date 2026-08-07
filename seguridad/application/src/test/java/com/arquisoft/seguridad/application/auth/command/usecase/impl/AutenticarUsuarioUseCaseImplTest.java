package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.seguridad.application.auth.command.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AutenticacionOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioUseCaseImplTest {

    @Mock
    private AutenticacionOutputPort autenticacionOutputPort;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private AutenticarUsuarioUseCaseImpl autenticarUsuarioUseCase;

    @Test
    void debeRetornarCredenciales_cuandoAutenticacionEsExitosa() {
        // Arrange
        AutenticarUsuarioCommand command = new AutenticarUsuarioCommand("test@example.com", "secreto");
        when(autenticacionOutputPort.autenticar("test@example.com", "secreto"))
                .thenReturn(CredencialesSesion.de("access-token", "refresh-token", 300L, "Bearer", "openid"));

        // Act
        AutenticacionResult resultado = autenticarUsuarioUseCase.ejecutar(command);

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
        AutenticarUsuarioCommand command = new AutenticarUsuarioCommand("otro@example.com", "clave");
        when(autenticacionOutputPort.autenticar("otro@example.com", "clave"))
                .thenReturn(CredencialesSesion.de("a", "r", 60L, "Bearer", ""));

        // Act
        autenticarUsuarioUseCase.ejecutar(command);

        // Assert
        verify(autenticacionOutputPort).autenticar("otro@example.com", "clave");
    }

    @Test
    void debePropagarExcepcion_cuandoCredencialesSonInvalidas() {
        // Arrange
        AutenticarUsuarioCommand command = new AutenticarUsuarioCommand("test@example.com", "mala");
        when(autenticacionOutputPort.autenticar("test@example.com", "mala"))
                .thenThrow(new AuthenticationException("Credenciales invalidas"));

        // Act / Assert
        assertThatThrownBy(() -> autenticarUsuarioUseCase.ejecutar(command))
                .isInstanceOf(AuthenticationException.class);
    }
}
