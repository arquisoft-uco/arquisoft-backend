package com.arquisoft.mapas_ruta.domain.asignacionasesor.rules.impl;

import com.arquisoft.mapas_ruta.domain.asignacionasesor.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.mapas_ruta.domain.asignacionasesor.model.AsignacionAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorAsignadoProyectoGradoRuleImplTest {

    private static final String TITULO_PROYECTO = "Sistema de gestión académica";

    private final AsesorAsignadoProyectoGradoRuleImpl rule = new AsesorAsignadoProyectoGradoRuleImpl();

    @Test
    void debeLanzar_cuandoNoEstaAsignado() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var asignacion = new AsignacionAsesorProyectoGrado(proyectoGrado, TITULO_PROYECTO, asesor, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(asignacion))
                .isInstanceOfSatisfying(AsesorNoAsignadoProyectoGradoException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError()).isEqualTo(MapasRutaCodes.AsignacionAsesor.NO_ASIGNADO);
                    assertThat(excepcion.getError().getMessage())
                            .contains(TITULO_PROYECTO)
                            .doesNotContain(proyectoGrado.toString())
                            .doesNotContain(asesor.toString());
                    assertThat(excepcion.getMessage())
                            .contains(proyectoGrado.toString())
                            .contains(asesor.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoEstaAsignado() {
        // Arrange
        var asignacion = new AsignacionAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), TITULO_PROYECTO, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(asignacion)).doesNotThrowAnyException();
    }
}
