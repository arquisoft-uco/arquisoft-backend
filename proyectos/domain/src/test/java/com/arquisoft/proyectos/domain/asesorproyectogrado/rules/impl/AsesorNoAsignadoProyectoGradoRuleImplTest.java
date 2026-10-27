package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorYaAsignadoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.AsignacionVigenteAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorNoAsignadoProyectoGradoRuleImplTest {

    private static final String NOMBRE_ASESOR = "Laura Gómez";

    private final AsesorNoAsignadoProyectoGradoRuleImpl regla = new AsesorNoAsignadoProyectoGradoRuleImpl();

    @Test
    void debePasar_cuandoElAsesorNoEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionVigenteAsesor(
                UtilUUID.generarNuevoUUID(), NOMBRE_ASESOR, UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(asignacion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorYaEstaAsignado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var asignacion = new AsignacionVigenteAsesor(asesor, NOMBRE_ASESOR, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(asignacion))
                .isInstanceOfSatisfying(AsesorYaAsignadoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_YA_ASIGNADO);
                    assertThat(exception.getError().getMessage())
                            .contains(NOMBRE_ASESOR)
                            .doesNotContain(asesor.toString());
                    assertThat(exception.getMessage()).contains(asesor.toString());
                });
    }
}
