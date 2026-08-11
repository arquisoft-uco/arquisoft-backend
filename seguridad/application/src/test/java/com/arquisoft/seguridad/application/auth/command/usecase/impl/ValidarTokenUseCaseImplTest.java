package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
import com.arquisoft.seguridad.domain.auth.secondaryport.ValidacionTokenOutputPort;
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
class ValidarTokenUseCaseImplTest {

    @Mock
    private ValidacionTokenOutputPort validacionTokenOutputPort;

        // Catalogo real: el mensaje viaja en ValidacionTokenResult, no solo al log,
    // y un mock devolveria null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private ValidarTokenUseCaseImpl validarTokenUseCase;

    @Test
    void debeRetornarValido_cuandoTokenEsCorrecto() {
        // Arrange
        TokenDomain token = TokenDomain.de("token-valido");
        when(validacionTokenOutputPort.validarToken("token-valido")).thenReturn(true);
        when(validacionTokenOutputPort.extraerInfo("token-valido"))
                .thenReturn(IdentidadToken.de("id-1", "test@example.com", "Test", List.of("estudiante")));

        // Act
        ValidacionTokenResult resultado = validarTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isTrue();
        assertThat(resultado.identidadId()).isEqualTo("id-1");
        assertThat(resultado.correo()).isEqualTo("test@example.com");
        assertThat(resultado.mensaje()).isNotBlank();
    }

    @Test
    void debeRetornarInvalido_cuandoTokenNoPasaLaValidacion() {
        // Arrange
        TokenDomain token = TokenDomain.de("token-invalido");
        when(validacionTokenOutputPort.validarToken("token-invalido")).thenReturn(false);

        // Act
        ValidacionTokenResult resultado = validarTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isFalse();
        assertThat(resultado.identidadId()).isNull();
        assertThat(resultado.correo()).isNull();
        assertThat(resultado.mensaje()).isNotBlank();
    }

    @Test
    void debeRetornarInvalido_cuandoLaValidacionLanzaExcepcion() {
        // Arrange
        TokenDomain token = TokenDomain.de("token-roto");
        when(validacionTokenOutputPort.validarToken("token-roto"))
                .thenThrow(new IllegalStateException("firma corrupta"));

        // Act
        ValidacionTokenResult resultado = validarTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado.valido()).isFalse();
        assertThat(resultado.mensaje()).contains("firma corrupta");
    }
}
