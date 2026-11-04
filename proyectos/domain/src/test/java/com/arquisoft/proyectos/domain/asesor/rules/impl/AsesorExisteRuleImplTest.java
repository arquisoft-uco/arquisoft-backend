package com.arquisoft.proyectos.domain.asesor.rules.impl;

import com.arquisoft.proyectos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.proyectos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorExisteRuleImplTest {

    private final AsesorExisteRuleImpl regla = new AsesorExisteRuleImpl();

    @Test
    void debePasar_cuandoElAsesorExiste() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarAsesorNoEncontrado_cuandoElAsesorNoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(AsesorNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.Asesor.NO_ENCONTRADO);
    }
}
