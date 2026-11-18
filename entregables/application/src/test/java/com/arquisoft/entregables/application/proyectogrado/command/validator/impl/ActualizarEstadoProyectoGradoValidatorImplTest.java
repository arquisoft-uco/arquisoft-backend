package com.arquisoft.entregables.application.proyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstadoProyectoGradoValidatorImplTest {

    private final ActualizarEstadoProyectoGradoValidatorImpl validator = new ActualizarEstadoProyectoGradoValidatorImpl();

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
