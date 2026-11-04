package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorProyectoGradoExisteRuleImplTest {

    private final AsesorProyectoGradoExisteRuleImpl rule = new AsesorProyectoGradoExisteRuleImpl();

    @Test
    void debePasar_cuandoElVinculoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElVinculoNoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(AsesorProyectoGradoNoEncontradoException.class)
                .satisfies(ex -> assertThat(((AsesorProyectoGradoNoEncontradoException) ex).getCodigoError())
                        .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_PROYECTO_GRADO_NO_ENCONTRADO));
    }
}
