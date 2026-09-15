package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.usuarios.domain.asesor.exception.AsesorUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorValidatorImplTest {

    private final AgregarAsesorValidatorImpl validator = new AgregarAsesorValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarAsesorValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsAsesor() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, true))
                .isInstanceOf(AsesorUsuarioDuplicadoException.class);
    }
}
