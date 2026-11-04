package com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoProyectoGradoExisteRuleImplTest {

    private final EstadoProyectoGradoExisteRuleImpl regla = new EstadoProyectoGradoExisteRuleImpl();

    @Test
    void debePasar_cuandoElProyectoTieneEstadoVigente() {
        // Arrange
        var existencia = new ExistenciaEstadoProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEstadoProyectoGradoNoEncontrado_cuandoElProyectoNoTieneEstado() {
        // Arrange
        var existencia = new ExistenciaEstadoProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EstadoProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.EstadoProyectoGrado.NO_ENCONTRADO);
    }
}
