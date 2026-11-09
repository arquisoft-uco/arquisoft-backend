package com.arquisoft.artefactos.application.paqueteentregable.command.validator.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarPaqueteEntregableValidatorImplTest {

    private final GenerarPaqueteEntregableValidatorImpl validator = new GenerarPaqueteEntregableValidatorImpl();

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(proyecto)).isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var proyecto = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> validator.validar(proyecto)).doesNotThrowAnyException();
    }
}
