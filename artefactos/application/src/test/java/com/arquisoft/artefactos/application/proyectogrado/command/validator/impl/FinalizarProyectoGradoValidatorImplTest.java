package com.arquisoft.artefactos.application.proyectogrado.command.validator.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizarProyectoGradoValidatorImplTest {

    private final FinalizarProyectoGradoValidatorImpl validator = new FinalizarProyectoGradoValidatorImpl();

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia)).doesNotThrowAnyException();
    }
}
