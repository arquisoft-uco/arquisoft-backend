package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstudianteValidatorImplTest {

    private final AgregarEstudianteValidatorImpl validator = new AgregarEstudianteValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarEstudianteValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), EstudianteDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsEstudiante() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, EstudianteDomain.crear(usuario)))
                .isInstanceOf(EstudianteUsuarioDuplicadoException.class);
    }
}
