package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.secondaryport.ValidacionTokenOutputPort;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarTokenUseCaseImplTest {

    @Mock
    private ValidacionTokenOutputPort validacionTokenOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ValidarTokenUseCaseImpl validarTokenUseCase;

    @Test
    void debeRetornarValido_cuandoTokenEsCorrecto() {
        // Arrange
        TokenDomain token = TokenDomain.crear("token-valido");
        when(validacionTokenOutputPort.extraerIdentidad("token-valido")).thenReturn(
                Optional.of(new IdentidadProveedor("id-1", "test@example.com", "Test", List.of("estudiante"))));

        // Act
        ValidacionTokenResult resultado = validarTokenUseCase.ejecutar(token);

        // Assert
        assertThat(resultado)
                .isInstanceOfSatisfying(ValidacionTokenResult.Valida.class, valida -> {
                    assertThat(valida.identidadId()).isEqualTo("id-1");
                    assertThat(valida.correo()).isEqualTo("test@example.com");
                });
    }

    @Test
    void debeRetornarInvalido_cuandoElTokenNoSePuedeDecodificar() {
        // Arrange
        TokenDomain token = TokenDomain.crear("token-invalido");
        when(validacionTokenOutputPort.extraerIdentidad("token-invalido")).thenReturn(Optional.empty());

        // Act
        ValidacionTokenResult resultado = validarTokenUseCase.ejecutar(token);

        // Assert — la variante Invalida no tiene campos, asi que no hay nulls que comprobar
        assertThat(resultado).isInstanceOf(ValidacionTokenResult.Invalida.class);
    }

    @Test
    void debePropagarExcepcionInesperada_cuandoFallaAlgoNoPrevisto() {
        // Arrange — el adaptador ya traduce el token invalido a Optional.empty(), asi que
        // una excepcion aqui solo puede ser un defecto y no debe disfrazarse de token invalido
        TokenDomain token = TokenDomain.crear("token-roto");
        when(validacionTokenOutputPort.extraerIdentidad("token-roto"))
                .thenThrow(new IllegalStateException("firma corrupta"));

        // Act / Assert
        assertThatThrownBy(() -> validarTokenUseCase.ejecutar(token))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("firma corrupta");
    }
}
