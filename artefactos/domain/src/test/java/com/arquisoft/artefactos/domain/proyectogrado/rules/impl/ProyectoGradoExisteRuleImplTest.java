package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoExisteRuleImplTest {

    private final ProyectoGradoExisteRuleImpl regla = new ProyectoGradoExisteRuleImpl();

    @Test
    void debeLanzar_cuandoElProyectoNoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(ProyectoGradoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.ProyectoGrado.NO_ENCONTRADO);
    }

    @Test
    void debePasar_cuandoElProyectoExiste() {
        // Arrange
        var existencia = new ExistenciaProyectoGrado(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
