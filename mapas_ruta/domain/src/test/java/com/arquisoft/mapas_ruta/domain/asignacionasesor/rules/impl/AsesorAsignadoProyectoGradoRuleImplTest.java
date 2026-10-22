package com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.impl;

import com.arquisoft.mapas_ruta.domain.asignacionasesor.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.model.AsignacionAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorAsignadoProyectoGradoRuleImplTest {

    private final AsesorAsignadoProyectoGradoRuleImpl rule = new AsesorAsignadoProyectoGradoRuleImpl();

    @Test
    void debeLanzar_cuandoNoEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(asignacion))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class)
                .hasFieldOrPropertyWithValue("codigoError", MapasRutaCodes.AsignacionAsesor.NO_ASIGNADO);
    }

    @Test
    void noDebeLanzar_cuandoEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(asignacion)).doesNotThrowAnyException();
    }
}
