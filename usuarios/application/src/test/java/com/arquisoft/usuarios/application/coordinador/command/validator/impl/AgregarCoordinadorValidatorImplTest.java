package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarCoordinadorValidatorImplTest {

    private final AgregarCoordinadorValidatorImpl validator = new AgregarCoordinadorValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarCoordinadorValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsCoordinador() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, true))
                .isInstanceOf(CoordinadorUsuarioDuplicadoException.class);
    }
}
