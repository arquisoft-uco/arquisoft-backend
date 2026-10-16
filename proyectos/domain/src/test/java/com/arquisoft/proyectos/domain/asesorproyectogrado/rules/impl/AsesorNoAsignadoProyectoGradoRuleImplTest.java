package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorYaAsignadoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.AsignacionVigenteAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorNoAsignadoProyectoGradoRuleImplTest {

    private final AsesorNoAsignadoProyectoGradoRuleImpl regla = new AsesorNoAsignadoProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElAsesorNoEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionVigenteAsesor(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(asignacion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorYaEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionVigenteAsesor(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(asignacion))
                .isInstanceOf(AsesorYaAsignadoException.class)
                .extracting("codigoError")
                .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_YA_ASIGNADO);
    }
}
