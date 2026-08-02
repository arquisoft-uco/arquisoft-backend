package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.port.out.TokenValidationOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateTokenUseCaseImplTest {

    @Mock
    private TokenValidationOutputPort tokenValidationOutputPort;

        // Catalogo real: el mensaje viaja en ValidacionTokenResult, no solo al log,
    // y un mock devolveria null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private ValidateTokenUseCaseImpl validateTokenUseCase;

    @Test
    void debeRetornarValido_cuandoTokenEsCorrecto() {
        // Arrange
        TokenAggregate token = TokenAggregate.de("token-valido");
        when(tokenValidationOutputPort.validarToken("token-valido")).thenReturn(true);
        when(tokenValidationOutputPort.extraerInfo("token-valido"))
                .thenReturn(IdentidadToken.de("id-1", "test@example.com", "Test", List.of("estudiante")));

        // Act
        ValidacionTokenResult resultado = validateTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isTrue();
        assertThat(resultado.identidadId()).isEqualTo("id-1");
        assertThat(resultado.correo()).isEqualTo("test@example.com");
        assertThat(resultado.mensaje()).isNotBlank();
    }

    @Test
    void debeRetornarInvalido_cuandoTokenNoPasaLaValidacion() {
        // Arrange
        TokenAggregate token = TokenAggregate.de("token-invalido");
        when(tokenValidationOutputPort.validarToken("token-invalido")).thenReturn(false);

        // Act
        ValidacionTokenResult resultado = validateTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isFalse();
        assertThat(resultado.identidadId()).isNull();
        assertThat(resultado.correo()).isNull();
        assertThat(resultado.mensaje()).isNotBlank();
    }

    @Test
    void debeRetornarInvalido_cuandoLaValidacionLanzaExcepcion() {
        // Arrange
        TokenAggregate token = TokenAggregate.de("token-roto");
        when(tokenValidationOutputPort.validarToken("token-roto"))
                .thenThrow(new IllegalStateException("firma corrupta"));

        // Act
        ValidacionTokenResult resultado = validateTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isFalse();
        assertThat(resultado.mensaje()).contains("firma corrupta");
    }
}
