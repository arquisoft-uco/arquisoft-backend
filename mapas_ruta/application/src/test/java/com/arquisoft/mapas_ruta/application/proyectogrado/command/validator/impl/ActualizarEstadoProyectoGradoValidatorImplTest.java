package com.arquisoft.mapas_ruta.application.proyectogrado.command.validator.impl;

import com.arquisoft.mapas_ruta.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstadoProyectoGradoValidatorImplTest {

    private final ActualizarEstadoProyectoGradoValidatorImpl validator = new ActualizarEstadoProyectoGradoValidatorImpl();

    @Test
    void debePasar_cuandoElProyectoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> validator.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoProyectoNoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(existencia))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class);
    }
}
