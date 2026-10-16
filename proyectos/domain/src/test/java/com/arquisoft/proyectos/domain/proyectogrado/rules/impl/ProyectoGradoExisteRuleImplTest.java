package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoExisteRuleImplTest {

    private final ProyectoGradoExisteRuleImpl regla = new ProyectoGradoExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElProyectoNoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO);
    }

    @Test
    void debePasar_cuandoElProyectoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
