package com.arquisoft.entregables.application.proyectogrado.command.validator.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizarProyectoGradoValidatorImplTest {

    private final FinalizarProyectoGradoValidatorImpl validator = new FinalizarProyectoGradoValidatorImpl();

    @Test
    void debePasarOLanzarNoEncontrado_segunLaExistenciaDelProyecto() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(new ExistenciaProyectoGrado(proyectoGrado, true)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> validator.validar(new ExistenciaProyectoGrado(proyectoGrado, false)))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
