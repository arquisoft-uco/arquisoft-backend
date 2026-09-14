package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorFichaValidatorImplTest {

    private final AgregarAsesorFichaValidatorImpl validator = new AgregarAsesorFichaValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarAsesorFichaValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), false))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsAsesorFicha() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, true))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class);
    }
}
