package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.exception.AutenticacionException;
import com.arquisoft.seguridad.domain.auth.AutenticacionDomain;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;
import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
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
class AutenticarUsuarioUseCaseImplTest {

    @Mock
    private AutenticacionOutputPort autenticacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AutenticarUsuarioUseCaseImpl autenticarUsuarioUseCase;

    @Test
    void debeRetornarCredenciales_cuandoAutenticacionEsExitosa() {
        // Arrange
        var entrada = autenticacionDe("test@example.com", "secreto");
        when(autenticacionOutputPort.autenticar("test@example.com", "secreto"))
                .thenReturn(new CredencialesProveedor("access-token", "refresh-token", 300L, "Bearer", "openid"));

        // Act
        AutenticacionResult resultado = autenticarUsuarioUseCase.ejecutar(entrada);

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
        var entrada = autenticacionDe("otro@example.com", "clave-larga");
        when(autenticacionOutputPort.autenticar("otro@example.com", "clave-larga"))
                .thenReturn(new CredencialesProveedor("a", "r", 60L, "Bearer", ""));

        // Act
        autenticarUsuarioUseCase.ejecutar(entrada);

        // Assert
        verify(autenticacionOutputPort).autenticar("otro@example.com", "clave-larga");
    }

    @Test
    void debePropagarExcepcion_cuandoCredencialesSonInvalidas() {
        // Arrange
        var entrada = autenticacionDe("test@example.com", "mala-clave");
        when(autenticacionOutputPort.autenticar("test@example.com", "mala-clave"))
                .thenThrow(new AutenticacionException("Credenciales invalidas"));

        // Act / Assert
        assertThatThrownBy(() -> autenticarUsuarioUseCase.ejecutar(entrada))
                .isInstanceOf(AutenticacionException.class);
    }

    private AutenticacionDomain autenticacionDe(String correo, String clave) {
        return AutenticacionDomain.crear(correo, clave);
    }
}
